package org.eclipse.sed.ifl.ide.gui.dialogs;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.sed.ifl.commons.model.source.Line;
import org.eclipse.sed.ifl.commons.model.source.Score;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ShowLinesDialog extends Dialog {
	
	private String title;
	private String message;
	private Table table;
	private TableColumn lineColumn;
	private TableColumn scoreColumn;
	
	private Map<Line, Score> lines;

	public ShowLinesDialog(Shell parentShell, String dialogTitle, String dialogMessage,
			Map<Line, Score> lines) {
		super(parentShell);
		this.title = dialogTitle;
		this.message = dialogMessage;
		this.lines = new TreeMap<>(Collections.unmodifiableMap(lines));
		// TODO Auto-generated constructor stub
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
		gd_table.heightHint = 150;
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
	
	private void createTableColumns() {
		lineColumn = new TableColumn(table, SWT.LEFT);
		lineColumn.setText("Lines");
		lineColumn.setResizable(false);
		
		scoreColumn = new TableColumn(table, SWT.LEFT);
		scoreColumn.setText("Scores");
		scoreColumn.setResizable(false);
	}
	
	private void setTableContents() {
		for(Map.Entry<Line, Score> line : lines.entrySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(table.indexOf(lineColumn), String.valueOf(line.getKey().getSourceFileLineNumber()));
			item.setText(table.indexOf(scoreColumn), line.getValue().getValue().toString());
		}
	}

}
