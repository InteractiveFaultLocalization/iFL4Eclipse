package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.ide.gui.ScoreHistoryUI;
import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;

public class ScoreHistoryView extends View {
	private ScoreHistoryUI ui;

	@Override
	public Composite getUI() {
		return ui;
	}

	public ScoreHistoryView(ScoreHistoryUI ui) {
		this.ui = ui;
	}
	
	public void addMonument(OptionKind type) {
		Image icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", type.getIconPath());
		new Label(getUI(), SWT.NONE).setImage(icon);
	}
}
