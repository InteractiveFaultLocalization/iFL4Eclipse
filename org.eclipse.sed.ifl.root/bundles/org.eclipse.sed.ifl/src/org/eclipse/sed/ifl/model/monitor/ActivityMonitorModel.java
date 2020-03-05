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
	
	private String macAddress;
	private String userId;
	private String scenarioId;
	
	
	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	@Override
	public void init() {
		g = traversal().withRemote(DriverRemoteConnection.using("localhost", 8182, "g"));
		super.init();
	}
	
	public void insertEvent(Event event) {
		synchronized (ActivityMonitorModel.class) {
			Vertex lastEvent = findLastEvent();
			Vertex newEvent = event.createNode(g);
			System.out.println("new event vertex: " + newEvent);
			Vertex idNode = g.V().hasLabel("id").has("mac", macAddress).has("userID", userId).has("scenarioID", scenarioId).next();
			System.out.println("id vertex of new event: " + idNode);
			Edge doneBy = g.V(newEvent).addE("doneBy").to(idNode).next();
			System.out.println("new event linked to id: " + doneBy);
			if(lastEvent != null) {
				Edge arrowOfTime = g.V(lastEvent).addE("follow").to(newEvent).next();
				System.out.println("new event linked to the chain: " + arrowOfTime);
			}
		}
	}

	public Vertex insertId(IdNode idNode) {
		synchronized (ActivityMonitorModel.class) {
			Vertex id = idNode.createNode(g);
			System.out.println("new ID node created: " + idNode);
			return id;
		}
	}
		
	private Vertex findLastEvent() {
		synchronized (ActivityMonitorModel.class) {
			Vertex idNode = null;
			List<Vertex> idNodes = g.V().hasLabel("id").has("mac", macAddress).has("userID", userId).has("scenarioID", scenarioId).toList();
			System.out.println("id list size: " + idNodes.size());
			if(!idNodes.isEmpty()) {
				idNode = idNodes.get(0);
			} else {
				idNode = insertId(new IdNode(macAddress, userId, scenarioId));
				System.out.println("new id node created: " + idNode);
				return null;
			}
			Vertex lastEvent = g.V(idNode).in().not(__.out("follow")).next();
			return lastEvent;
		}
	}
	
	@Override
	public void teardown() {
		EU.tryUnchecked(g::close);
		super.teardown();
	}
}
