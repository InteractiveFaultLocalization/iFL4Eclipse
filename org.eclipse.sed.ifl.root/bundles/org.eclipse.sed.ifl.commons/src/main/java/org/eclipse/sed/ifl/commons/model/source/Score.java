package org.eclipse.sed.ifl.commons.model.source;

import org.eclipse.sed.ifl.commons.model.util.wrapper.Defineable;

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
}
