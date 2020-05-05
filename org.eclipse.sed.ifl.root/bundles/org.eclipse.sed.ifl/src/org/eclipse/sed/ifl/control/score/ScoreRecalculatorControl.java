package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.view.ScoreLoaderView;

public class ScoreRecalculatorControl extends Control<ScoreListModel, ScoreLoaderView> {
	
	private boolean interactivity;
	
	public ScoreRecalculatorControl(boolean interactivity) {
		this.interactivity = interactivity;
	}
	
	public void load() {
		getView().select();
	}

}
