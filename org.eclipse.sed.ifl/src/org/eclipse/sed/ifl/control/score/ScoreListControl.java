package org.eclipse.sed.ifl.control.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.core.BasicIflMethodScoreHandler;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.identification.IUser;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.view.ScoreListView;

public class ScoreListControl extends Control<ScoreListModel, ScoreListView> {

	private BasicIflMethodScoreHandler handler = new BasicIflMethodScoreHandler(null);

	public ScoreListControl(ScoreListModel model, ScoreListView view) {
		super(model, view);
	}

	@Override
	public void init() {
		getView().refreshScores(getModel().getScores());
		getModel().eventScoreUpdated().add(scoreUpdatedListener);
		getView().createOptionsMenu(handler.getProvidedOptions());
		getView().eventOptionSelected().add(optionSelectedListener);
		//TODO: reenable these after clean up
		//handler.eventScoreUpdated().add(scoreUpdatedListener);
		//handler.loadMethodsScoreMap(getModel().getScores());
		super.init();
	}

	@Override
	public void teardown() {
		getModel().eventScoreUpdated().remove(scoreUpdatedListener);
		getView().eventOptionSelected().remove(optionSelectedListener);
		//TODO: reenable this after clean up
		//handler.eventScoreUpdated().remove(scoreUpdatedListener);
		super.teardown();
	}

	//TODO: Yoda-mode :) split or move it to view
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

	//TODO: it was public, but I think it could be private
	private void updateScore(Map<IMethodDescription, Score> newScores) {
		//TODO: reenable this after clean up
		//handler.loadMethodsScoreMap(getModel().getScores());
		getView().refreshScores(filterForView(newScores));
	}

	//TODO: replace with strategy pattern
	private Map<IMethodDescription, Score> filterForView(Map<IMethodDescription, Score> allScores) {
		Map<IMethodDescription, Score> filtered = new HashMap<>();
		if (hideUndefinedScores) {
			for (var item : allScores.entrySet()) {
				if (item.getValue().isDefinit()) {
					filtered.put(item.getKey(), item.getValue());
				}
			}
		}
		else {
			filtered = allScores;
		}
		return filtered;
	}

	private Boolean hideUndefinedScores = false;

	public void setHideUndefinedScores(Boolean status) {
		System.out.println("hiding undefined scores are requested to set: " + status);
		hideUndefinedScores = status;
		updateScore(getModel().getScores());
	}

	private IListener<Map<String, List<IMethodDescription>>> optionSelectedListener = new IListener<>() {

		@Override
		public void invoke(Map<String, List<IMethodDescription>> event) {
			handler.updateScore(new IUserFeedback() {

				@Override
				public IUser getUser() {
					return null;
				}

				@Override
				public Iterable<IMethodDescription> getSubjects() {
					return event.entrySet().iterator().next().getValue();
				}

				@Override
				public Option getChoise() {
					for (Option option : handler.getProvidedOptions()) {
						if (option.getId().equals(event.entrySet().iterator().next().getKey())) {
							return option;
						}
					}
					new UnsupportedOperationException("invalid option");
					return null;
				}
			});
		}

	};

	private IListener<Map<IMethodDescription, Score>> scoreUpdatedListener = new IListener<>() {

		@Override
		public void invoke(Map<IMethodDescription, Score> event) {
			updateScore(event);
		}
		
	};

}
