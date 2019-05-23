package org.eclipse.sed.ifl.view;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

public class CustomInputDialog extends InputDialog {
	
	private List <IMethodDescription> list;
	private ArrayList <Label> labelList;
	private ArrayList <Text> textList;
	
	public CustomInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator, List<IMethodDescription> methodDescription) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.list = methodDescription;
		textList = new ArrayList<Text>();
		labelList = new ArrayList<Label>();
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
	    TableColumn typeColumn = tableViewerColumn2.getColumn();
	    System.out.println("Table area width: " + table.getClientArea().width);
	    typeColumn.setText("Check");
	    typeColumn.setWidth(220);
	    typeColumn.setResizable(false);
	    
	    for(int i = 0; i < list.size(); i++) {
	    	new TableItem(table, SWT.NONE);
	    }
	    TableItem[] items = table.getItems();
	    for(int i = 0; i < items.length; i++) {
	    	TableEditor nameEditor = new TableEditor(table);
	    	Label nameLabel = new Label(table, SWT.LEFT);
	    	nameLabel.setText(list.get(i).getId().getName());
	    	nameEditor.grabHorizontal = true;
			nameEditor.grabVertical = true;
	    	nameEditor.horizontalAlignment = SWT.RIGHT;
	    	nameEditor.setEditor(nameLabel, items[i], 0);
	    	
	    	labelList.add(nameLabel);
	    	
	    	TableEditor typeEditor = new TableEditor(table);
	    	Text nameText = new Text(table, SWT.BORDER);
	    	nameText.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					validateInput();
					
				}
	    		
	    	});
	        
	    	typeEditor.grabHorizontal = true;
			typeEditor.grabVertical = true;
	    	typeEditor.horizontalAlignment = SWT.RIGHT;
	    	typeEditor.setEditor(nameText, items[i], 1);
	    	
	        textList.add(nameText);
	    }
	    	    
	    return body;
	  }
	
	@Override
	protected void validateInput() {
		String errorMessage = null;
		if (super.getValidator() != null) {
			errorMessage = super.getValidator().isValid(super.getText().getText());
		}
		
		if(!allNamesWritten()) {
			errorMessage = "Type \"Finish session\" and the name of all selected methods or select cancel to abort.";
		}
		
		super.setErrorMessage(errorMessage);
	}
	
	public boolean allNamesWritten() {
		boolean rValue = true;
		for(int i=0; i<labelList.size(); i++) {
			if(!textList.get(i).getText().equals(labelList.get(i).getText())) {
				rValue = false;
				break;
			}
		}
		
		return rValue;
	}
	
}
