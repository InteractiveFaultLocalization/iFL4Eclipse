package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.model.source.ICodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out.println("double click on list detected");
				if (table.getSelectionCount() == 1) {
					var selected = table.getSelection()[0];
					var path = selected.getText(table.indexOf(pathColumn));
					var offset = Integer.parseInt(selected.getText(table.indexOf(positionColumn)));
					System.out.println("navigation requested to: " + path + ":" + offset);
					var entry = (IMethodDescription) selected.getData();
					navigateToRequired.invoke(entry.getLocation());
				} else {
					System.out.println("//TODO: handle multiply selection");
				}
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		createColumns();

		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				TableColumn sortColumn = table.getSortColumn();
				int dir = table.getSortDirection();

				TableColumn column = (TableColumn) e.widget;
				SortingArg arg = (SortingArg) column.getData("sort");

				if (sortColumn == column) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					table.setSortColumn(column);
					dir = SWT.DOWN;
				}
				
				table.setSortColumn(column);
				table.setSortDirection(dir);
				
				arg.setDescending(dir == SWT.DOWN);
				var watch = new NanoWatch("sorting score-list");
				sortRequired.invoke(arg);
				System.out.println(watch);
			}
		};
		
		for (var column : table.getColumns()) {
			column.addListener(SWT.Selection, sortListener);
		}
	}

	private void createColumns() {
		iconColumn = new TableColumn(table, SWT.NONE);
		iconColumn.setWidth(32);
		iconColumn.setResizable(false);

		scoreColumn = new TableColumn(table, SWT.NONE);
		scoreColumn.setMoveable(true);
		scoreColumn.setWidth(100);
		scoreColumn.setText("Score");
		scoreColumn.setData("sort", SortingArg.Score);

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

		pathColumn = new TableColumn(table, SWT.NONE);
		pathColumn.setWidth(100);
		pathColumn.setText("Path");

		positionColumn = new TableColumn(table, SWT.NONE);
		positionColumn.setWidth(100);
		positionColumn.setText("Lineinfo");

		contextSizeColumn = new TableColumn(table, SWT.NONE);
		contextSizeColumn.setWidth(100);
		contextSizeColumn.setText("Context size");
	}

	private NonGenericListenerCollection<ICodeChunkLocation> navigateToRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<ICodeChunkLocation> eventNavigateToRequired() {
		return navigateToRequired;
	}
	
	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	public void setMethodScore(Map<IMethodDescription, Score> scores) {
		for (var entry : scores.entrySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			String iconPath = entry.getValue().getStatus().getIconPath();
			if (iconPath != null) {
				var icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath);
				item.setImage(table.indexOf(iconColumn), icon);
			}
			if (entry.getValue().isDefinit()) {
				item.setText(table.indexOf(scoreColumn), String.format("%.4f", entry.getValue().getValue()));
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

	public void createContexMenu(Iterable<Option> options) {
		Menu contextMenu = new Menu(table);
		table.setMenu(contextMenu);
		addFeedbackOptions(options, contextMenu);
		new MenuItem(contextMenu, SWT.SEPARATOR);
		var navigateToSelected = new MenuItem(contextMenu, SWT.None);
		navigateToSelected.setText("Navigate to selected");
	}

	private void addFeedbackOptions(Iterable<Option> options, Menu contextMenu) {
		for (Option option : options) {
			MenuItem item = new MenuItem(contextMenu, SWT.None);
			item.setText(option.getTitle());
			item.setData(option);
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					List<IMethodDescription> subjects = Stream.of(table.getSelection())
						.map(selection -> (IMethodDescription)selection.getData())
						.collect(Collectors.toUnmodifiableList());
					UserFeedback feedback = new UserFeedback(option, subjects);					
					optionSelected.invoke(feedback);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}
	}

	private NonGenericListenerCollection<IUserFeedback> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventOptionSelected() {
		return optionSelected;
	}

}
