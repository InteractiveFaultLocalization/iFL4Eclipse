package org.eclipse.sed.ifl.model.monitor.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.sed.ifl.model.monitor.IncorrectGraphState;
import org.eclipse.sed.ifl.model.monitor.Node;

public abstract class Resource extends Node {
	@Override
	protected final String getLabel() {
		return "resource";
	}

	private String id;

	public Resource(String id, Map<String, Object> properties) {
		super(Stream.concat(properties.entrySet().stream(), new HashMap<String, Object>() {
			{
				put("id", id);
			}
		}.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		this.id = id;
	}

	@Override
	public Vertex createNode(GraphTraversalSource g) {
		List<Vertex> candidates = g.V().where(__.properties("id").value().is(id)).toList();
		if (candidates.isEmpty()) {
			return super.createNode(g);
		} else {
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else {
				throw new IncorrectGraphState();
			}
		}
	}
}
