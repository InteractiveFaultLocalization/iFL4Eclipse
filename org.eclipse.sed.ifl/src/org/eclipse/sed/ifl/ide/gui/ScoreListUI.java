package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;

import java.awt.BorderLayout;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
	private TableColumn keyColumn;

	public ScoreListUI(Composite parent, int style) {
		super(parent, style);
		setLayoutData(BorderLayout.CENTER);
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
		keyColumn = new TableColumn(table, SWT.NONE);
		keyColumn.setWidth(300);
		keyColumn.setText("Key");

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
				if (column == keyColumn) {
					index = 5;
				}

				final int finalIndex = index;
				if (dir == SWT.UP) {
					Arrays.sort(items, (TableItem a, TableItem b) -> collator.compare(a.getText(finalIndex), b.getText(finalIndex)));
				}
				else if (dir == SWT.DOWN) {
					Arrays.sort(items, (TableItem a, TableItem b) -> -collator.compare(a.getText(finalIndex), b.getText(finalIndex)));					
				}
				for (var item : items) {
					TableItem newItem = new TableItem(table, SWT.NONE);
					newItem.setText(new String[] { 
							item.getText(0),
							item.getText(1),
							item.getText(2),
							item.getText(3),
							item.getText(4),
							item.getText(5)
					});
					newItem.setImage(new Image[] {
							item.getImage(0),
							item.getImage(1),
							item.getImage(2),
							item.getImage(3),
							item.getImage(4),
							item.getImage(5)
					});
					newItem.setBackground(item.getBackground());
					newItem.setForeground(item.getForeground());
					item.dispose();
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

		Menu contextMenu = new Menu(table);
	    table.setMenu(contextMenu);
	    MenuItem mItem1 = new MenuItem(contextMenu, SWT.None);
	    mItem1.setText("Menu Item Test.");

	    table.addListener(SWT.MouseDown, new Listener(){

	        @Override
	        public void handleEvent(Event event) {
	            TableItem[] selection = table.getSelection();
	            if(selection.length!=0 && (event.button == 3)){
	                contextMenu.setVisible(true);
	            }

	        }

	    });

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
			item.setText(5, entry.getKey().getId().getKey());
		}
		iconColumn.pack();
	}

	public void clearMethodScores() {
		table.removeAll();
	}

}
