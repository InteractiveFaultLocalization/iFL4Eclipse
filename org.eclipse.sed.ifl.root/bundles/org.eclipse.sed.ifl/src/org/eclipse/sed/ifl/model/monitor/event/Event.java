package org.eclipse.sed.ifl.model.monitor.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.sed.ifl.model.monitor.Node;
import org.eclipse.sed.ifl.model.monitor.resource.Resource;

public abstract class Event extends Node {
	public Event(Map<String, Object> properties) {
		super(properties);
	}

	@Override
	protected final String getLabel() {
		return "event";
	}
	
	@Override
	public Vertex createNode(GraphTraversalSource g) {
		Vertex eventNode = super.createNode(g);
		for (Entry<Resource, String> resource : resources.entrySet()) {
			g.V(eventNode)
			.addE("related").to(resource.getKey().createNode(g))
			.property("role", resource.getValue())
			.next();
		}
		return eventNode;
	}
	
	protected Map<Resource, String> resources = new HashMap<>();

	@Override
	public String toString() {
		return String.format("Event [getType()=%s, getCreation()=%s]", getType(), getCreation());
	}
	
	
}
