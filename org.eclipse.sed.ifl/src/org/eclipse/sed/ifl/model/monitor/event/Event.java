package org.eclipse.sed.ifl.model.monitor.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.sed.ifl.model.monitor.Node;
import org.eclipse.sed.ifl.model.monitor.resource.Resource;

public abstract class Event extends Node {
	@Override
	protected final String getLabel() {
		return "event";
	}
	
	@Override
	public Vertex createNode(GraphTraversalSource g) {
		Vertex eventNode = super.createNode(g);
		for (var resource : createResources(g).entrySet()) {
			g.V(eventNode).addE("related").to(resource.getKey().createNode(g)).next();
		}
		return eventNode;
	}
	
	protected Map<Resource, String> createResources(GraphTraversalSource g) {
		return new HashMap<>();
	}
}
