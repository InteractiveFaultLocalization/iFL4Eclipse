package org.eclipse.sed.ifl.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ScoreLoaderView extends View {

	private FileDialog dialog;

	public ScoreLoaderView() {
		dialog = new FileDialog(new Shell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] {"*.csv"});
	}
	
	@Override
	public Composite getUI() {
		return null;
	}

	public void select() {
		dialog.open();
	}
	
}
