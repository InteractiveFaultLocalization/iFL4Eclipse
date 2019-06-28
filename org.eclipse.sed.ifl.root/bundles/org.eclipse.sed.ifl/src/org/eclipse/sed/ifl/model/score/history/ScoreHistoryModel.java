package org.eclipse.sed.ifl.model.score.history;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;

public class ScoreHistoryModel extends EmptyModel {
	private List<Monument<Score, IMethodDescription, IUserFeedback>> steps = new ArrayList<>();
	
	public void store(Monument<Score, IMethodDescription, IUserFeedback> state) {
		steps.add(state);
	}
}
