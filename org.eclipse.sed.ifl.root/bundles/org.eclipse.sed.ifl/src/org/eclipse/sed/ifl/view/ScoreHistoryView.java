package org.eclipse.sed.ifl.view;

import java.time.LocalDateTime;

import org.eclipse.sed.ifl.ide.gui.ScoreHistoryUI;
import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.swt.widgets.Composite;

public class ScoreHistoryView extends View {
	private ScoreHistoryUI ui;

	@Override
	public Composite getUI() {
		return ui;
	}

	public ScoreHistoryView(ScoreHistoryUI ui) {
		this.ui = ui;
	}
	
	public void addMonument(OptionKind kind, LocalDateTime creation) {
		ui.putInRow(kind, creation);
	}
	
	public void clearMonuments() {
		ui.clearRow();
	}
}
