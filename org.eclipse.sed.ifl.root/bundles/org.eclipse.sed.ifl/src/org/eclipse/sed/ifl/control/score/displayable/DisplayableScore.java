package org.eclipse.sed.ifl.control.score.displayable;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.Score;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;

public class DisplayableScore {
	private IMethodDescription methodDescription;
	private Score score;
	private Monument<Score, IMethodDescription, IUserFeedback> lastAction;

	public DisplayableScore(IMethodDescription methodDesription, Score score,
			Monument<Score, IMethodDescription, IUserFeedback> lastAction) {
		super();
		this.methodDescription = methodDesription;
		this.score = score;
		this.lastAction = lastAction;
	}

	public IMethodDescription getMethodDescription() {
		return methodDescription;
	}

	public Score getScore() {
		return score;
	}

	public Monument<Score, IMethodDescription, IUserFeedback> getLastAction() {
		return lastAction;
	}
	
}
