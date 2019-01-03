package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.ide.accessor.source.EditorAccessor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class ScoreListUI extends Composite {
	private Table table;
	private TableColumn nameColumn;
	private TableColumn iconColumn;
	private TableColumn scoreColumn;
	private TableColumn signitureColumn;
	private TableColumn typeColumn;
	private TableColumn pathColumn;
	private TableColumn positionColumn;
	private TableColumn contextSizeColumn;

	public ScoreListUI(Composite parent, int style) {
		super(parent, style);
		setLayoutData(BorderLayout.CENTER);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out.println("double click");
				if (table.getSelectionCount() == 1) {
					var selected = table.getSelection()[0];
					var path = selected.getText(table.indexOf(pathColumn));
					var offset = Integer.parseInt(selected.getText(table.indexOf(positionColumn)));
					System.out.println("navigation requested to: " + path + ":" + offset);
					// TODO: move this to controll layer ASAP!!
					EditorAccessor accessor = new EditorAccessor();
					accessor.open(path, offset);
				} else {
					// TODO: handle multiply selection
				}
			}
		});
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

		// TODO: update sorting
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
				final int index = table.indexOf(column);
				if (dir == SWT.UP) {
					Arrays.sort(items,
							(TableItem a, TableItem b) -> collator.compare(a.getText(index), b.getText(index)));
				} else if (dir == SWT.DOWN) {
					Arrays.sort(items,
							(TableItem a, TableItem b) -> -collator.compare(a.getText(index), b.getText(index)));
				}
				for (var item : items) {
					TableItem newItem = new TableItem(table, SWT.NONE);
					newItem.setText(new String[] { item.getText(0), item.getText(1), item.getText(2), item.getText(3),
							item.getText(4), item.getText(5) });
					newItem.setImage(new Image[] { item.getImage(0), item.getImage(1), item.getImage(2),
							item.getImage(3), item.getImage(4), item.getImage(5) });
					newItem.setBackground(item.getBackground());
					newItem.setForeground(item.getForeground());
					newItem.setData(item.getData());
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

		pathColumn = new TableColumn(table, SWT.NONE);
		pathColumn.setWidth(100);
		pathColumn.setText("path");

		positionColumn = new TableColumn(table, SWT.NONE);
		positionColumn.setWidth(100);
		positionColumn.setText("line info");

		contextSizeColumn = new TableColumn(table, SWT.NONE);
		contextSizeColumn.setWidth(100);
		contextSizeColumn.setText("context size");
	}

	public void setMethodScore(Map<IMethodDescription, Defineable<Double>> scores, String iconPath) {
		for (Entry<IMethodDescription, Defineable<Double>> entry : scores.entrySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			if (iconPath != null) {
				var icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath);
				item.setImage(table.indexOf(iconColumn), icon);
			}
			if (entry.getValue().isDefinit()) {
				item.setText(table.indexOf(scoreColumn), entry.getValue().getValue().toString());
			} else {
				item.setText(table.indexOf(scoreColumn), "undefined");
			}
			item.setText(table.indexOf(nameColumn), entry.getKey().getId().getName());
			item.setText(table.indexOf(signitureColumn), entry.getKey().getId().getSignature());
			item.setText(table.indexOf(typeColumn), entry.getKey().getId().getParentType());
			item.setText(table.indexOf(pathColumn), entry.getKey().getLocation().getAbsolutePath());
			item.setText(table.indexOf(positionColumn),
					entry.getKey().getLocation().getBegining().getOffset().toString());
			item.setText(table.indexOf(contextSizeColumn), entry.getKey().getContext().size() + " methods");
			item.setData(entry.getKey());
		}
		iconColumn.pack();
	}

	public void clearMethodScores() {
		table.removeAll();
	}

	public void createMenuOptions(Iterable<Option> options) {
		Menu contextMenu = new Menu(table);
		table.setMenu(contextMenu);
		for (Option option : options) {
			MenuItem mItem = new MenuItem(contextMenu, SWT.None);
			mItem.setText(option.getTitle());
			mItem.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					List<IMethodDescription> subjects = new ArrayList<>();

					for (TableItem item : table.getSelection()) {
						subjects.add((IMethodDescription) item.getData());
					}
					Map map = new HashMap<String, List>();
					map.put(option.getId(), subjects);
					optionSelected.invoke(map);

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});

		}

		table.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {
				TableItem[] selection = table.getSelection();
				if (selection.length != 0 && (event.button == 3)) {
					contextMenu.setVisible(true);
				}

			}

		});

	}

	private NonGenericListenerCollection<Map> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Map> eventOptionSelected() {
		return optionSelected;
	}

}
