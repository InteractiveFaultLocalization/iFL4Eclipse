# python log-sanity.py -is "ws://localhost:8182/gremlin" -its "g"

import pdb

from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process import anonymous_traversal
from gremlin_python.process.graph_traversal import __
from gremlin_python.process.traversal import P

import argparse
import logging

if __name__ == '__main__':
    clap = argparse.ArgumentParser(
        description='This script checks the senity of the log database in some degree.')
    clap.add_argument(
        '-is', '--input-server',
        dest='input_server',
        action='store',
        help='URL of the input Gremlin server',
        required=True)
    clap.add_argument(
        '-its', '--input-source',
        dest='input_source',
        action='store',
        help='name of the input Gremlin traversal source',
        required=True)
    clap.add_argument(
        "-l", "--log",
        default='INFO',
        dest="log_level",
        choices=['DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL'],
        help="set the logging level",
        required=False)

    clargs = clap.parse_args()

    logger = logging.getLogger(__name__)
    logger.setLevel(clargs.log_level)
    handler = logging.FileHandler('sanity.log', mode='w')
    handler.setFormatter(logging.Formatter('%(asctime)s - %(levelname)s - %(message)s'))
    logger.addHandler(handler)

    logger.info("subject Gremlin server: %s, %s" % (clargs.input_server, clargs.input_source))
    graph = anonymous_traversal.traversal().withRemote(DriverRemoteConnection(clargs.input_server, clargs.input_source))


    def percent_of(value, total):
        return (value / total) * 100


    def nice_percent(value, total):
        return '{} ({:.3f}%)'.format(value, percent_of(value, total))


    event_count = graph.V().hasLabel('event').count().next()
    logger.info("checking broken event chain: event with either incoming or outgoing follow edge")
    no_prev = graph.V().hasLabel('event').where(__.outE('follow')).not_(__.inE('follow')).count().next()
    no_next = graph.V().hasLabel('event').where(__.inE('follow')).not_(__.outE('follow')).count().next()
    logger.info("  (event)-[follow]-> {}".format(
        nice_percent(no_prev, event_count),
        nice_percent(no_next, event_count)))
    logger.info("  -[follow]->(event) {}".format(
        nice_percent(no_prev, event_count),
        nice_percent(no_next, event_count)))

    logger.info("checking unconnected events: event without follow edge")
    unconnected_event = graph.V().hasLabel('event').not_(__.inE('follow')).not_(__.outE('follow')).count().next()
    logger.info("  X-[follow]->(event)-[follow]-X {}".format(nice_percent(unconnected_event, event_count)))
    if unconnected_event > 0:
        logger.error("there are some unconnected events")
        per_types = graph \
            .V().hasLabel('event').not_(__.inE('follow')).not_(__.outE('follow')) \
            .groupCount().by('type').next()
        for msg in ['  {} of type {}'.format(count, node_type) for node_type, count in per_types.items()]:
            logger.info(msg)
        per_source = graph \
            .V() \
            .hasLabel('event').not_(__.inE('follow')).not_(__.outE('follow')) \
            .groupCount().by('source_name').next()
        for msg in ['  {} from source {}'.format(count, node_type) for node_type, count in per_source.items()]:
            logger.info(msg)

    logging.info("checking unconnected resources: resources not connected to event")
    unconnected_resource = graph.V().hasLabel('resource').not_(__.inE().outV().hasLabel('event')).count().next()
    if unconnected_resource > 0:
        logger.error("there are some unconnected resources")
        per_types = graph \
            .V().hasLabel('resource').not_(__.inE().outV().hasLabel('event')) \
            .groupCount().by('type').next()
        for msg in ['  {} of type {}'.format(count, node_type) for node_type, count in per_types.items()]:
            logger.info(msg)
        per_source = graph \
            .V().hasLabel('resource').not_(__.inE().outV().hasLabel('event')) \
            .groupCount().by('source_name').next()
        for msg in ['  {} from source {}'.format(count, node_type) for node_type, count in per_source.items()]:
            logger.info(msg)
