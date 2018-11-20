package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.view.ScoreListView;

public class ScoreListControl extends Control<ScoreListModel, ScoreListView> {

	public ScoreListControl(ScoreListModel model, ScoreListView view) {
		super(model, view);
	}
	
	@Override
	public void init() {
		getView().initScores(getModel().getScores());
		super.init();
	}
}
