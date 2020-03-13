package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.dialogs.ContextBasedOptionCreatorDialog;
import org.eclipse.swt.widgets.Display;

public class ContextBasedOptionCreatorView extends View implements IEmbedee {

	private ContextBasedOptionCreatorDialog dialog = new ContextBasedOptionCreatorDialog(Display.getCurrent().getActiveShell());
	
	
	public void display() {
		dialog.open();
	}

	@Override
	public void embed(IEmbeddable embedded) {
		dialog.embed(embedded);
	}
}
