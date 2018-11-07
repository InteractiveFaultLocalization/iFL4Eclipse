package org.eclipse.sed.ifl.model.project;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class ProjectModel extends EmptyModel {
	public ProjectModel(Iterable<IMethodDescription> methods) {
		for (var method : methods) {
			scores.put(method, new Defineable<Double>(0.0));
		}
	}
	
	private Map<IMethodDescription, Defineable<Double>> scores = new HashMap<>();
	
	public Map<IMethodDescription, Defineable<Double>> getScores() {
		return Collections.unmodifiableMap(scores);
	}
}
