package org.eclipse.sed.ifl.control.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.ScoreListControl.ScoreStatus;
import org.eclipse.sed.ifl.core.BasicIflMethodScoreHandler;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.identification.IUser;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.ScoreListView;

public class ScoreListControl extends Control<ScoreListModel, ScoreListView> {

	private BasicIflMethodScoreHandler handler = new BasicIflMethodScoreHandler(null);

	public ScoreListControl(ScoreListModel model, ScoreListView view) {
		super(model, view);
	}

	@Override
	public void init() {
		getView().refreshScores(getModel().getScores(), ScoreStatus.UNDEFINED);
		getModel().eventScoreUpdateRequested().add(scoreUpdateRequestedListener);
		getView().createOptionsMenu(handler.getProvidedOptions());
		getView().eventOptionSelected().add(optionSelectedListener);
		handler.eventScoreUpdated().add(scoreUpdatedListener);
		handler.loadMethodsScoreMap(getModel().getScores());
		super.init();
	}

	@Override
	public void teardown() {
		getModel().eventScoreUpdateRequested().remove(scoreUpdateRequestedListener);
		getView().eventOptionSelected().remove(optionSelectedListener);
		handler.eventScoreUpdated().remove(scoreUpdatedListener);
		super.teardown();
	}

	public enum ScoreStatus {
		NONE(null), INCREASED("icons/up32.png"), DECREASED("icons/down32.png"), UNDEFINED("icons/undefined32.png");

		private final String iconPath;

		ScoreStatus(String iconPath) {
			this.iconPath = iconPath;
		}

		public String getIconPath() {
			return iconPath;
		}
	}

	public void updateScore(Map<IMethodDescription, Defineable<Double>> newScores) {
		var<ScoreStatus, Map<IMethodDescription, Defineable<Double>>> buckets = detectStatus(newScores);
		getModel().updateScore(buckets.values());
		handler.loadMethodsScoreMap(getModel().getScores());
		if (hideUndefinedScores) {
			getView().refreshScores(buckets.entrySet().stream().filter(entry -> entry.getKey() != ScoreStatus.UNDEFINED)
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
		} else {
			getView().refreshScores(buckets);
		}
	}

	private IListener<Map<IMethodDescription, Defineable<Double>>> scoreUpdateRequestedListener = new IListener<>() {

		@Override
		public void invoke(Map<IMethodDescription, Defineable<Double>> event) {
			updateScore(event);
		}
	};

	private Map<ScoreStatus, Map<IMethodDescription, Defineable<Double>>> detectStatus(
			Map<IMethodDescription, Defineable<Double>> newScores) {
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
				Map.entry(ScoreStatus.INCREASED, higherScores), Map.entry(ScoreStatus.DECREASED, lowerScores),
				Map.entry(ScoreStatus.NONE, unchangedScores), Map.entry(ScoreStatus.UNDEFINED, otherScores));
		return buckets;
	}

	// TODO: for testing only
	public void updateRandomScores(int count) {
		var methods = getModel().getRandomMethods(count);
		var<ScoreStatus> statuses = List.of(ScoreStatus.DECREASED, ScoreStatus.INCREASED, ScoreStatus.NONE, ScoreStatus.UNDEFINED);
		Random r = new Random();
		Map<IMethodDescription, Defineable<Double>> newScores = new HashMap<>();
		for (var entry : methods) {
			var status = statuses.get(r.nextInt(statuses.size()));
			var<Double> value = new Defineable<>(0.0);
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
			System.out.printf("Changing %s from %s to %s\n", entry.getKey().getId().toString(),
					entry.getValue().toString(), value.toString());
		}
		System.out.println("random score changes generated");
		updateScore(newScores);
	}

	private Boolean hideUndefinedScores = false;

	public void setHideUndefinedScores(Boolean status) {
		System.out.println("hiding undefined scores are requested to set: " + status);
		hideUndefinedScores = status;
		updateScore(getModel().getScores());
	}

	private IListener<Map> optionSelectedListener = new IListener<Map>() {

		@Override
		public void invoke(Map event) {
			handler.updateScore(new IUserFeedback() {

				@Override
				public IUser getUser() {
					return null;
				}

				@Override
				public Iterable<IMethodDescription> getSubjects() {
//					List<IMethodDescription> subjects = new ArrayList<IMethodDescription>();
					Map.Entry<String, List> entry = (Entry<String, List>) event.entrySet().iterator().next();
					return entry.getValue();
				}

				@Override
				public Option getChoise() {
					for (Option option : handler.getProvidedOptions()) {
						Map.Entry<String, List> entry = (Entry<String, List>) event.entrySet().iterator().next();
						if (option.getId().equals(entry.getKey())) {
							return option;
						}
					}
					new UnsupportedOperationException("invalid option");
					return null;
				}
			});
		}

	};

	private IListener<Map<IMethodDescription, Defineable<Double>>> scoreUpdatedListener = new IListener<Map<IMethodDescription, Defineable<Double>>>() {

		@Override
		public void invoke(Map<IMethodDescription, Defineable<Double>> event) {
			updateScore(event);

		}

	};

}
