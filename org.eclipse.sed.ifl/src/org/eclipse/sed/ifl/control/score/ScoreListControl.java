package org.eclipse.sed.ifl.control.score;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.ScoreListView;
import org.eclipse.sed.ifl.view.ScoreListView.ScoreStatus;
import org.eclipse.sed.ifl.view.ScoreListView.ScoreStatus;

public class ScoreListControl extends Control<ScoreListModel, ScoreListView> {

	public ScoreListControl(ScoreListModel model, ScoreListView view) {
		super(model, view);
	}
	
	@Override
	public void init() {
		getView().refreshScores(getModel().getScores(), ScoreStatus.UNDEFINED);
		super.init();
	}
	
	//TODO: finish this. 
	public void updateScore(Map<IMethodDescription, Defineable<Double>> scores) {
		Map<IMethodDescription, Defineable<Double>> higherScores = new HashMap<IMethodDescription, Defineable<Double>>();
		Map<IMethodDescription, Defineable<Double>> lowerScores = new HashMap<IMethodDescription, Defineable<Double>>();
		Map<IMethodDescription, Defineable<Double>> otherScores = new HashMap<IMethodDescription, Defineable<Double>>();
		for (var newEntry : scores.entrySet()) {
			var current = getModel().getScores().get(newEntry.getKey());
			if (current != null) {
				if (current.isDefinit() && newEntry.getValue().isDefinit()) {
					if (current.getValue() < newEntry.getValue().getValue()) {
						higherScores.put(newEntry.getKey(), newEntry.getValue());
					} else if (current.getValue() > newEntry.getValue().getValue()) {
						lowerScores.put(newEntry.getKey(), newEntry.getValue());
					} else {
						otherScores.put(newEntry.getKey(), newEntry.getValue());
					}
				} else {
					otherScores.put(newEntry.getKey(), newEntry.getValue());
				}
			} else {
				otherScores.put(newEntry.getKey(), newEntry.getValue());
			}
		}
		Map<ScoreStatus, Map<IMethodDescription, Defineable<Double>>> buckets = Map.ofEntries(
				Map.entry(ScoreStatus.INCREASED, higherScores),
				Map.entry(ScoreStatus.DECREASED, lowerScores),
				Map.entry(ScoreStatus.INCREASED, otherScores)
		);
	}
}
