package org.eclipse.sed.ifl.ide.gui.dialogs;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;

public class ContextBasedOptionCreatorDialog extends Dialog implements IEmbedee {

	public ContextBasedOptionCreatorDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(3, false));
		for (IEmbeddable embedded : contents) {
			embedded.setParent(composite);
		}
		return composite;
	}

	private List<IEmbeddable> contents = new ArrayList<>();
	
	@Override
	public void embed(IEmbeddable embedded) {
		contents.add(embedded);
	}
}
