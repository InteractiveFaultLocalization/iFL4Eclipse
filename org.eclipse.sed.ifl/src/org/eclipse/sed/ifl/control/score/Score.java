package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.score.ScoreListControl.ScoreStatus;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class Score extends Defineable<Double> {
	
	public Score() {
		super();
		status = ScoreStatus.UNDEFINED;
	}

	public Score(Double value) {
		super(value);
	}
	
	public Score(Defineable<Double> score) {
		if (score.isDefinit()) {
			this.setValue(score.getValue());
		}
	}

	private ScoreStatus status = ScoreStatus.UNDEFINED;
	
	public ScoreStatus getStatus() {
		return status;
	}
	
	@Override
	public void setValue(Double value) {
		if (value > this.getValue()) {
			status = ScoreStatus.INCREASED;
		} else if (value < this.getValue()) {
			status = ScoreStatus.DECREASED;
		} else {
			status = ScoreStatus.NONE;
		}
		super.setValue(value);
	}
}
