package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.ide.gui.element.CardHolderComposite;
import org.eclipse.sed.ifl.ide.gui.element.CodeElementUI;
import org.eclipse.sed.ifl.ide.gui.element.SelectedElementUI;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;



public class ScoreListUI extends Composite {

	private Label noItemsToDisplayLabel;
	private Button showFilterPart;
	
	private HashSet<Entry<IMethodDescription, Score>> selectedSet = new HashSet<Entry<IMethodDescription, Score>>();

	
	private void requestNavigateToAllSelection() {
		if(checkSelectedNotNull()) {	
			for (Entry<IMethodDescription, Score> selected : selectedSet) {
				String path = selected.getKey().getLocation().getAbsolutePath();
				int offset = selected.getKey().getLocation().getBegining().getOffset();
				System.out.println("navigation requested to: " + path + ":" + offset);
				IMethodDescription entry = selected.getKey();
				navigateToRequired.invoke(entry);
			}
		}
	}
	
	
	
	private void requestNavigateToContextSelection() {
		if(checkSelectedNotNull()) {
			
			List<IMethodDescription> contextList = new ArrayList<IMethodDescription>();
			for (Entry<IMethodDescription, Score> selected : selectedSet) {
				IMethodDescription entry = selected.getKey();
				List<MethodIdentity> context = entry.getContext();
				for (Control item : cardsComposite.getDisplayedCards()) {
					for (MethodIdentity target : context) {
						if (item.getData() instanceof IMethodDescription &&
							target.equals(((IMethodDescription)item.getData()).getId())) {
							contextList.add((IMethodDescription) item.getData());
							String path = ((CodeElementUI)item).getPositionValueLabel().getText();
							int offset = Integer.parseInt(((CodeElementUI)item).getPositionValueLabel().getText());
							System.out.println("navigation requested to: " + path + ":" + offset);
						}
					}
				}
			}
			navigateToContext.invoke(contextList);
		}
	}
	

	private boolean checkSelectedNotNull() {
		boolean rValue = true;
		if(selectedSet.isEmpty()) {
			MessageDialog.open(MessageDialog.ERROR, null, "No elements selected", "No code elements are selected. Select some code elements.", SWT.NONE);
			rValue = false;
		}
		return rValue;
	}
	
	private boolean checkSelectedInteractive() {
		boolean rValue = true;
		for(Entry<IMethodDescription, Score> selected : selectedSet) {
			if(!selected.getKey().isInteractive()) {
				MessageDialog.open(MessageDialog.ERROR, null, "Non-interactive elements selected", "Non-interactive code elements are selected. Feedback can only be given on elements whose interactivity is set to enabled.", SWT.NONE);
				rValue = false;
				break;
			}
		}
		return rValue;
	}
	
	private NonGenericListenerCollection<HashSet<Entry<IMethodDescription, Score>>> selectionChanged = new NonGenericListenerCollection<>();

	
	public INonGenericListenerCollection<HashSet<Entry<IMethodDescription, Score>>> eventSelectionChanged() {
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
		setLayout(new GridLayout(3, false));
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setSize(0, 100);
		GridLayout gl_composite = new GridLayout(2, false);
		composite.setLayout(gl_composite);
		
		showFilterPart = new Button(composite, SWT.NONE);
		showFilterPart.setText("Show filters");
		
		noItemsToDisplayLabel = new Label(composite, SWT.CENTER);
		noItemsToDisplayLabel.setAlignment(SWT.CENTER);
		GridData gd_noItemsToDisplayLabel = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_noItemsToDisplayLabel.horizontalIndent = 20;
		noItemsToDisplayLabel.setLayoutData(gd_noItemsToDisplayLabel);
		noItemsToDisplayLabel.setText("\nThere are no source code items to display. Please check if you have set the filters in a way that hides all items or if you have marked all items as not suspicious.");
		noItemsToDisplayLabel.setVisible(false);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		cardsComposite = new CardHolderComposite(this, SWT.NONE);
		GridData gd_cardsComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_cardsComposite.widthHint = 1259;
		cardsComposite.setLayoutData(gd_cardsComposite);
		
		label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.heightHint = 458;
		label.setLayoutData(gd_label);
		
		scrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		GridData gd_scrolledComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_scrolledComposite.widthHint = 384;
		gd_scrolledComposite.heightHint = 467;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		
		selectedComposite = new Composite(scrolledComposite, SWT.NONE);
		selectedComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		selectedComposite.setLayout(new GridLayout(1, false));
		selectedComposite.setSize(new Point(388, 464));
		scrolledComposite.setContent(selectedComposite);
		scrolledComposite.setMinSize(selectedComposite.getSize());
		
		cardsComposite.eventDisplayedPageChanged().add(displayedPageChangedListener);
		
		showFilterPart.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				openFiltersPage.invoke(new EmptyEvent());
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {	
			}
			
		});
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

	public void addListenersAndMenuToCards(List<CodeElementUI> cards) {
		for (CodeElementUI element : cards) {
			
			element.addMouseListener(new MouseAdapter()
			{
				@SuppressWarnings("unchecked")
				public void mouseDown(MouseEvent event)
			    {
			    	if(event.button == 1) {
				    	if(!selectedSet.contains((Entry<IMethodDescription, Score>) ((CodeElementUI)event.widget).getData("entry"))) {
					        ((CodeElementUI)event.widget).setBackground(SWTResourceManager.getColor(103, 198, 235));
					        ((CodeElementUI)event.widget).setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT));
					        for(Control control : element.getChildren()) {
					        	control.setBackground(SWTResourceManager.getColor(103, 198, 235));
					        	if(control.getForeground().equals(SWTResourceManager.getColor(SWT.COLOR_BLACK))) {
					        		control.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT));
					        	}
					        }
					        selectedSet.add((Entry<IMethodDescription, Score>) ((CodeElementUI)event.widget).getData("entry"));
					        addSelectedElementToComposite(((CodeElementUI)event.widget));
					    	selectionChanged.invoke(selectedSet);
					     }
			    	}
			    }
			});
			if(((Score)element.getData("score")).isDefinit() && ((IMethodDescription)element.getData()).isInteractive()) {
				element.setMenu(contextMenu);
			} else {
				element.setMenu(nonInteractiveContextMenu);
			}
			
		}
	}
	
	private void addSelectedElementToComposite(CodeElementUI element) {
		SelectedElementUI selected = new SelectedElementUI(selectedComposite, SWT.NONE, element);
		selected.eventSelectedRemoved().add(selectedRemovedListener);
		selected.requestLayout();
		scrolledComposite.setMinSize(selectedComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void checkSelectedSet() {
		for(CodeElementUI displayed : cardsComposite.getDisplayedCards()) {
				if(selectedSet.contains(displayed.getData("entry"))) {
				   displayed.setBackground(SWTResourceManager.getColor(103, 198, 235));
			       displayed.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT));
			        for(Control control : displayed.getChildren()) {
			        	control.setBackground(SWTResourceManager.getColor(103, 198, 235));
			        	if(control.getForeground().equals(SWTResourceManager.getColor(SWT.COLOR_BLACK))) {
			        		control.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT));
			        	}
					}
				} else {
					displayed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
			        for(Control control : displayed.getChildren()) {
			        	control.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
			        	if(control.getForeground().equals(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT))) {
			        		control.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			        	}
					}
				}
		}
	}
	
	private IListener<List<CodeElementUI>> displayedPageChangedListener = event -> {
		checkSelectedSet();
		addListenersAndMenuToCards(event);
	};
	
	private IListener<Entry<IMethodDescription, Score>> selectedRemovedListener = event -> {
		selectedSet.remove(event);
		checkSelectedSet();
	};
	
	public void setMethodScore(Map<IMethodDescription, Score> scores) {
		cardsComposite.setContent(scores);
		cardsComposite.requestLayout();
	}

	
	public void clearMethodScores() {
		cardsComposite.clearMethodScores();
		selectedSet.clear();
		for(Control control : selectedComposite.getChildren()) {
			control.dispose();
		}
	}

	Menu contextMenu;
	Menu nonInteractiveContextMenu;
	
	public void createNonINteractiveContextMenu() {
		nonInteractiveContextMenu = new Menu(cardsComposite);
		
		addDisabledFeedbackOptions(nonInteractiveContextMenu);
		addNavigationOptions(nonInteractiveContextMenu);
		addDetailsOptions(nonInteractiveContextMenu);
	}
	
	public void createContexMenu(Iterable<Option> options) {
		//It sets the parent of popup menu on the given !!parent's shell!!,
		//because the late parent setters it is not possible to instantiate these before.
		contextMenu = new Menu(cardsComposite);
		nonInteractiveContextMenu = new Menu(cardsComposite);
		
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
				if(checkSelectedNotNull()) {
					for (Entry<IMethodDescription, Score> item : selectedSet) {
						IMethodDescription sourceItem = item.getKey();
						if (sourceItem.hasDetailsLink()) {
							openDetails.setEnabled(true);
							return;
						}
					}
					openDetails.setEnabled(false);
				}
			}
			
			@Override
			public void menuHidden(MenuEvent e) { }
		});
		openDetails.setText("Open details...");
		openDetails.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/open-details16.png"));
		openDetails.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(checkSelectedNotNull()) {
					for (Entry<IMethodDescription, Score> item : selectedSet) {
						IMethodDescription sourceItem = item.getKey();
						if (sourceItem.hasDetailsLink()) {
							openDetailsRequired.invoke(sourceItem);
						}
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}

	private void addDisabledFeedbackOptions(Menu menu) {
		MenuItem noFeedback = new MenuItem(menu, SWT.NONE);
		noFeedback.setText("(User feedback is disabled or score is undefined)");
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
			}
			
		});
	}

	
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

				@Override
				public void widgetSelected(SelectionEvent e) {
					if(checkSelectedNotNull()) {
						if(checkSelectedInteractive()) {
							if(option.getId().equals("CONTEXT_BASED_OPTION")) {
								List<IMethodDescription> subjects = selectedSet.stream()
										.map(selection -> selection.getKey())
										.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
								customOptionSelected.invoke(subjects);
							} else {
								Map<IMethodDescription, Defineable<Double>> subjects = new HashMap<>();							
								try {
									for(Entry<IMethodDescription, Score> element : selectedSet) {
										assert element instanceof Entry<?, ?>;
										subjects.put(element.getKey(),
												new Defineable<Double>(element.getValue().getValue()));
									}
								
								UserFeedback feedback = new UserFeedback(option, subjects);
								optionSelected.invoke(feedback);
								} catch (UnsupportedOperationException undefinedScore) {
									MessageDialog.open(MessageDialog.ERROR, null, "Unsupported feedback", "Choosing undefined elements to be faulty is unsupported.", SWT.NONE);
								}
							}
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}
	}

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
	private CardHolderComposite cardsComposite;
	private ScrolledComposite scrolledComposite;
	private Label label;
	private Composite selectedComposite;
	
	public void highlight(List<MethodIdentity> context) {
			for (Control item : cardsComposite.getDisplayedCards()) {
				((CodeElementUI)item).resetNeutralIcons();
			}
			for (Control item : cardsComposite.getDisplayedCards()) {
				for (MethodIdentity target : context) {
					if (item.getData() instanceof IMethodDescription &&
						target.equals(((IMethodDescription)item.getData()).getId())) {
						((CodeElementUI)item).setContextIcons();
					}
				}
			}
	}
	/*
	public void highlightNonInteractiveContext(List<IMethodDescription> context) {
		if(checkSelectedNotNull()) {
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
	}
	*/

}
