package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.score.history.ScoreHistoryModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.view.ScoreHistoryView;

public class ScoreHistoryControl extends Control<ScoreHistoryModel, ScoreHistoryView> {

	public ScoreHistoryControl(ScoreHistoryModel model, ScoreHistoryView view) {
		super(model, view);
	}
	
	public void store(Score newScore, Score oldScore, IMethodDescription subject, IUserFeedback cause) {
		getModel().store(new Monument<>(newScore, oldScore, subject, cause));
	}
}
