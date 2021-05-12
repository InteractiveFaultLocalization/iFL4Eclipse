package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.score.ScoreListModel;


public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {

	public ScoreRecalculatorControl() {

	}

	

	public void recalculate() throws UnsupportedOperationException {
		System.out.println("Recalculating scores are requested...");
		throw new UnsupportedOperationException("Function is not yet implemented");
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void teardown() {
		super.teardown();
	}

}
