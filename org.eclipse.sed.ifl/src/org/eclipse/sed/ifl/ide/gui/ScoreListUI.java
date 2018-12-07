package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import java.text.Collator;
import java.util.Locale;
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
	private TableColumn nameColumn;
	private TableColumn iconColumn;
	private TableColumn scoreColumn;
	private TableColumn signitureColumn;
	private TableColumn typeColumn;

	public ScoreListUI(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		iconColumn = new TableColumn(table, SWT.NONE);
		iconColumn.setWidth(32);
		iconColumn.setResizable(false);

		scoreColumn = new TableColumn(table, SWT.NONE);
		scoreColumn.setMoveable(true);
		scoreColumn.setWidth(100);
		scoreColumn.setText("Score");

		nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setMoveable(true);
		nameColumn.setWidth(100);
		nameColumn.setText("Name");

		signitureColumn = new TableColumn(table, SWT.NONE);
		signitureColumn.setMoveable(true);
		signitureColumn.setWidth(100);
		signitureColumn.setText("Signiture");

		typeColumn = new TableColumn(table, SWT.NONE);
		typeColumn.setMoveable(true);
		typeColumn.setWidth(100);
		typeColumn.setText("Parent type");

		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				TableItem[] items = table.getItems();
				Collator collator = Collator.getInstance(Locale.getDefault());
				TableColumn sortColumn = table.getSortColumn();
				int dir = table.getSortDirection();

				TableColumn column = (TableColumn) e.widget;

				if (sortColumn == column) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					table.setSortColumn(column);
					dir = SWT.UP;
				}
				int index = 0;
				if (column == scoreColumn) {
					index = 1;
				}
				if (column == nameColumn) {
					index = 2;
				}
				if (column == signitureColumn) {
					index = 3;
				}
				if (column == typeColumn) {
					index = 4;
				}

				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {
						String value2 = items[j].getText(index);
						if ((collator.compare(value1, value2) < 0 && dir == SWT.UP)
								|| (collator.compare(value1, value2) > 0 && dir == SWT.DOWN)) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
				table.setSortColumn(column);
				table.setSortDirection(dir);
			}
		};
		scoreColumn.addListener(SWT.Selection, sortListener);
		nameColumn.addListener(SWT.Selection, sortListener);
		signitureColumn.addListener(SWT.Selection, sortListener);
		typeColumn.addListener(SWT.Selection, sortListener);

		table.setSortColumn(nameColumn);

	}

	public void setMethodScore(Map<IMethodDescription, Defineable<Double>> scores, String iconPath) {
		for (Entry<IMethodDescription, Defineable<Double>> entry : scores.entrySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			if (iconPath != null) {
				var icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath);
				item.setImage(0, icon);
			}
			if (entry.getValue().isDefinit()) {
				item.setText(1, entry.getValue().getValue().toString());
			} else {
				item.setText(1, "undefined");
			}
			item.setText(2, entry.getKey().getId().getName());
			item.setText(3, entry.getKey().getId().getSignature());
			item.setText(4, entry.getKey().getId().getParentType());
		}
		// table.setSortColumn(nameColumn);
		// table.setSortDirection(SWT.DOWN);
		iconColumn.pack();
	}

	public void clearMethodScores() {
		table.removeAll();
	}

}
