package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class Score extends Defineable<Double> {
	
	public Score(boolean interactive) {
		super();
		this.interactive = interactive;
	}

	public Score(Double value, boolean interactive) {
		super(value);
		this.interactive = interactive;
	}
	
	public Score(Defineable<Double> score, boolean interactive) {
		if (score.isDefinit()) {
			this.setValue(score.getValue());
		}
		this.interactive = interactive;
	}
	
	private boolean interactive = true;
	
	public boolean isInteractive() {
		return interactive;
	}
	
	private Monument<Score, IMethodDescription, IUserFeedback> lastAction;

	public Monument<Score, IMethodDescription, IUserFeedback> getLastAction() {
		return lastAction;
	}

	public void setLastAction(Monument<Score, IMethodDescription, IUserFeedback> lastAction) {
		this.lastAction = lastAction;
	}
}
