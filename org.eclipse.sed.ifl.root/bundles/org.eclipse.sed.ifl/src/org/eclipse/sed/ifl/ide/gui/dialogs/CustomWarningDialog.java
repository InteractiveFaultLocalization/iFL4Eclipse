package org.eclipse.sed.ifl.ide.gui.dialogs;

import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
public class CustomWarningDialog extends IconAndMessageDialog {

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
		super.message = message;
		createMessageArea(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		data.horizontalSpan = 2;
		composite.setLayoutData(data);
		return composite;
	}
		

	@Override
	protected Image getImage() {
		return image;
	}
	

}
