package org.eclipse.sed.ifl.model.monitor.resource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.sed.ifl.model.monitor.IncorrectGraphState;
import org.eclipse.sed.ifl.model.monitor.Node;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.sed.ifl.util.profile.NanoWatch;

public abstract class Resource extends Node {
	@Override
	protected final String getLabel() {
		return "resource";
	}
	
	private String id;
	
	public Resource(String id, Map<String, Object> properties) {
		super(
			Stream.concat(
				properties.entrySet().stream(),
				Maps.<String, Object>builder().put("id", id).unmodifiable(true).build().entrySet().stream())
			.collect(Collectors.collectingAndThen(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue),Collections::unmodifiableMap)));
		this.id = id;
	}
	
	@Override
	public Vertex createNode(GraphTraversalSource g) {
		NanoWatch watch = new NanoWatch("finding resource candidates");
		List<Vertex> candidates = g.V().where(__.properties("id").value().is(id)).toList();
		System.out.println(watch);
		if (candidates.isEmpty()) {
			return super.createNode(g);
		}
		else {
			if (candidates.size() == 1) {
				return candidates.get(0);
			}
			else {
				throw new IncorrectGraphState();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public GraphTraversal<Vertex, Vertex> createNodeTraversal(GraphTraversalSource g) {
		NanoWatch watch = new NanoWatch("finding resource candidates");
		GraphTraversal<Vertex, Vertex> candidates = g.V().has("id", id).fold().coalesce(__.unfold(), super.createNodeTraversal(g));
		System.out.println(watch);
		return candidates;
		/*
		if (candidates.isEmpty()) {
			return super.createNodeTraversal(g);
		}
		else {
			if (candidates.size() == 1) {
				return g.V(candidates.get(0));
			}
			else {
				throw new IncorrectGraphState();
			}
		}*/
	}
}
