package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.layout.FillLayout;

import java.time.LocalDateTime;

import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowLayout;

public class ScoreHistoryUI extends Composite {
	private Composite monumentRow;

	public ScoreHistoryUI(Composite parent, int style) {
		super(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.wrap = false;
		layout.center = true;
		setLayout(layout);
		
		Label lblHistory = new Label(this, SWT.NONE);
		lblHistory.setText("History");
		
		monumentRow = new Composite(this, SWT.NONE);
		monumentRow.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		//for test only
		putInRow(OptionKind.CONTEXT_00X, LocalDateTime.now());
	}
	
	public void putInRow(OptionKind kind, LocalDateTime creation) {
		Image icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", kind.getIconPath());
		MonumentUI monument = new MonumentUI(monumentRow, SWT.NONE, icon, creation);
		monumentRow.requestLayout();
	}
	
	public void clearRow() {
		for (Control control : monumentRow.getChildren()) {
			control.dispose();
		}
		monumentRow.requestLayout();
	}
}
