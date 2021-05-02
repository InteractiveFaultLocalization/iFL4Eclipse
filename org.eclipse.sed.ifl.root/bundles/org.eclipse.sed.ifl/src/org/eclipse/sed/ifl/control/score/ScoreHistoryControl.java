package org.eclipse.sed.ifl.control.score;

import java.util.List;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.score.history.ScoreHistoryModel;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.view.ScoreHistoryView;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class ScoreHistoryControl extends Control<ScoreHistoryModel, ScoreHistoryView> {

	public void store(Score newScore, Score oldScore, IMethodDescription subject, IUserFeedback cause) {
		getModel().store(new Monument<>(newScore, oldScore, subject, cause));
	}
	
	public void hideView() {
		getView().hide();
	}
	
	public void display(IMethodDescription subject) {
		getView().clearMonuments();
		List<Monument<Score,IMethodDescription,IUserFeedback>> monuments = getModel().getMonumentsFor(subject);
		if (monuments.isEmpty()) {
			getView().hide();
		} else {
			getView().show();
			for (Monument<Score,IMethodDescription,IUserFeedback> monument : monuments) {
				getView().addMonument(monument.getChange(), monument.getCreation());
			}
		}
	}
	
	public Monument<Score,IMethodDescription,IUserFeedback> getLastOf(IMethodDescription subject) {
		return getModel().getLastOf(subject);
	}
}
