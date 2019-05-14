package org.eclipse.sed.ifl.view;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.util.ArrayList;
import java.util.List;

public class CustomInputDialog extends InputDialog {
	
	private List <IMethodDescription> list;
	private ArrayList <Button> buttonList;
	
	public CustomInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator, List<IMethodDescription> methodDescription) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.list = methodDescription;
		buttonList = new ArrayList<Button>();
	}
	
	@Override
	  protected Control createDialogArea(final Composite parent)
	  {
	    final Composite body = (Composite)super.createDialogArea(parent);

	    final TableViewer viewer = new TableViewer(body, SWT.BORDER);

	    Table table = viewer.getTable();
	
	    table.setLinesVisible(false);
	    table.setHeaderVisible(true);
    
	    TableViewerColumn tableViewerColumn1 = new TableViewerColumn(viewer, SWT.LEFT);
	    TableColumn nameColumn = tableViewerColumn1.getColumn();
	    System.out.println("Table area width: " + table.getClientArea().width);
	    nameColumn.setText("Name");
	    nameColumn.setWidth(220);
	    nameColumn.setResizable(false);
	    
	    TableViewerColumn tableViewerColumn2 = new TableViewerColumn(viewer, SWT.LEFT);
	    TableColumn checkColumn = tableViewerColumn2.getColumn();
	    System.out.println("Table area width: " + table.getClientArea().width);
	    checkColumn.setText("Check");
	    checkColumn.setWidth(220);
	    checkColumn.setResizable(false);
	    
	    for(int i = 0; i < list.size(); i++) {
	    	new TableItem(table, SWT.NONE);
	    }
	    TableItem[] items = table.getItems();
	    for(int i = 0; i < items.length; i++) {
	    	TableEditor nameEditor = new TableEditor(table);
	    	Label methodName = new Label(table, SWT.LEFT);
	    	methodName.setText(list.get(i).getId().getName());
	    	nameEditor.grabHorizontal = true;
			nameEditor.grabVertical = true;
	    	nameEditor.horizontalAlignment = SWT.RIGHT;
	    	nameEditor.setEditor(methodName, items[i], 0);
	    	
	    	TableEditor checkEditor = new TableEditor(table);
	    	Button button = new Button(table, SWT.CHECK);
	    	button.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					validateInput();
					
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
	    		
	    	});
	        button.pack();
	        checkEditor.minimumWidth = button.getSize().x;
	    	checkEditor.horizontalAlignment = SWT.LEFT;
	        checkEditor.setEditor(button, items[i], 1);
	    	buttonList.add(button);
	    }
	    	    
	    return body;
	  }
	
	@Override
	protected void validateInput() {
		String errorMessage = null;
		if (super.getValidator() != null) {
			errorMessage = super.getValidator().isValid(super.getText().getText());
		}
		
		if(!allBoxesChecked()) {
			errorMessage = "Type \"Finish session\" and check all boxes or select cancel to abort.";
		}
		
		super.setErrorMessage(errorMessage);
	}
	
	public boolean allBoxesChecked() {
		boolean rValue = true;
		for(int i=0; i<buttonList.size(); i++) {
			if(!buttonList.get(i).getSelection()) {
				rValue = false;
				break;
			}
		}
		
		return rValue;
	}
	
}
