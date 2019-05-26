package org.eclipse.sed.ifl.view;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomInputDialog extends Dialog {
	
	private Button okButton;
	
	private String title;
	private String message;
	private Table table;
	private TableColumn nameColumn;
	private TableColumn typeNameColumn;
	
	private List <IMethodDescription> list;
	private List <Text> typedTextList = new ArrayList<Text>();
		
	public CustomInputDialog(Shell parentShell, String dialogTitle, String dialogMessage,
			List<IMethodDescription> methodDescription) {
		super(parentShell);
		this.title = dialogTitle;
		this.message = dialogMessage;
		this.list = methodDescription;
	}
	
	@Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }
	
	@Override
	  protected Control createDialogArea(Composite parent)
	  {
		Composite container = (Composite) super.createDialogArea(parent);
		
		if (message != null) {
			Label label = new Label(container, SWT.WRAP);
			label.setText(message);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL
					| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
					| GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(data);
			label.setFont(parent.getFont());
		}
		
		table = new Table(container, SWT.HIDE_SELECTION);
		GridData gd_table = new GridData(SWT.FILL);
		gd_table.grabExcessVerticalSpace = true;
		gd_table.grabExcessHorizontalSpace = true;
		gd_table.verticalAlignment = SWT.FILL;
		gd_table.horizontalAlignment = SWT.FILL;
		table.setLayoutData(gd_table);
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		createTableColumns();
		
		setTableContents();
				
		
		return container;
	  }

	
	private void setTableContents() {
		for(IMethodDescription methodName : list) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(table.indexOf(nameColumn), methodName.getId().getName());
			
			TableEditor editor = new TableEditor(table);
			Text text = new Text(table, SWT.NONE);
			text.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					okButton.setEnabled(validateInputs());;
				}
				
			});
			typedTextList.add(text);
			editor.grabHorizontal = true;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(text, item, 1);
		}
		
	}

	private void createTableColumns() {
		nameColumn = new TableColumn(table, SWT.CENTER);
		nameColumn.setWidth(270);
		nameColumn.setText("Name");
		nameColumn.setResizable(false);
		
		typeNameColumn = new TableColumn(table, SWT.CENTER);
		typeNameColumn.setWidth(270);
		typeNameColumn.setText("Type name");
		typeNameColumn.setResizable(false);
	}
	
	
	private boolean validateInputs() {
		boolean rValue = true;
		
		for(int i=0; i<list.size(); i++) {
			if(!list.get(i).getId().getName().equals(typedTextList.get(i).getText())) {
				rValue = false;
				break;
			}
		}
		
		return rValue;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		
	}
	
}
