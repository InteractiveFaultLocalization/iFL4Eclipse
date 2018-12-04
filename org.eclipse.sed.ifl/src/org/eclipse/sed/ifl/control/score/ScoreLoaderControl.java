package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.view.ScoreLoaderView;

public class ScoreLoaderControl extends Control<ScoreListModel, ScoreLoaderView> {

	public ScoreLoaderControl(ScoreListModel model, ScoreLoaderView view) {
		super(model, view);
	}

	public void load() {
		getView().select();
	}
}
