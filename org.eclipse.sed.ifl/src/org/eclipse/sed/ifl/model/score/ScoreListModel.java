package org.eclipse.sed.ifl.model.score;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

	public Map<IMethodDescription, Score> getScores() {
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
		scoreUpdated.invoke(getScores());
	}

	private NonGenericListenerCollection<Map<IMethodDescription, Score>> scoreUpdated = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Map<IMethodDescription, Score>> eventScoreUpdated() {
		return scoreUpdated;
	}
}
