package org.eclipse.sed.ifl.ide.gui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sed.ifl.ide.gui.element.ContextBasedScoreSetter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;

public class ContextBasedOptionCreatorDialog extends Dialog {

	public ContextBasedOptionCreatorDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(3, false));
		ContextBasedScoreSetter selecteds = new ContextBasedScoreSetter(composite, SWT.NONE);
		selecteds.setTitle("selected");
		ContextBasedScoreSetter contexts = new ContextBasedScoreSetter(composite, SWT.NONE);
		contexts.setTitle("context");
		ContextBasedScoreSetter others = new ContextBasedScoreSetter(composite, SWT.NONE);
		others.setTitle("other");
		return composite;
	}
}
