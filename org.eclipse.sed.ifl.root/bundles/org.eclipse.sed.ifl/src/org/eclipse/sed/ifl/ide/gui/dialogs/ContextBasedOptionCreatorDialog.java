package org.eclipse.sed.ifl.ide.gui.dialogs;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;

public class ContextBasedOptionCreatorDialog extends Dialog implements IEmbedee {

	private static int OK_BUTTON_ID = 1600;
	private static int CANCEL_BUTTON_ID = 1601;
	private Label warningLabel;
	
	public ContextBasedOptionCreatorDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected void configureShell(Shell shell) {
	      super.configureShell(shell);
	      shell.setText("Context based feedback");
	   }
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, OK_BUTTON_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, CANCEL_BUTTON_ID, IDialogConstants.CANCEL_LABEL, false);
		getButton(OK_BUTTON_ID).addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.getDisplay().getActiveShell().setVisible(false);
				customFeedbackSelected.invoke(true);
				refreshUi.invoke(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		getButton(CANCEL_BUTTON_ID).addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.getDisplay().getActiveShell().setVisible(false);
				refreshUi.invoke(true);
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
		refreshUi.invoke(true);
		return this.getShell().getVisible();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite labelComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft = 5;
		layout.marginTop = 5;
		labelComp.setLayout(layout);
		
		warningLabel = new Label(labelComp, SWT.NONE);
		warningLabel.setText("Warning: the visualization does not represent all elements. To see all elements and score changes, use the tables below for each group.");
		
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
	
	private NonGenericListenerCollection<Boolean> customFeedbackSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Boolean> eventCustomFeedbackSelected() {
		return customFeedbackSelected;
	}
	
	private NonGenericListenerCollection<Boolean> refreshUi = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Boolean> eventRefreshUi() {
		return refreshUi;
	}
	
}
