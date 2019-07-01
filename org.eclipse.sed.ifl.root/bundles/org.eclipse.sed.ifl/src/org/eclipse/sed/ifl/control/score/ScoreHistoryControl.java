package org.eclipse.sed.ifl.control.score;

import java.util.List;

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
	
	public void display(IMethodDescription subject) {
		List<Monument<Score,IMethodDescription,IUserFeedback>> monuments = getModel().getMonumentsFor(subject);
		for (Monument<Score,IMethodDescription,IUserFeedback> monument : monuments) {
			getView().addMonument(monument.getCause().getChoise().getKind());
		}
	}
}
