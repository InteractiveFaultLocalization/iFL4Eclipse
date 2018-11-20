package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.swt.widgets.Composite;

public class ProjectView extends View {
	ScoreListUI ui;
	
	public ProjectView(ScoreListUI ui) {
		super();
		this.ui = ui;
	}

	@Override
	public Composite getUI() {
		return ui;
	}
}
