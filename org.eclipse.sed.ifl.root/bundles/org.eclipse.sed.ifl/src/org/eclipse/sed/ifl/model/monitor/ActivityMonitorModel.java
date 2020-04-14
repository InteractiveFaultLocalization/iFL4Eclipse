package org.eclipse.sed.ifl.model.monitor;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;


import java.util.List;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.sed.ifl.util.exception.EU;

public class ActivityMonitorModel extends EmptyModel {
	private GraphTraversalSource g;
	
	public GraphTraversalSource getG() {
		return g;
	}

	public void setG() {
		g = traversal().withRemote(DriverRemoteConnection.using(hostName, Integer.parseInt(portNumber), "g"));
	}

	private String macAddress;
	private String userId;
	private String scenarioId;
	private String hostName;
	private String portNumber;
	private String generatedId;
	
	public String getGeneratedId() {
		return generatedId;
	}
	
	public void setGeneratedId(String generatedId) {
		this.generatedId = generatedId;
	}
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

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
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String host = store.getString("host");
		String port = store.getString("port");
		if(!(host.equals("") && port.equals(""))) {
			g = traversal().withRemote(DriverRemoteConnection.using(host, Integer.parseInt(port), "g"));
		}
		super.init();
	}
	
	public void insertEvent(Event event) {
		synchronized (ActivityMonitorModel.class) {
			Vertex lastEvent = findLastEvent();
			Vertex newEvent = event.createNode(g);
			System.out.println("new event vertex: " + newEvent);
			Vertex idNode;
			if(!Activator.getDefault().getPreferenceStore().getString("macAddress").equals("")) {
				idNode = g.V().hasLabel("id").has("mac", macAddress).has("userID", userId).has("scenarioID", scenarioId).next();
			} else {
				idNode = g.V().hasLabel("id").has("mac", Activator.getDefault().getPreferenceStore().getString("ipAddress")).has("userID", userId).has("scenarioID", scenarioId).next();
			}
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
			if(Activator.getDefault().getPreferenceStore().getString("macAddress").equals("")) {
				macAddress = Activator.getDefault().getPreferenceStore().getString("ipAddress");
			}
			List<Vertex> idNodes = g.V().hasLabel("id").has("mac", macAddress).has("userID", userId).has("scenarioID", scenarioId).toList();
			System.out.println("id list size: " + idNodes.size());
			if(!idNodes.isEmpty()) {
				idNode = idNodes.get(0);
			} else {
				idNode = insertId(new IdNode(macAddress, userId, scenarioId, generatedId));
				System.out.println("new id node created: " + idNode);
				return null;
			}
			Vertex lastEvent = g.V(idNode).in().not(__.out("follow")).next();
			return lastEvent;
		}
	}
	
	@Override
	public void teardown() {
		if (g != null) {
			EU.tryUnchecked(g::close);
		}
		super.teardown();
	}
}
