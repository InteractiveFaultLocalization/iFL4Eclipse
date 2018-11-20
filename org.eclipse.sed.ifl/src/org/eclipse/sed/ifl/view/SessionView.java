package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.ide.gui.MainPart;
import org.eclipse.swt.widgets.Composite;

public class SessionView extends View {
	private MainPart part;
	
	public SessionView(MainPart part) {
		this.part = part;
	}

	@Override
	public Composite getUI() {
		return part.getUI();
	}
}
