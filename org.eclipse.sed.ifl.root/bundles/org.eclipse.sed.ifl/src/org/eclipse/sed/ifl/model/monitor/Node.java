package org.eclipse.sed.ifl.model.monitor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public abstract class Node {
	protected abstract String getLabel();
	
	private LocalDateTime creation = LocalDateTime.now();
	
	public LocalDateTime getCreation() {
		return creation;
	}
	
	private Map<String, Object> properties = new HashMap<>();
	
	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public Node(Map<String, Object> properties) {
		super();
		this.properties.putAll(properties);
	}
	
	public GraphTraversal<Vertex, Vertex> createNodeTraversal(GraphTraversalSource g) {
		GraphTraversal<Vertex, Vertex> t = g.addV(getLabel()).property("type", getType()).property("created", getCreation().toString());
		for (Entry<String, Object> property : getProperties().entrySet()) {
			t.property(property.getKey(), property.getValue().toString());
		}
		return t;
	}
	
	public Vertex createNode(GraphTraversalSource g) {
		GraphTraversal<Vertex, Vertex> t = g.addV(getLabel()).property("type", getType()).property("created", getCreation().toString());
		for (Entry<String, Object> property : getProperties().entrySet()) {
			t.property(property.getKey(), property.getValue().toString());
		}
		return t.next();
	}

	protected final String getType() {
		return this.getClass().getSimpleName();
	}
}
