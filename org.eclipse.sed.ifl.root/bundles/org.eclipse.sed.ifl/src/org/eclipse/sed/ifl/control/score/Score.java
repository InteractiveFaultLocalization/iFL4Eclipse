package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.score.ScoreListControl.ScoreStatus;
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
			updateStatus(score.getValue());
		}
		this.interactive = interactive;
	}
	
	private boolean interactive = true;
	
	public boolean isInteractive() {
		return interactive;
	}

	private ScoreStatus status = ScoreStatus.UNDEFINED;
	
	public ScoreStatus getStatus() {
		return status;
	}
	
	public void updateStatus(Double oldValue) {
		if (isDefinit()) {
			if (oldValue < this.getValue()) {
				status = ScoreStatus.INCREASED;
			} else if (oldValue > this.getValue()) {
				status = ScoreStatus.DECREASED;
			} else {
				status = ScoreStatus.NONE;
			}
		} else {
			status = ScoreStatus.UNDEFINED;
		}
	}
}
