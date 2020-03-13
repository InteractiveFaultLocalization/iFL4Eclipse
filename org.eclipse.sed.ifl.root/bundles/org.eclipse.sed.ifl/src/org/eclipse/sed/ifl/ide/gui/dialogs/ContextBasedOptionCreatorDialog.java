package org.eclipse.sed.ifl.ide.gui.dialogs;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;

public class ContextBasedOptionCreatorDialog extends Dialog implements IEmbedee {

	public ContextBasedOptionCreatorDialog(Shell parentShell) {
		super(parentShell);
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, 1600, IDialogConstants.OK_LABEL, true);
		createButton(parent, 1601, IDialogConstants.CANCEL_LABEL, false);
		getButton(1600).addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.getDisplay().getActiveShell().setVisible(false);
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		getButton(1601).addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.getDisplay().getActiveShell().setVisible(false);
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	public boolean close() {
		this.getShell().setVisible(false);
		return this.getShell().getVisible();
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
