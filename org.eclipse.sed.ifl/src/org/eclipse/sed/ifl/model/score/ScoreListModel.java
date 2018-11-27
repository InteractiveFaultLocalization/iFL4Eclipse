package org.eclipse.sed.ifl.model.score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
	
	public void updateScore(Collection<Map<IMethodDescription, Defineable<Double>>> buckets) {
		scores.clear();
		for (var bucket : buckets) {
			scores.putAll(bucket);
		}
	}
	
	//TODO: for testing only
	public List<Entry<IMethodDescription, Defineable<Double>>> getRandomMethods(int count) {
		var keyList = new ArrayList<>(scores.entrySet());
		Collections.shuffle(keyList);
		return keyList.stream().limit(Math.min(keyList.size(), count)).collect(Collectors.toList());
	}
}
