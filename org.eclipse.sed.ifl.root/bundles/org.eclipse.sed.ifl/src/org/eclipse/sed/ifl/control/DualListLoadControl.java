package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.score.ScoreListModel;


public class DualListLoadControl extends ViewlessControl<ScoreListModel> {

	public DualListLoadControl() {

	}

	

	public void duallistload() throws UnsupportedOperationException {
		System.out.println("Loading ordering list is requested...");
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