package org.eclipse.sed.ifl.control.score.displayable;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.Score;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;

public class DisplayableScore {
	private IMethodDescription methodDesription;
	private Score score;
	private Monument<Score, IMethodDescription, IUserFeedback> lastAction;
	
	public DisplayableScore(IMethodDescription methodDesription, Score score) {
		super();
		this.methodDesription = methodDesription;
		this.score = score;
	}

	public IMethodDescription getMethodDesription() {
		return methodDesription;
	}

	public Score getScore() {
		return score;
	}

	public Monument<Score, IMethodDescription, IUserFeedback> getLastAction() {
		return lastAction;
	}

	public void setLastAction(Monument<Score, IMethodDescription, IUserFeedback> lastAction) {
		this.lastAction = lastAction;
	}
	
}
