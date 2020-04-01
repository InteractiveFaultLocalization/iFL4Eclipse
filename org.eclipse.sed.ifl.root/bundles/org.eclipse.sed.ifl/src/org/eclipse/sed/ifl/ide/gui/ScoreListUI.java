package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;


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
	private Label noItemsToDisplayLabel;
	

	private void requestNavigateToAllSelection() {
		for (TableItem selected : table.getSelection()) {
			String path = selected.getText(table.indexOf(pathColumn));
			int offset = Integer.parseInt(selected.getText(table.indexOf(positionColumn)));
			System.out.println("navigation requested to: " + path + ":" + offset);
			IMethodDescription entry = (IMethodDescription) selected.getData();
			navigateToRequired.invoke(entry);
		}
	}
	
	private void requestNavigateToContextSelection() {
		List<IMethodDescription> contextList = new ArrayList<IMethodDescription>();
		
		for (TableItem selected : table.getSelection()) {
			IMethodDescription entry = (IMethodDescription) selected.getData();
			List<MethodIdentity> context = entry.getContext();
			for (TableItem item : table.getItems()) {
				for (MethodIdentity target : context) {
					if (item.getData() instanceof IMethodDescription &&
						target.equals(((IMethodDescription)item.getData()).getId())) {
						contextList.add((IMethodDescription) item.getData());
						String path = item.getText(table.indexOf(pathColumn));
						int offset = Integer.parseInt(item.getText(table.indexOf(positionColumn)));
						System.out.println("navigation requested to: " + path + ":" + offset);
					}
				}
			}
		}
		navigateToContext.invoke(contextList);
	}

	private NonGenericListenerCollection<Table> selectionChanged = new NonGenericListenerCollection<>();
	private Label minLabel;
	private Label maxLabel;
	private TableColumn interactivityColumn;
	
	public INonGenericListenerCollection<Table> eventSelectionChanged() {
		return selectionChanged;
	}
	
	private void updateScoreFilterLimit(double value) {
		scale.setSelection(toScale(value));
		String formattedValue = LIMIT_FORMAT.format(value);
		manualText.setText(formattedValue.replaceAll(",", "."));
		enabledCheckButton.setText("Filter scores <= ");
		enabledCheckButton.requestLayout();
		lowerScoreLimitChanged.invoke(value);
	}
	
	private void updateContextSizeLimit(int value) {
		contextSizeLimitChanged.invoke(value);
	}
	
	private void updateContextSizeRelation(String text) {
		contextSizeRelationChanged.invoke(text);
	}

	public double userInputTextValidator(String input) {
		double returnValue;
		try {
			input.replaceAll(",","." );
			returnValue = Double.parseDouble(input);
		} catch (NumberFormatException e) {
			returnValue = minValue;
			MessageDialog.open(MessageDialog.ERROR, null, "Input format error",
			"User provided upper score limit is not a number." + System.lineSeparator() + 
			"Your input " + input + " could not be interpreted as a number." + System.lineSeparator() +
			"Upper score limit has been set to minimum value.", SWT.NONE);
		}
		return returnValue;
	}
			
	public double userInputRangeValidator(double input) {
		double returnValue = input;
		if (input < minValue) {
			returnValue = minValue;
			MessageDialog.open(MessageDialog.ERROR, null, "Input range error",
			"User provided upper score limit " + LIMIT_FORMAT.format(input) + 
			" is lesser than " + LIMIT_FORMAT.format(minValue) + " minimum value." + System.lineSeparator() + 
			"Upper score limit has been set to minimum value.", SWT.NONE);
		}
		if (input > maxValue) {
			returnValue = minValue;
			MessageDialog.open(MessageDialog.ERROR, null, "Input format error",
			"User provided upper score limit " + LIMIT_FORMAT.format(input) + 
			" is greater than " + LIMIT_FORMAT.format(maxValue) + " maximum value." + System.lineSeparator() + 
			"Upper score limit has been set to minimum value.", SWT.NONE);
		}
		return returnValue;
	}
	
	public ScoreListUI() {
		this(new Shell());
	}
	
	public ScoreListUI(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(BorderLayout.CENTER);
		setLayout(new GridLayout(1, false));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setSize(0, 100);
		composite.setLayout(new GridLayout(7, false));
		
		contextSizeComposite = new Composite(this, SWT.NONE);
		contextSizeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		contextSizeComposite.setSize(0, 100);
		contextSizeComposite.setLayout(new GridLayout(10, false));

		enabledCheckButton = new Button(composite, SWT.CHECK);
		enabledCheckButton.setToolTipText("enable");
		enabledCheckButton.setEnabled(false);
		enabledCheckButton.setText("Load some defined scores to enable this filter");
		enabledCheckButton.setSelection(true);
		enabledCheckButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				lowerScoreLimitEnabled.invoke(enabledCheckButton.getSelection());
				scale.setEnabled(enabledCheckButton.getSelection());
				manualText.setEnabled(enabledCheckButton.getSelection());
				manualButton.setEnabled(enabledCheckButton.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		manualText = new Text(composite, SWT.BORDER);
		manualText.setToolTipText("You may enter the score value manually here");
		manualText.setEnabled(false);
		manualText.addListener(SWT.Traverse, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if(event.detail == SWT.TRAVERSE_RETURN) {
		            double value = userInputTextValidator(manualText.getText());
		            double correctValue = userInputRangeValidator(value);
		            updateScoreFilterLimit(correctValue);
		        }
			}
		});
				
		manualButton = new Button(composite, SWT.NONE);
		manualButton.setText("Apply");
		manualButton.setEnabled(false);
		manualButton.addSelectionListener(new SelectionListener() {
					
			@Override
			public void widgetSelected(SelectionEvent e) {
				double value = userInputTextValidator(manualText.getText());
		        double correctValue = userInputRangeValidator(value);
		        updateScoreFilterLimit(correctValue);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
					
			}
		});

		minLabel = new Label(composite, SWT.NONE);
		minLabel.setText("");
		scale = new Scale(composite, SWT.NONE);
		scale.setEnabled(false);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scale.setSelection(0);
		scale.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				double value = fromScale(scale.getSelection());
				updateScoreFilterLimit(value);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});

		maxLabel = new Label(composite, SWT.NONE);
		maxLabel.setText("");
		
		contextSizeCheckBox = new Button(contextSizeComposite, SWT.CHECK);
		contextSizeCheckBox.setEnabled(false);
		contextSizeCheckBox.setText("Filter context size");
		contextSizeCheckBox.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				contextSizeLimitEnabled.invoke(contextSizeCheckBox.getSelection());
				contextSizeSpinner.setEnabled(contextSizeCheckBox.getSelection());
				contextSizeCombo.setEnabled(contextSizeCheckBox.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		contextSizeCombo = new Combo(contextSizeComposite, SWT.READ_ONLY);
		contextSizeCombo.add("<");
		contextSizeCombo.add("<=");
		contextSizeCombo.add("=");
		contextSizeCombo.add(">=");
		contextSizeCombo.add(">");
		contextSizeCombo.setText("=");
		contextSizeCombo.setEnabled(false);
		contextSizeCombo.addSelectionListener(new SelectionListener () {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String text = contextSizeCombo.getText();
				System.out.println("Combo selected item: "+text);
				updateContextSizeRelation(text);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		contextSizeSpinner = new Spinner(contextSizeComposite, SWT.BORDER);
		contextSizeSpinner.setEnabled(false);
		contextSizeSpinner.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int value = contextSizeSpinner.getSelection();
				updateContextSizeLimit(value);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		noItemsToDisplayLabel = new Label(this, SWT.NONE);
		noItemsToDisplayLabel.setText("\nThere are no source code items to display. Please check if you have set the filters in a way that hides all items or if you have marked all items as not suspicious.");
		GridData gd_label = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		noItemsToDisplayLabel.setLayoutData(gd_label);
		noItemsToDisplayLabel.setVisible(false);
		
		table = new Table(this, SWT.FULL_SELECTION | SWT.MULTI);
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
		iconColumn.setResizable(false);
		iconColumn.setText("Last action");

		scoreColumn = new TableColumn(table, SWT.NONE);
		scoreColumn.setMoveable(true);
		scoreColumn.setWidth(100);
		scoreColumn.setText("Score");
		scoreColumn.setData("sort", SortingArg.Score);

		nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setMoveable(true);
		nameColumn.setWidth(100);
		nameColumn.setText("Name");
		nameColumn.setData("sort", SortingArg.Name);

		signitureColumn = new TableColumn(table, SWT.NONE);
		signitureColumn.setMoveable(true);
		signitureColumn.setWidth(100);
		signitureColumn.setText("Signature");
		signitureColumn.setData("sort", SortingArg.Signature);

		typeColumn = new TableColumn(table, SWT.NONE);
		typeColumn.setMoveable(true);
		typeColumn.setWidth(100);
		typeColumn.setText("Parent type");
		typeColumn.setData("sort", SortingArg.ParentType);

		pathColumn = new TableColumn(table, SWT.NONE);
		pathColumn.setMoveable(true);
		pathColumn.setWidth(100);
		pathColumn.setText("Path");
		pathColumn.setData("sort", SortingArg.Path);

		positionColumn = new TableColumn(table, SWT.NONE);
		positionColumn.setMoveable(true);
		positionColumn.setWidth(100);
		positionColumn.setText("Position");
		positionColumn.setData("sort", SortingArg.Position);

		contextSizeColumn = new TableColumn(table, SWT.NONE);
		contextSizeColumn.setMoveable(true);
		contextSizeColumn.setWidth(100);
		contextSizeColumn.setText("Context size");
		contextSizeColumn.setData("sort", SortingArg.ContextSize);
		
		interactivityColumn = new TableColumn(table, SWT.NONE);
		interactivityColumn.setWidth(200);
		interactivityColumn.setText("Interactivity");
	}
	
	private NonGenericListenerCollection<Double> lowerScoreLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Double> eventlowerScoreLimitChanged() {
		return lowerScoreLimitChanged;
	}

	private NonGenericListenerCollection<Boolean> lowerScoreLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventlowerScoreLimitEnabled() {
		return lowerScoreLimitEnabled;
	}
	
	private NonGenericListenerCollection<Boolean> contextSizeLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventContextSizeLimitEnabled() {
		return contextSizeLimitEnabled;
	}
	
	private NonGenericListenerCollection<Integer> contextSizeLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Integer> eventContextSizeLimitChanged() {
		return contextSizeLimitChanged;
	}
	
	private NonGenericListenerCollection<String> contextSizeRelationChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventContextSizeRelationChanged() {
		return contextSizeRelationChanged;
	}
	
	private NonGenericListenerCollection<IMethodDescription> navigateToRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<IMethodDescription> eventNavigateToRequired() {
		return navigateToRequired;
	}
	
	private NonGenericListenerCollection<List<IMethodDescription>> navigateToContext = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<List<IMethodDescription>> eventNavigateToContext() {
		return navigateToContext;
	}
	
	private NonGenericListenerCollection<IMethodDescription> openDetailsRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<IMethodDescription> eventOpenDetailsRequired() {
		return openDetailsRequired;
	}
	
	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	private static final double SLIDER_PRECISION = 10000.0;
	private static final DecimalFormat LIMIT_FORMAT = new DecimalFormat("#0.0000");
	
	private static int toScale(double value) {
		return Double.valueOf(value * SLIDER_PRECISION).intValue();
	}
	
	private static double fromScale(int value) {
		return value / SLIDER_PRECISION;
	}
	
	private double minValue;
	private double maxValue;
	
	public void setScoreFilter(Double min, Double max) {
		minValue = min;
		maxValue = max;
		maxLabel.setText(LIMIT_FORMAT.format((max)));
		maxLabel.requestLayout();
		minLabel.setText(LIMIT_FORMAT.format((min)));
		minLabel.requestLayout();
		scale.setMaximum(toScale(max));
		System.out.println("Maximum value: " + max);
		System.out.println("Scale maximum selection set: " + toScale(max));
		System.out.println("Scale maximum selection allowed: " + scale.getMaximum());
		scale.setMinimum(toScale(min));
		enabledCheckButton.setEnabled(true);
		enabledCheckButton.setSelection(true);
		lowerScoreLimitEnabled.invoke(true);
		manualText.setEnabled(true);
		manualButton.setEnabled(true);
		scale.setEnabled(true);
		contextSizeCheckBox.setEnabled(true);
		updateScoreFilterLimit(min);
	}

	public void setScoreFilter(Double min, Double max, Double current) {
		setScoreFilter(min, max);
		if (min < current && current < max) {
			scale.setSelection(toScale(current));
			updateScoreFilterLimit(current);
		}
	}

	public void setMethodScore(Map<IMethodDescription, Score> scores) {
		for (Entry<IMethodDescription, Score> entry : scores.entrySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			if (entry.getValue().getLastAction() != null) {
				String iconPath = null;
				try {
				iconPath = entry.getValue().getLastAction().getCause().getChoise().getKind().getIconPath();
				} catch (NullPointerException e) {
					
				}
				if (iconPath != null) {
					Image icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath);
					item.setImage(table.indexOf(iconColumn), icon);
				}
			}
			if (entry.getValue().isDefinit()) {
				//item.setText(table.indexOf(scoreColumn), String.format("%.4f", entry.getValue().getValue()));
				LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
				item.setText(table.indexOf(scoreColumn), LIMIT_FORMAT.format(entry.getValue().getValue()));
				//System.out.println("Entry score value is: " + entry.getValue().getValue());
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
				item.setText(table.indexOf(interactivityColumn), "User feedback disabled");
				item.setForeground(table.indexOf(interactivityColumn), new Color(item.getDisplay(), 139,0,0));
			} else {
				item.setText(table.indexOf(interactivityColumn), "User feedback enabled");
				item.setForeground(table.indexOf(interactivityColumn), new Color(item.getDisplay(), 34,139,34));
			}
			item.setData(entry.getKey());
			item.setData("score", entry.getValue());
			item.setData("entry", entry);
		}
		iconColumn.pack();
	}

	public void clearMethodScores() {
		table.removeAll();
	}

	Menu contextMenu;
	Menu nonInteractiveContextMenu;
	
	public void createContexMenu(Iterable<Option> options) {
		//It sets the parent of popup menu on the given !!parent's shell!!,
		//because the late parent setters it is not possible to instantiate these before.
		contextMenu = new Menu(table);
		nonInteractiveContextMenu = new Menu(table);
		
		table.setMenu(contextMenu);
		addFeedbackOptions(options, contextMenu);
		addDisabledFeedbackOptions(nonInteractiveContextMenu);
		addNavigationOptions(contextMenu);
		addNavigationOptions(nonInteractiveContextMenu);
		addDetailsOptions(contextMenu);
		addDetailsOptions(nonInteractiveContextMenu);
	}

	private void addDetailsOptions(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem openDetails = new MenuItem(menu, SWT.NONE);
		menu.addMenuListener(new MenuListener() {
			
			@Override
			public void menuShown(MenuEvent e) {
				for (TableItem item : table.getSelection()) {
					IMethodDescription sourceItem = (IMethodDescription)item.getData();
					if (sourceItem.hasDetailsLink()) {
						openDetails.setEnabled(true);
						return;
					}
				}
				openDetails.setEnabled(false);
			}
			
			@Override
			public void menuHidden(MenuEvent e) { }
		});
		openDetails.setText("Open details...");
		openDetails.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/open-details16.png"));
		openDetails.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem item : table.getSelection()) {
					IMethodDescription sourceItem = (IMethodDescription)item.getData();
					if (sourceItem.hasDetailsLink()) {
						openDetailsRequired.invoke(sourceItem);
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}

	private void addDisabledFeedbackOptions(Menu menu) {
		MenuItem noFeedback = new MenuItem(menu, SWT.NONE);
		noFeedback.setText("(User feedback is disabled)");
		noFeedback.setToolTipText("User feedback is disabled for some of the selected items. Remove these items from the selection to reenable it.");
		noFeedback.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/feedback-disabled.png"));
		noFeedback.setEnabled(false);
	}
	
	private void addNavigationOptions(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem navigateToSelected = new MenuItem(menu, SWT.None);
		navigateToSelected.setText("Navigate to selected");
		navigateToSelected.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/go-to-selected16.png"));
		navigateToSelected.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				requestNavigateToAllSelection();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		MenuItem navigateToContext = new MenuItem(menu, SWT.None);
		navigateToContext.setText("Navigate to context");
		navigateToContext.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/go-to-context16.png"));
		navigateToContext.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				requestNavigateToContextSelection();
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	private void addFeedbackOptions(Iterable<Option> options, Menu contextMenu) {
		for (Option option : options) {
			if(option.getId().equals("CUSTOM_FEEDBACK")) {
				continue;
			}
			MenuItem item = new MenuItem(contextMenu, SWT.None);
			item.setText(option.getTitle() + (option.getSideEffect()!=SideEffect.NOTHING ? " (terminal choice)" : ""));
			item.setToolTipText(option.getDescription());
			item.setData(option);
			if (option.getKind().getIconPath() != null) {
				item.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", option.getKind().getIconPath()));
			}
			item.addSelectionListener(new SelectionListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void widgetSelected(SelectionEvent e) {
					Map<IMethodDescription, Defineable<Double>> subjects = new HashMap<>();							
					List<TableItem> itemList = Arrays.asList(table.getSelection());
					for(TableItem tableItem : itemList) {
						assert tableItem.getData("entry") instanceof Entry<?, ?>;
						subjects.put(((Entry<IMethodDescription, Score>)(tableItem.getData("entry"))).getKey(),
								new Defineable<Double>(((Entry<IMethodDescription, Score>)(tableItem.getData("entry"))).getValue().getValue()));
					}
					UserFeedback feedback = new UserFeedback(option, subjects);					
					optionSelected.invoke(feedback);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}
		//TODO ne legyen külön menü elem, hanem a basiciflmethodscorehandler option-jébe 
		MenuItem item = new MenuItem(contextMenu, SWT.None);
		item.setText("Context based feedback...");
		item.setToolTipText("Individually change the scores of selected, context and other items.");
		item.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<IMethodDescription> subjects = Stream.of(table.getSelection())
						.map(selection -> (IMethodDescription)selection.getData())
						.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
				customOptionSelected.invoke(subjects);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
	}

	public void showNoItemsLabel(boolean show) {
		table.setVisible(!show);
		noItemsToDisplayLabel.setVisible(show);
	}
	
	private NonGenericListenerCollection<IUserFeedback> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventOptionSelected() {
		return optionSelected;
	}

	private NonGenericListenerCollection<List<IMethodDescription>> customOptionSelected = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<List<IMethodDescription>> eventCustomOptionSelected() {
		return customOptionSelected;
	}

	
	private Composite composite;
	private Composite contextSizeComposite;
	private Combo contextSizeCombo;
	private Button contextSizeCheckBox;
	private Spinner contextSizeSpinner;
	private Button enabledCheckButton;
	private Scale scale;
	private Text manualText;
	private Button manualButton;

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
