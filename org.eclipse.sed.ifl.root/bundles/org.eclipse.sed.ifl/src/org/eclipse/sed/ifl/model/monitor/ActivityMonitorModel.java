package org.eclipse.sed.ifl.model.monitor;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

import java.util.List;

import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.sed.ifl.util.exception.EU;

public class ActivityMonitorModel extends EmptyModel {
	private GraphTraversalSource g;
	
	@Override
	public void init() {
		g = traversal().withRemote(DriverRemoteConnection.using("localhost", 8182, "g"));
		super.init();
	}
	
	public void insertEvent(Event event) {
		synchronized (ActivityMonitorModel.class) {
			Vertex lastEvent = findLastEvent();
			Vertex newEvent = event.createNode(g);
			Edge arrowOfTime = g.V(lastEvent).addE("follow").to(newEvent).next();
			System.out.println("new event linked to the chain: " + arrowOfTime);
		}
	}

	public void insertId(IdNode idNode) {
		synchronized (ActivityMonitorModel.class) {
			if(!idExists(idNode)) {
				idNode.createNode(g);
				System.out.println("new ID node created: " + idNode);
			}
		}
	}
	
	private boolean idExists(IdNode idNode) {
		synchronized(ActivityMonitorModel.class) {
			boolean rValue = false;
			
			return rValue;
		}
	}
	
	private Vertex findLastEvent() {
		synchronized (ActivityMonitorModel.class) {
			List<Vertex> lastEvents = g.V().hasLabel("event").not(__.out("follow")).toList();
			Vertex lastEvent;
			if (lastEvents.size() == 1) {
				lastEvent = lastEvents.get(0);
			}
			else {
				if (!lastEvents.isEmpty()) {
					g.V().drop().toList();
				}
				lastEvent = g.addV("event").property("type", "init").next();
			}
			return lastEvent;
		}
	}
	
	@Override
	public void teardown() {
		EU.tryUnchecked(g::close);
		super.teardown();
	}
}
