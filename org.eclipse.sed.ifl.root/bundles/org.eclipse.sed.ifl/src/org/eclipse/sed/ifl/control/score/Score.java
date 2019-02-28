package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.score.ScoreListControl.ScoreStatus;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class Score extends Defineable<Double> {
	
	public Score(boolean interactive) {
		super();
		status = ScoreStatus.UNDEFINED;
		this.interactive = interactive;
	}

	public Score(Double value, boolean interactive) {
		super(value);
	}
	
	public Score(Defineable<Double> score, boolean interactive) {
		if (score.isDefinit()) {
			this.setValue(score.getValue());
		}
	}
	
	private boolean interactive = true;
	
	public boolean isInteractive() {
		return interactive;
	}

	private ScoreStatus status = ScoreStatus.UNDEFINED;
	
	public ScoreStatus getStatus() {
		return status;
	}
	
	@Override
	public void setValue(Double value) {
		if (isDefinit()) {
			if (value > this.getValue()) {
				status = ScoreStatus.INCREASED;
			} else if (value < this.getValue()) {
				status = ScoreStatus.DECREASED;
			} else {
				status = ScoreStatus.NONE;
			}
		} else {
			status = ScoreStatus.UNDEFINED;
		}
		super.setValue(value);
	}
}
