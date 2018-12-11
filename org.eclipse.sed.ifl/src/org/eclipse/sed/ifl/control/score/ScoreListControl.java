package org.eclipse.sed.ifl.control.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.ScoreListView;

public class ScoreListControl extends Control<ScoreListModel, ScoreListView> {

	public ScoreListControl(ScoreListModel model, ScoreListView view) {
		super(model, view);
	}
	
	@Override
	public void init() {
		getView().refreshScores(getModel().getScores(), ScoreStatus.UNDEFINED);
		getModel().eventScoreUpdateRequested().add(scoreUpdateRequestedListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		getModel().eventScoreUpdateRequested().remove(scoreUpdateRequestedListener);
		super.teardown();
	}
	
	public enum ScoreStatus {
		NONE (null),
		INCREASED ("icons/up32.png"),
		DECREASED ("icons/down32.png"),
		UNDEFINED ("icons/undefined32.png");
		
		private final String iconPath;
		
		ScoreStatus(String iconPath) {
			this.iconPath = iconPath;
		}
		
		public String getIconPath() {
			return iconPath;
		}
	}
	
	public void updateScore(Map<IMethodDescription, Defineable<Double>> newScores) {
		var buckets = detectStatus(newScores);
		getModel().updateScore(buckets.values());
		getView().refreshScores(buckets);
	}
	
	private IListener<Map<IMethodDescription, Defineable<Double>>> scoreUpdateRequestedListener = new IListener<>() {
		
		@Override
		public void invoke(Map<IMethodDescription, Defineable<Double>> event) {
			updateScore(event);
		}
	};

	private Map<ScoreStatus, Map<IMethodDescription, Defineable<Double>>> detectStatus(Map<IMethodDescription, Defineable<Double>> newScores) {
		Map<IMethodDescription, Defineable<Double>> higherScores = new HashMap<IMethodDescription, Defineable<Double>>();
		Map<IMethodDescription, Defineable<Double>> lowerScores = new HashMap<IMethodDescription, Defineable<Double>>();
		Map<IMethodDescription, Defineable<Double>> unchangedScores = new HashMap<IMethodDescription, Defineable<Double>>();
		Map<IMethodDescription, Defineable<Double>> otherScores = new HashMap<IMethodDescription, Defineable<Double>>();
		for (var current : getModel().getScores().entrySet()) {
			if (!newScores.containsKey(current.getKey())) {
				unchangedScores.put(current.getKey(), current.getValue());
			}
		}
		for (var newEntry : newScores.entrySet()) {
			var current = getModel().getScores().get(newEntry.getKey());
			if (current != null) {
				if (current.isDefinit() && newEntry.getValue().isDefinit()) {
					if (current.getValue() < newEntry.getValue().getValue()) {
						higherScores.put(newEntry.getKey(), newEntry.getValue());
					} else if (current.getValue() > newEntry.getValue().getValue()) {
						lowerScores.put(newEntry.getKey(), newEntry.getValue());
					} else {
						unchangedScores.put(newEntry.getKey(), newEntry.getValue());
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
				Map.entry(ScoreStatus.NONE, unchangedScores),
				Map.entry(ScoreStatus.UNDEFINED, otherScores)
		);
		return buckets;
	}

	//TODO: for testing only
	public void updateRandomScores(int count) {
		var methods = getModel().getRandomMethods(count);
		var statuses = List.of(ScoreStatus.DECREASED, ScoreStatus.INCREASED, ScoreStatus.NONE, ScoreStatus.UNDEFINED);
		Random r = new Random();
		Map<IMethodDescription, Defineable<Double>> newScores = new HashMap<>();
		for (var entry : methods) {
			var status = statuses.get(r.nextInt(statuses.size()));
			var value = new Defineable<>(0.0);
			if (entry.getValue().isDefinit()) {
				switch (status) {
				case DECREASED:
					value.setValue(entry.getValue().getValue() - 1);
					break;
				case INCREASED:
					value.setValue(entry.getValue().getValue() + 1);
					break;
				case NONE:
					value.setValue(entry.getValue().getValue());
					break;
				case UNDEFINED:
					value.undefine();
					break;
				default:
					throw new RuntimeException("wrong test");
				}
			}
			newScores.put(entry.getKey(), value);
			System.out.printf("Changing %s from %s to %s\n", entry.getKey().getId().toString(), entry.getValue().toString(), value.toString());
		}
		System.out.println("random score changes generated");
		updateScore(newScores);
	}
}
