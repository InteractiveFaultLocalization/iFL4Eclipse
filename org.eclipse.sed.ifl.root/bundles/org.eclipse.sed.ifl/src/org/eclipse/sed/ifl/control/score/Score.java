package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class Score extends Defineable<Double> {
	
	public Score() {
		super();
	}

	public Score(Double value) {
		super(value);
	}
	
	public Score(Defineable<Double> score) {
		if (score.isDefinit()) {
			this.setValue(score.getValue());
		}
	}
	
	
	private Monument<Score, IMethodDescription, IUserFeedback> lastAction;

	public Monument<Score, IMethodDescription, IUserFeedback> getLastAction() {
		return lastAction;
	}

	public void setLastAction(Monument<Score, IMethodDescription, IUserFeedback> lastAction) {
		this.lastAction = lastAction;
	}
}
