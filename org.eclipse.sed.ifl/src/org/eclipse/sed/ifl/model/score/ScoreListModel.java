package org.eclipse.sed.ifl.model.score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class ScoreListModel extends EmptyModel {
	public ScoreListModel(Iterable<IMethodDescription> methods) {
		for (var method : methods) {
			scores.put(method, new Score());
		}
	}

	private Map<IMethodDescription, Score> scores = new HashMap<>();

	public Map<IMethodDescription, Defineable<Double>> getScores() {
		return Collections.unmodifiableMap(scores);
	}

	public void updateScore(Map<IMethodDescription, Defineable<Double>> newScores) {
		for (var score : newScores.entrySet()) {
			Score oldScore = scores.get(score.getKey());
			if (oldScore == null) {
				scores.put(score.getKey(), new Score(score.getValue()));
			} else {
				if (score.getValue().isDefinit()) {
					oldScore.setValue(score.getValue().getValue());
				} else {
					oldScore.undefine();
				}
			}
		}
		scoresUpdated.invoke(getScores());
	}

	/**
	 * @deprecated for testing only
	 */
	@Deprecated
	public List<Entry<IMethodDescription, Defineable<Double>>> getRandomMethods(int count) {
		var keyList = new ArrayList<>(scores.entrySet());
		Collections.shuffle(keyList);
		return keyList.stream().limit(Math.min(keyList.size(), count)).collect(Collectors.toList());
	}

	private NonGenericListenerCollection<Map<IMethodDescription, Defineable<Double>>> scoresUpdated = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Map<IMethodDescription, Defineable<Double>>> eventScoreUpdated() {
		return scoresUpdated;
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
