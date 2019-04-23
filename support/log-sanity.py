# python log-sanity.py -is "ws://localhost:8182/gremlin" -its "g"

import pdb

from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process import anonymous_traversal
from gremlin_python.process.graph_traversal import __
from gremlin_python.process.traversal import P

import argparse
import logging


def non_root_logger():
    for handler in logging.root.handlers:
        logging.root.removeHandler(handler)
    return logging.getLogger(__name__)


if __name__ == '__main__':
    clap = argparse.ArgumentParser(
        description='This script checks the sanity of the log database in some degree.')
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

    non_root_logger().setLevel(logging.getLevelName(clargs.log_level))
    log_format = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')

    log_file_handler = logging.FileHandler('sanity.log', mode='w')
    log_file_handler.setFormatter(log_format)
    log_file_handler.setLevel(logging.DEBUG)
    non_root_logger().addHandler(log_file_handler)

    console_log_handler = logging.StreamHandler()
    console_log_handler.setLevel(logging.getLevelName(clargs.log_level))
    console_log_handler.setFormatter(log_format)
    non_root_logger().addHandler(console_log_handler)

    non_root_logger().info("subject Gremlin server: %s, %s" % (clargs.input_server, clargs.input_source))
    graph = anonymous_traversal.traversal().withRemote(DriverRemoteConnection(clargs.input_server, clargs.input_source))

    def percent_of(value, total):
        return (value / total) * 100


    def nice_percent(value, total):
        return '{} ({:.3f}%)'.format(value, percent_of(value, total))

    event_count = graph.V().hasLabel('event').count().next()
    non_root_logger().info("checking broken event chain: event with either incoming or outgoing follow edge")
    no_prev = graph.V().hasLabel('event').where(__.outE('follow')).not_(__.inE('follow')).count().next()
    no_next = graph.V().hasLabel('event').where(__.inE('follow')).not_(__.outE('follow')).count().next()
    non_root_logger().info("  (event)-[follow]-> {}".format(
        nice_percent(no_prev, event_count),
        nice_percent(no_next, event_count)))
    non_root_logger().info("  -[follow]->(event) {}".format(
        nice_percent(no_prev, event_count),
        nice_percent(no_next, event_count)))

    non_root_logger().info("checking unconnected events: event without follow edge")
    unconnected_event = graph.V().hasLabel('event').not_(__.inE('follow')).not_(__.outE('follow')).count().next()
    non_root_logger().info("  X-[follow]->(event)-[follow]-X {}".format(nice_percent(unconnected_event, event_count)))
    if unconnected_event > 0:
        non_root_logger().error("there are some unconnected events")
        per_types = graph \
            .V().hasLabel('event').not_(__.inE('follow')).not_(__.outE('follow')) \
            .groupCount().by('type').next()
        for msg in ['  {} of type {}'.format(count, node_type) for node_type, count in per_types.items()]:
            non_root_logger().info(msg)
        per_source = graph \
            .V() \
            .hasLabel('event').not_(__.inE('follow')).not_(__.outE('follow')) \
            .groupCount().by('source_name').next()
        for msg in ['  {} from source {}'.format(count, node_type) for node_type, count in per_source.items()]:
            non_root_logger().info(msg)

    non_root_logger().info("checking unconnected resources: resources not connected to event")
    unconnected_resource = graph.V().hasLabel('resource').not_(__.inE().outV().hasLabel('event')).count().next()
    if unconnected_resource > 0:
        non_root_logger().error("there are some unconnected resources")
        per_types = graph \
            .V().hasLabel('resource').not_(__.inE().outV().hasLabel('event')) \
            .groupCount().by('type').next()
        for msg in ['  {} of type {}'.format(count, node_type) for node_type, count in per_types.items()]:
            non_root_logger().info(msg)
        per_source = graph \
            .V().hasLabel('resource').not_(__.inE().outV().hasLabel('event')) \
            .groupCount().by('source_name').next()
        for msg in ['  {} from source {}'.format(count, node_type) for node_type, count in per_source.items()]:
            non_root_logger().info(msg)
