package org.eclipse.sed.ifl.ide.gui.dialogs;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomInputDialog extends Dialog {
	
	private Button okButton;
	
	private String title;
	private String message;
	private Table table;
	private TableColumn nameColumn;
	private TableColumn typeNameColumn;
	
	private List <String> list;
	private List <Text> typedTextList = new ArrayList<Text>();
	private Set <String> elementSet = new HashSet<String>();
		
	public CustomInputDialog(Shell parentShell, String dialogTitle, String dialogMessage,
			List<String> elementNames) {
		super(parentShell);
		this.title = dialogTitle;
		this.message = dialogMessage;
		this.list = elementNames;
		putListElemsToSet(elementNames);
	}
	
	private void putListElemsToSet(List<String> list) {
		for(String elem : list) {
			elementSet.add(elem);
		}
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
		
		table = new Table(container, SWT.HIDE_SELECTION | SWT.V_SCROLL);
		GridData gd_table = new GridData(SWT.FILL);
		gd_table.grabExcessVerticalSpace = false;
		gd_table.grabExcessHorizontalSpace = true;
		gd_table.verticalAlignment = SWT.FILL;
		gd_table.horizontalAlignment = SWT.FILL;
		if(elementSet.size() > 8) {
			gd_table.heightHint = 150;
		}
		table.setLayoutData(gd_table);
		
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		
		tableLayout.addColumnData(new ColumnWeightData(50));
		tableLayout.addColumnData(new ColumnWeightData(50));
		
		table.setLinesVisible(true);
		table.setHeaderVisible(false);
		
		createTableColumns();
		
		setTableContents();		
		
		return container;
	  }

	
	private void setTableContents() {
		
		for(String elementName : elementSet) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(table.indexOf(nameColumn), elementName);
			
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
		typedTextList.get(0).setFocus();
	}

	private void createTableColumns() {
		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("");
		nameColumn.setResizable(false);
		
		typeNameColumn = new TableColumn(table, SWT.LEFT);
		typeNameColumn.setText("");
		typeNameColumn.setResizable(false);
	}
	
	
	private boolean validateInputs() {
		boolean rValue = true;
		
		for(int i=0; i<list.size(); i++) {
			if(!list.get(i).equals(typedTextList.get(i).getText())) {
				rValue = false;
				typedTextList.get(i).setBackground(new Color(null, 255,102,102));     
			} else {
				typedTextList.get(i).setBackground(new Color(null, 178,255,102));
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
