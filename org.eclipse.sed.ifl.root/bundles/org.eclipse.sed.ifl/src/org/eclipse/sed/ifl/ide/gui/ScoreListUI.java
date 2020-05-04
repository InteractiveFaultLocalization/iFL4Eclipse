package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;

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
import org.eclipse.sed.ifl.ide.gui.element.CodeElementUI;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;

import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FillLayout;


public class ScoreListUI extends Composite {

	private Label noItemsToDisplayLabel;
	private Button showFilterPart;
	

	/*
	private void requestNavigateToAllSelection() {
		for (TableItem selected : table.getSelection()) {
			String path = selected.getText(table.indexOf(pathColumn));
			int offset = Integer.parseInt(selected.getText(table.indexOf(positionColumn)));
			System.out.println("navigation requested to: " + path + ":" + offset);
			IMethodDescription entry = (IMethodDescription) selected.getData();
			navigateToRequired.invoke(entry);
		}
	}
	*/
	
	/*
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
	*/

	private NonGenericListenerCollection<Table> selectionChanged = new NonGenericListenerCollection<>();

	
	public INonGenericListenerCollection<Table> eventSelectionChanged() {
		return selectionChanged;
	}
	
	
	public ScoreListUI() {
		this(new Shell());
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public ScoreListUI(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(BorderLayout.CENTER);
		setLayout(new GridLayout(1, false));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setSize(0, 100);
		composite.setLayout(new GridLayout(1, false));
		
		showFilterPart = new Button(composite, SWT.NONE);
		showFilterPart.setText("Show filters");
		showFilterPart.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				openFiltersPage.invoke(new EmptyEvent());
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
			
		});
		
		scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_scrolledComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_scrolledComposite.heightHint = 215;
		gd_scrolledComposite.widthHint = 1600;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		cardsComposite = new Composite(scrolledComposite, SWT.NONE);
		cardsComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		cardsComposite.setLayout(new GridLayout(4, false));
		scrolledComposite.setContent(cardsComposite);
		scrolledComposite.setMinSize(cardsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		noItemsToDisplayLabel = new Label(this, SWT.NONE);
		noItemsToDisplayLabel.setText("\nThere are no source code items to display. Please check if you have set the filters in a way that hides all items or if you have marked all items as not suspicious.");
		noItemsToDisplayLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		noItemsToDisplayLabel.setVisible(false);
	}
		
	
	private NonGenericListenerCollection<EmptyEvent> openFiltersPage = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<EmptyEvent> eventOpenFiltersPage() {
		return openFiltersPage;
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
	
	
	

	

	public void setMethodScore(Map<IMethodDescription, Score> scores) {
		//int rowCount = scores.entrySet().size() / 4;
	//	int elementHeight = 0;
		for (Entry<IMethodDescription, Score> entry : scores.entrySet()) {
			CodeElementUI element = new CodeElementUI(cardsComposite, SWT.NONE,
					entry.getValue(),
					entry.getKey().getId().getName(),
					entry.getKey().getId().getSignature(),
					entry.getKey().getId().getParentType(),
					entry.getKey().getLocation().getAbsolutePath(),
					entry.getKey().getLocation().getBegining().getOffset().toString(),
					entry.getKey().getContext().size()+1,
					entry.getKey().isInteractive(),
					entry.getValue().getLastAction());
			element.setData(entry.getKey());
			element.setData("score", entry.getValue());
			element.setData("entry", entry);
			
			/*
			TableItem item = new TableItem(table, SWT.NULL);
			if (entry.getValue().getLastAction() != null) {
				String iconPath = entry.getValue().getLastAction().getCause().getChoice().getKind().getIconPath();
				if (iconPath != null) {
					Image icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath);
					item.setImage(table.indexOf(iconColumn), icon);
				}
			}
			if (entry.getValue().isDefinit()) {
				LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
				item.setText(table.indexOf(scoreColumn), LIMIT_FORMAT.format(entry.getValue().getValue()));
			} else {
				item.setText(table.indexOf(scoreColumn), "undefined");
			}
			item.setText(table.indexOf(nameColumn), entry.getKey().getId().getName());
			item.setText(table.indexOf(signitureColumn), entry.getKey().getId().getSignature());
			item.setText(table.indexOf(typeColumn), entry.getKey().getId().getParentType());
			item.setText(table.indexOf(pathColumn), entry.getKey().getLocation().getAbsolutePath());
			item.setText(table.indexOf(positionColumn),
					entry.getKey().getLocation().getBegining().getOffset().toString());
			item.setText(table.indexOf(contextSizeColumn), entry.getKey().getContext().size()+1 + " methods");
			if (!entry.getKey().isInteractive()) {
				item.setText(table.indexOf(interactivityColumn), "User feedback disabled");
				item.setForeground(table.indexOf(interactivityColumn), new Color(item.getDisplay(), 139,0,0));
			} else {
				item.setText(table.indexOf(interactivityColumn), "User feedback enabled");
				item.setForeground(table.indexOf(interactivityColumn), new Color(item.getDisplay(), 34,139,34));
			}
			item.setData(entry.getKey());
			item.setData("score", entry.getValue());
			item.setData("entry", entry);
			*/
			
			//elementHeight = element.getBounds().height;
		}
		//cardsComposite.setSize(cardsComposite.getSize().x, rowCount * elementHeight);
		//System.out.println("width: " + cardsComposite.getSize().x + ", height: " + rowCount * elementHeight);
		scrolledComposite.setContent(cardsComposite);
	}

	public void clearMethodScores() {
		for(Control control : cardsComposite.getChildren()) {
			control.dispose();
		}
	}

	Menu contextMenu;
	Menu nonInteractiveContextMenu;
	
	/*
	public void createContexMenu(Iterable<Option> options) {
		//It sets the parent of popup menu on the given !!parent's shell!!,
		//because the late parent setters it is not possible to instantiate these before.
		contextMenu = new Menu(cardsComposite);
		nonInteractiveContextMenu = new Menu(cardsComposite);
		
		cardsComposite.setMenu(contextMenu);
		addFeedbackOptions(options, contextMenu);
		addDisabledFeedbackOptions(nonInteractiveContextMenu);
		addNavigationOptions(contextMenu);
		addNavigationOptions(nonInteractiveContextMenu);
		addDetailsOptions(contextMenu);
		addDetailsOptions(nonInteractiveContextMenu);
	}
*/
	
	/*
	private void addDetailsOptions(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem openDetails = new MenuItem(menu, SWT.NONE);
		menu.addMenuListener(new MenuListener() {
			
			@Override
			public void menuShown(MenuEvent e) {
				for (Control item : cardsComposite.getSelection()) {
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
*/
	private void addDisabledFeedbackOptions(Menu menu) {
		MenuItem noFeedback = new MenuItem(menu, SWT.NONE);
		noFeedback.setText("(User feedback is disabled)");
		noFeedback.setToolTipText("User feedback is disabled for some of the selected items. Remove these items from the selection to reenable it.");
		noFeedback.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/feedback-disabled.png"));
		noFeedback.setEnabled(false);
	}
	/*
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
*/
	/*
	private void addFeedbackOptions(Iterable<Option> options, Menu contextMenu) {
		for (Option option : options) {
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
					if(option.getId().equals("CONTEXT_BASED_OPTION")) {
						List<IMethodDescription> subjects = Stream.of(table.getSelection())
								.map(selection -> (IMethodDescription)selection.getData())
								.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
						customOptionSelected.invoke(subjects);
					} else {
						Map<IMethodDescription, Defineable<Double>> subjects = new HashMap<>();							
						List<TableItem> itemList = Arrays.asList(table.getSelection());
						try {
							for(TableItem tableItem : itemList) {
								assert tableItem.getData("entry") instanceof Entry<?, ?>;
								subjects.put(((Entry<IMethodDescription, Score>)(tableItem.getData("entry"))).getKey(),
										new Defineable<Double>(((Entry<IMethodDescription, Score>)(tableItem.getData("entry"))).getValue().getValue()));
							}
						
						UserFeedback feedback = new UserFeedback(option, subjects);
						optionSelected.invoke(feedback);
						} catch (UnsupportedOperationException undefinedScore) {
							MessageDialog.open(MessageDialog.ERROR, null, "Unsupported feedback", "Choosing undefined elements to be faulty is unsupported.", SWT.NONE);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}
	}
*/
	public void showNoItemsLabel(boolean show) {
		cardsComposite.setVisible(!show);
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
	private ScrolledComposite scrolledComposite;
	private Composite cardsComposite;
	
/*
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
	*/
	/*
	public void highlightNonInteractiveContext(List<IMethodDescription> context) {
		if(context != null) {
			for (TableItem item : table.getItems()) {
				item.setBackground(null);
			}
			List<TableItem> elementList = new ArrayList<TableItem>();
			for (TableItem item : table.getItems()) {
				for (IMethodDescription target : context) {
					if (item.getData() instanceof IMethodDescription &&
						target.getId().equals(((IMethodDescription)item.getData()).getId())) {
						elementList.add(item);
					}
				}
			}
			TableItem[] elementArray = new TableItem[elementList.size()];
			table.setSelection(elementList.toArray(elementArray));
		}
	}
	*/

}
