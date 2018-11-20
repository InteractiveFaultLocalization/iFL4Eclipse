package org.eclipse.sed.ifl.view;

import java.util.Map;

import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.widgets.Composite;

public class ScoreListView extends View {
	ScoreListUI ui;
	
	public ScoreListView(ScoreListUI ui) {
		super();
		this.ui = ui;
	}

	@Override
	public Composite getUI() {
		return ui;
	}
	
	public void initScores(Map<IMethodDescription, Defineable<Double>> scores) {
		ui.setMethodScore(scores);
	}
}
