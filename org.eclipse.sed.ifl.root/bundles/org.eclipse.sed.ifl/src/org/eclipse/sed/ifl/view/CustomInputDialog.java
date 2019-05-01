package org.eclipse.sed.ifl.view;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CustomInputDialog extends InputDialog {

	private Label counterLabel;
	private int elementNumber;
	private int currentElementNumber;
	
	public CustomInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator, int elementNumber, int currentElementNumber) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.elementNumber = elementNumber;
		this.currentElementNumber = currentElementNumber;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		
		 parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		 createButton(parent, IDialogConstants.RETRY_ID, "<< First", false);
		 createButton(parent, IDialogConstants.BACK_ID, "< Prev.", false);
		 counterLabel = new Label(parent, SWT.NONE);
		 counterLabel.setText(currentElementNumber + "/" + elementNumber);
		 createButton(parent, IDialogConstants.NEXT_ID, "Next >", false);
		 createButton(parent, IDialogConstants.FINISH_ID, "Last >>", false);

		  // Create a spacer label
		 Label spacer = new Label(parent, SWT.NONE);
		 spacer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

		  // Update layout of the parent composite to count the spacer
		 GridLayout layout = (GridLayout)parent.getLayout();
		 layout.numColumns = 10;
		 layout.makeColumnsEqualWidth = false;

		 createButton(parent, IDialogConstants.OK_ID,"OK", false);
		 createButton(parent, IDialogConstants.CANCEL_ID,"Close", false);
	}
	
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
	}
	
}
