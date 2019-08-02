package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.ide.gui.dialogs.ContextBasedOptionCreatorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ContextBasedOptionCreatorView extends View {

	ContextBasedOptionCreatorDialog dialog = new ContextBasedOptionCreatorDialog(Display.getCurrent().getActiveShell());
	
	@Override
	public Composite getUI() {
		return null;
	}
	
	public void display() {
		dialog.open();
	}

}
