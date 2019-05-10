package org.eclipse.sed.ifl.view;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import java.util.ArrayList;
import java.util.List;

public class CustomInputDialog extends InputDialog {
	
	private List <IMethodDescription> list;
	
	public CustomInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator, List<IMethodDescription> methodDescription) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.list = methodDescription;
	}
	
	@Override
	  protected Control createDialogArea(final Composite parent)
	  {
	    final Composite body = (Composite)super.createDialogArea(parent);

	    final TableViewer viewer = new TableViewer(body, SWT.BORDER);

	    viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    // TODO: Set TableViewer content and label providers
	    // TODO: Set TableViewer input

	    return body;
	  }
	
}
