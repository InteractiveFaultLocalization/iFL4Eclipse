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
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class ScoreListModel extends EmptyModel {
	public ScoreListModel(Iterable<IMethodDescription> methods) {
		for (var method : methods) {
			scores.put(method, new Defineable<Double>());
		}
	}

	private Map<IMethodDescription, Defineable<Double>> scores = new HashMap<>();

	public Map<IMethodDescription, Defineable<Double>> getScores() {
		Map<IMethodDescription, Defineable<Double>> scores2 = scores;
		return Collections.unmodifiableMap(scores);
	}

	public void updateScore(Collection<Map<IMethodDescription, Defineable<Double>>> buckets) {
		scores.clear();
		for (var bucket : buckets) {
			scores.putAll(bucket);
		}
	}

	// TODO: for testing only
	public List<Entry<IMethodDescription, Defineable<Double>>> getRandomMethods(int count) {
		var keyList = new ArrayList<>(scores.entrySet());
		Collections.shuffle(keyList);
		return keyList.stream().limit(Math.min(keyList.size(), count)).collect(Collectors.toList());
	}

	private NonGenericListenerCollection<Map<IMethodDescription, Defineable<Double>>> scoreUpdateRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Map<IMethodDescription, Defineable<Double>>> eventScoreUpdateRequested() {
		return scoreUpdateRequested;
	}

	public int requestScoreUpdate(Map<String, Double> rawScores) {
		int count = 0;
		Map<IMethodDescription, Defineable<Double>> entries = new HashMap<>();
		for (var raw : rawScores.entrySet()) {
			for (var entry : scores.entrySet()) {
				if (entry.getKey().getId().toCSVKey().equals(raw.getKey())) {
					entries.put(entry.getKey(), new Defineable<>(raw.getValue()));
					count++;
				}
			}
		}
		scoreUpdateRequested.invoke(entries);
		System.out.println(count + "/" + scores.size() + " scores will be updated");
		return count;
	}
}
