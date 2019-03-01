package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;

import java.util.Collections;

import java.text.DecimalFormat;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.model.source.ICodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;

import org.eclipse.swt.graphics.Image;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Slider;


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

	private void requestNavigateToAllSelection() {
		for (TableItem selected : table.getSelection()) {
			String path = selected.getText(table.indexOf(pathColumn));
			int offset = Integer.parseInt(selected.getText(table.indexOf(positionColumn)));
			System.out.println("navigation requested to: " + path + ":" + offset);
			IMethodDescription entry = (IMethodDescription) selected.getData();
			navigateToRequired.invoke(entry.getLocation());
		}
	}


	private NonGenericListenerCollection<Table> selectionChanged = new NonGenericListenerCollection<>();
	private Label minLabel;
	private Label maxLabel;
	private TableColumn interactivityColumn;
	
	public INonGenericListenerCollection<Table> eventSelectionChanged() {
		return selectionChanged;
	}

	private void updateScoreFilterLimit() {
		double value = slider.getSelection() / SLIDER_PRECISION;
		enabledCheckButton.setText("filter scores <= " + new DecimalFormat("#0.0000").format(value));
		enabledCheckButton.requestLayout();
		lowerScoreLimitChanged.invoke(value);
	}

	public ScoreListUI(Composite parent, int style) {
		super(parent, style);
		setLayoutData(BorderLayout.CENTER);
		setLayout(new GridLayout(1, false));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setSize(0, 100);
		composite.setLayout(new GridLayout(4, false));
		
		enabledCheckButton = new Button(composite, SWT.CHECK);
		enabledCheckButton.setToolTipText("enable");
		enabledCheckButton.setEnabled(false);
		enabledCheckButton.setText("load some defined scores to enable this filter");
		enabledCheckButton.setSelection(true);
		enabledCheckButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				lowerScoreLimitEnabled.invoke(enabledCheckButton.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		minLabel = new Label(composite, SWT.NONE);
		minLabel.setText("");

		slider = new Slider(composite, SWT.NONE);
		slider.setEnabled(false);
		slider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider.setSelection(0);
		slider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateScoreFilterLimit();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});

		maxLabel = new Label(composite, SWT.NONE);
		maxLabel.setText("");

		table = new Table(this, SWT.FULL_SELECTION | SWT.MULTI);
		contextMenu = new Menu(table);
		nonInteractiveContextMenu = new Menu(table);
		GridData gd_table = new GridData(SWT.FILL);
		gd_table.grabExcessVerticalSpace = true;
		gd_table.grabExcessHorizontalSpace = true;
		gd_table.verticalAlignment = SWT.FILL;
		gd_table.horizontalAlignment = SWT.FILL;
		table.setLayoutData(gd_table);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out.println("double click on list detected");
				requestNavigateToAllSelection();
			}
		});
		table.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectionChanged.invoke((Table)e.widget);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		table.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Boolean interactivity = Stream.of(table.getSelection())
						.map(selection -> ((Score)selection.getData("score")).isInteractive())
						.reduce((Boolean t, Boolean u) -> t && u).get();
				if (interactivity) {
					table.setMenu(contextMenu);
				} else {
					table.setMenu(nonInteractiveContextMenu);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
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
				NanoWatch watch = new NanoWatch("sorting score-list");
				sortRequired.invoke(arg);
				System.out.println(watch);
			}
		};
		
		for (TableColumn column : table.getColumns()) {
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
		signitureColumn.setText("Signature");

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
		
		interactivityColumn = new TableColumn(table, SWT.NONE);
		interactivityColumn.setWidth(200);
		interactivityColumn.setText("");
	}

	private NonGenericListenerCollection<Double> lowerScoreLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Double> eventlowerScoreLimitChanged() {
		return lowerScoreLimitChanged;
	}

	private NonGenericListenerCollection<Boolean> lowerScoreLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventlowerScoreLimitEnabled() {
		return lowerScoreLimitEnabled;
	}
	
	private NonGenericListenerCollection<ICodeChunkLocation> navigateToRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<ICodeChunkLocation> eventNavigateToRequired() {
		return navigateToRequired;
	}
	
	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	private static final double SLIDER_PRECISION = 1000.0; 
	
	public void setScoreFilter(Double min, Double max) {
		DecimalFormat format = new DecimalFormat("#0.0000");
		maxLabel.setText(format.format(max));
		maxLabel.requestLayout();
		minLabel.setText(format.format(min));
		minLabel.requestLayout();
		slider.setMinimum(Double.valueOf(min * SLIDER_PRECISION).intValue());
		slider.setMaximum(Double.valueOf(max * SLIDER_PRECISION).intValue());
		enabledCheckButton.setEnabled(true);
		slider.setEnabled(true);
		updateScoreFilterLimit();
	}
	
	public void setMethodScore(Map<IMethodDescription, Score> scores) {
		for (Entry<IMethodDescription, Score> entry : scores.entrySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			String iconPath = entry.getValue().getStatus().getIconPath();
			if (iconPath != null) {
				Image icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath);
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
			if (!entry.getValue().isInteractive()) {
				item.setText(table.indexOf(interactivityColumn), "(user feedback disabled)");
				item.setForeground(table.indexOf(interactivityColumn), new Color(item.getDisplay(), 255,100,100));
			}
			item.setData(entry.getKey());
			item.setData("score", entry.getValue());
		}
		iconColumn.pack();
	}

	public void clearMethodScores() {
		table.removeAll();
	}

	Menu contextMenu;
	Menu nonInteractiveContextMenu;
	
	public void createContexMenu(Iterable<Option> options) {
		table.setMenu(contextMenu);
		addFeedbackOptions(options, contextMenu);
		addDisabledFeedbackOptions(nonInteractiveContextMenu);
		addNavigationOptions(contextMenu);
		addNavigationOptions(nonInteractiveContextMenu);
	}

	private void addDisabledFeedbackOptions(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem noFeedback = new MenuItem(menu, SWT.NONE);
		noFeedback.setText("(user feedback is disabled)");
		noFeedback.setToolTipText("User feedback is disabled for some the selected items. Remove these items from the selection to reenable it.");
		noFeedback.setEnabled(false);
	}
	
	private void addNavigationOptions(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem navigateToSelected = new MenuItem(menu, SWT.None);
		navigateToSelected.setText("Navigate to selected");
		navigateToSelected.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				requestNavigateToAllSelection();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		MenuItem navigateToContext = new MenuItem(menu, SWT.None);
		navigateToContext.setEnabled(false);
		navigateToContext.setText("Navigate to context");
	}

	private void addFeedbackOptions(Iterable<Option> options, Menu contextMenu) {
		for (Option option : options) {
			MenuItem item = new MenuItem(contextMenu, SWT.None);
			item.setText(option.getTitle() + (option.getSideEffect()!=SideEffect.NOTHING ? " (terminal choice)" : ""));
			item.setData(option);
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					List<IMethodDescription> subjects = Stream.of(table.getSelection())
							.map(selection -> (IMethodDescription)selection.getData())
							.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
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
	private Composite composite;
	private Button enabledCheckButton;
	private Slider slider;

	public INonGenericListenerCollection<IUserFeedback> eventOptionSelected() {
		return optionSelected;
	}

	public void highlight(List<MethodIdentity> context) {
		for (TableItem item : table.getItems()) {
			item.setBackground(null);
		}
		for (TableItem item : table.getItems()) {
			for (MethodIdentity target : context) {
				if (item.getData() instanceof IMethodDescription &&
					target.equals(((IMethodDescription)item.getData()).getId())) {
					item.setBackground(new Color(item.getDisplay(), 249,205,173));
				}
			}
		}
	}

}
