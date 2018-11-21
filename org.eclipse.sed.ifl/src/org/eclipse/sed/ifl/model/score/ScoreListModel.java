package org.eclipse.sed.ifl.model.score;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class ScoreListModel extends EmptyModel {
	public ScoreListModel(Iterable<IMethodDescription> methods) {
		for (var method : methods) {
			scores.put(method, new Defineable<Double>());
		}
	}
	
	private Map<IMethodDescription, Defineable<Double>> scores = new HashMap<>();
	
	public Map<IMethodDescription, Defineable<Double>> getScores() {
		return Collections.unmodifiableMap(scores);
	}
}
