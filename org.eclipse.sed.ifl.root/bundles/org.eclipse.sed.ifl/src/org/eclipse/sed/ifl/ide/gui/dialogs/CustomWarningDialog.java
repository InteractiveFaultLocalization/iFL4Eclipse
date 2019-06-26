package org.eclipse.sed.ifl.ide.gui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
public class CustomWarningDialog extends Dialog {

	private Image image;
	private String title;
	private String message;
	
	public CustomWarningDialog(Shell parentShell, String dialogTitle, String dialogMessage) {
		super(parentShell);
		this.title = dialogTitle;
		this.message = dialogMessage;
		this.image = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/warning-icon.png");
	}
	
	@Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }
	
	@Override
	protected Control createDialogArea(Composite parent) {
		createMessageArea(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		composite.setLayoutData(data);
		return composite;
	}
	
	protected Control createMessageArea(Composite composite) {
		Label imageLabel = new Label(composite, SWT.NULL);
		image.setBackground(imageLabel.getBackground());
		imageLabel.setImage(image);

		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).applyTo(imageLabel);
		
		Label messageLabel = new Label(composite, SWT.NONE);
		messageLabel.setText(message);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
			.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH),SWT.DEFAULT).applyTo(messageLabel);
		return composite;
	}

}
