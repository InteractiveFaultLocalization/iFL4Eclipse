package org.eclipse.sed.ifl.view;


import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


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

	    Table table = viewer.getTable();
	    
	    table.setLayoutData(new GridData(GridData.FILL_BOTH));

	    // TODO: Set TableViewer content and label providers
	    
	    table.setLinesVisible(true);
	    table.setHeaderVisible(true);
    
	    TableViewerColumn tableViewerColumn1 = new TableViewerColumn(viewer, SWT.NONE);
	    
	    TableColumn nameColumn = tableViewerColumn1.getColumn();
	    nameColumn.setResizable(false);
	    System.out.println("Table area width: " + table.getClientArea().width);
	    nameColumn.setText("Name");
	    nameColumn.setWidth(200);
	    
	    TableViewerColumn tableViewerColumn2 = new TableViewerColumn(viewer, SWT.NONE);
	    TableColumn checkColumn = tableViewerColumn2.getColumn();
	    System.out.println("Table area width: " + table.getClientArea().width);
	    checkColumn.setText("Check");
	    checkColumn.setWidth(200);
	    
	    for(int i = 0; i < list.size(); i++) {
	    	new TableItem(table, SWT.NONE);
	    }
	    TableItem[] items = table.getItems();
	    for(int i = 0; i < items.length; i++) {
	    	TableEditor editor = new TableEditor(table);
	    	Button button = new Button(table, SWT.CHECK);
	        button.pack();
	        editor.minimumWidth = button.getSize().x;
	        editor.horizontalAlignment = SWT.LEFT;
	        editor.setEditor(button, items[i], 1);
	    }
	    
	    
	    // TODO: Set TableViewer input

	    return body;
	  }
	
}
