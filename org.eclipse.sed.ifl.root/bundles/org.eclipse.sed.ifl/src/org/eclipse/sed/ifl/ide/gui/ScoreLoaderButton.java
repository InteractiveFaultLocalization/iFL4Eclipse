package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import swing2swt.layout.BorderLayout;

public class ScoreLoaderButton extends Composite {

	public ScoreLoaderButton(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		Button loadButton = new Button(this, SWT.NONE);
		loadButton.setText("Load scores...");
	}
}
