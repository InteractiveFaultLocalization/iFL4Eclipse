package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.ResourceManager;

public class ScoreListUI extends Composite {
	private Table table;

	public ScoreListUI(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableColumn iconColumn = new TableColumn(table, SWT.NONE);
		iconColumn.setWidth(32);
		iconColumn.setResizable(false);
		
		TableColumn scoreColumn = new TableColumn(table, SWT.NONE);
		scoreColumn.setMoveable(true);
		scoreColumn.setWidth(100);
		scoreColumn.setText("Score");
		
		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setMoveable(true);
		nameColumn.setWidth(100);
		nameColumn.setText("Name");
		
		TableColumn signitureColumn = new TableColumn(table, SWT.NONE);
		signitureColumn.setMoveable(true);
		signitureColumn.setWidth(100);
		signitureColumn.setText("Signiture");
		
		TableColumn typeColumn = new TableColumn(table, SWT.NONE);
		typeColumn.setMoveable(true);
		typeColumn.setWidth(100);
		typeColumn.setText("Parent type");
	}
	
	public void setMethodScore(Map<IMethodDescription, Defineable<Double>> scores, String iconPath) {
		table.removeAll();
		for (Entry<IMethodDescription, Defineable<Double>> entry : scores.entrySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			if (iconPath != null) {
				item.setImage(0, ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath));
			}
			if (entry.getValue().isDefinit()) {
				item.setText(1, entry.getValue().getValue().toString());
			}
			else {
				item.setText(1, "undefined");
			}
			item.setText(2, entry.getKey().getId().getName());
			item.setText(3, entry.getKey().getId().getSignature());
			item.setText(4, entry.getKey().getId().getParentType());
		}
	}


}
