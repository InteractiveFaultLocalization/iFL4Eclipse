package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;

public class ScoreHistoryUI extends Composite {
	public ScoreHistoryUI(Composite parent, int style) {
		super(parent, SWT.BORDER);
		setLayout(new FillLayout(SWT.HORIZONTAL));
	}
}
