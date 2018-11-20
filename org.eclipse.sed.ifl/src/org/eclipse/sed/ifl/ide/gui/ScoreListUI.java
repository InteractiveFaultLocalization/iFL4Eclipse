package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import java.util.Map;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.layout.FillLayout;

public class ScoreListUI extends Composite {
	private Table table;

	public ScoreListUI(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("Name");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("Signiture");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("Parent type");
	}
	
	public void setMethodScore(Map<IMethodDescription, Defineable<Double>> scores) {
		table.removeAll();
		for (IMethodDescription method : scores.keySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, method.getId().getName());
			item.setText(1, method.getId().getSignature());
			item.setText(2, method.getId().getParentType());
		}
	}


}
