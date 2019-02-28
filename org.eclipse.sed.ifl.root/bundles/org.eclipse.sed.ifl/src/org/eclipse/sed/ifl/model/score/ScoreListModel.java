package org.eclipse.sed.ifl.model.score;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class ScoreListModel extends EmptyModel {
	public ScoreListModel(Iterable<IMethodDescription> methods) {
		for (IMethodDescription method : methods) {
			scores.put(method, new Score(true));
		}
	}

	private Map<IMethodDescription, Score> scores = new HashMap<>();

	public Map<IMethodDescription, Score> getScores() {
		return Collections.unmodifiableMap(scores);
	}
	
	public Map<IMethodDescription, Defineable<Double>> getRawScore() {
		return getScores().entrySet().stream()
		.collect(Collectors.collectingAndThen(Collectors.toMap(e -> e.getKey(), e -> (Defineable<Double>)e.getValue()),Collections::unmodifiableMap));
	}

	public void updateScore(Map<IMethodDescription, Score> newScores) {
		for (Entry<IMethodDescription, Score> score : newScores.entrySet()) {
			Score oldScore = scores.get(score.getKey());
			if (oldScore == null) {
				scores.put(score.getKey(), score.getValue());
			} else {
				if (score.getValue().isDefinit()) {
					oldScore.setValue(score.getValue().getValue());
				} else {
					oldScore.undefine();
				}
			}
		}
		scoreUpdated.invoke(new EmptyEvent());
	}
	
	public int loadScore(Map<String, Score> rawScores) {
		int count = 0;
		Map<IMethodDescription, Score> entries = new HashMap<>();
		for (Entry<String, Score> raw : rawScores.entrySet()) {
			for (Entry<IMethodDescription, Score> entry : scores.entrySet()) {
				if (entry.getKey().getId().toCSVKey().equals(raw.getKey())) {
					entries.put(entry.getKey(), raw.getValue());
					count++;
				}
			}
		}
		updateScore(entries);
		System.out.println(count + "/" + scores.size() + " scores will be updated");
		return count;
	}

	private NonGenericListenerCollection<EmptyEvent> scoreUpdated = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<EmptyEvent> eventScoreUpdated() {
		return scoreUpdated;
	}
}
