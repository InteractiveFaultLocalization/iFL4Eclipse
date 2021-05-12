package org.eclipse.sed.ifl.model.monitor.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.sed.ifl.model.monitor.Node;
import org.eclipse.sed.ifl.model.monitor.resource.Resource;
import org.eclipse.sed.ifl.util.profile.NanoWatch;

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
		NanoWatch watch = new NanoWatch("creating event node and resources and edges between them");
		Vertex eventNode = super.createNode(g);
		GraphTraversal<Vertex, Vertex> t = g.V(eventNode);
		for (Entry<Resource, String> resource : resources.entrySet()) {
			t
			.addE("related").to(resource.getKey().createNodeTraversal(g))
			.property("role", resource.getValue()).outV();
		}
		System.out.println("gremlin query hossza: " + t.toString().length());
		t.next();
		System.out.println(watch);
		System.out.println(eventNode);
		//System.out.println("new event outEs: " + g.V(eventNode).outE().next());
		return eventNode;
	}
	//nanowatch
	/*
	public Vertex createNode(GraphTraversalSource g) {
		NanoWatch watch = new NanoWatch("creating event node and resources and edges between them");
		Vertex eventNode = super.createNode(g);
		for (Entry<Resource, String> resource : resources.entrySet()) {
			g.V(eventNode)
			.addE("related").to(resource.getKey().createNode(g))
			.property("role", resource.getValue())
			//.next();
			.iterate();
		}
		System.out.println(watch);
		return eventNode;
	}
	*/
	protected Map<Resource, String> resources = new HashMap<>();

	@Override
	public String toString() {
		return String.format("Event [getType()=%s, getCreation()=%s]", getType(), getCreation());
	}
	
	public Vertex getIdNode(GraphTraversalSource g) {
		List <Vertex> idNode = g.V(this).out().hasLabel("id").toList();
		if(!idNode.isEmpty()) {
			return idNode.get(0);
		} else {
		return null;
		}
	}
	
}
