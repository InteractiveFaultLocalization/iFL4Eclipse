package org.eclipse.sed.ifl.view;

import java.time.LocalDateTime;

import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.ide.gui.ScoreHistoryUI;
import org.eclipse.sed.ifl.ide.gui.icon.ScoreStatus;
import org.eclipse.swt.widgets.Composite;

public class ScoreHistoryView extends View implements IEmbeddable {
	private ScoreHistoryUI ui = new ScoreHistoryUI();

	@Override
	public void setParent(Composite parent) {
		ui.setParent(parent);
	}
	
	@Override
	public void init() {
		super.init();
		hide();
	}
	
	public void addMonument(ScoreStatus status, LocalDateTime creation) {
		ui.putInRow(status, creation);
	}
	
	public void clearMonuments() {
		ui.clearRow();
	}
	
	public void hide() {
		ui.setVisible(false);
	}
	
	public void show() {
		ui.setVisible(true);
	}
}
