package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.ide.gui.element.CardHolderComposite;
import org.eclipse.sed.ifl.ide.gui.element.CodeElementContentProvider;
import org.eclipse.sed.ifl.ide.gui.element.CodeElementUI;
import org.eclipse.sed.ifl.ide.gui.element.SelectedElementUI;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.MethodIdentity;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

@SuppressWarnings("restriction")
public class ScoreListUI extends Composite {

	private Label noItemsToDisplayLabel;
	private Button showFilterPart;
	private Button showDualListPart;
	private GC gc;
	private Composite composite;
	private CardHolderComposite cardsComposite;
	private ScrolledComposite scrolledComposite;
	private Label label;
	private Composite selectedComposite;
	private TreeViewer viewer;
	
	private IThemeEngine themeEngine = (IThemeEngine) Display.getDefault().getData(
            "org.eclipse.e4.ui.css.swt.theme");

	private List<Entry<IMethodDescription, Score>> selectedList = new ArrayList<Entry<IMethodDescription, Score>>();

	private void requestNavigateToAllSelection() {
		if (checkSelectedNotNull()) {
			for (Entry<IMethodDescription, Score> selected : selectedList) {
				String path = selected.getKey().getLocation().getAbsolutePath();
				int offset = selected.getKey().getLocation().getBegining().getOffset();
				System.out.println("navigation requested to: " + path + ":" + offset);
				IMethodDescription entry = selected.getKey();
				navigateToRequired.invoke(entry);
			}
		}
	}

	private void requestNavigateToContextSelection() {
		if (checkSelectedNotNull()) {
			for(Entry<IMethodDescription, Score> selected : selectedList) {
				navigateToContext.invoke(selected);
			}
		}
	}

	private boolean checkSelectedNotNull() {
		boolean rValue = true;
		if (selectedList.isEmpty()) {
			MessageDialog.open(MessageDialog.ERROR, null, "No elements selected",
					"No code elements are selected. Select some code elements.", SWT.NONE);
			rValue = false;
		}
		return rValue;
	}

	private boolean checkSelectedInteractive() {
		boolean rValue = true;
		for (Entry<IMethodDescription, Score> selected : selectedList) {
			if (!selected.getKey().isInteractive()) {
				MessageDialog.open(MessageDialog.ERROR, null, "Non-interactive elements selected",
						"Non-interactive code elements are selected. Feedback can only be given on elements whose interactivity is set to enabled.",
						SWT.NONE);
				rValue = false;
				break;
			}
		}
		return rValue;
	}

	private boolean checkSelectedUndefined() {
		boolean rValue = true;
		for (Entry<IMethodDescription, Score> selected : selectedList) {
			if (!selected.getValue().isDefinit()) {
				MessageDialog.open(MessageDialog.ERROR, null, "Undefined elements selected",
						"Code elements with undefined scores are selected. Feedback can only be given on elements whose score is defined.",
						SWT.NONE);
				rValue = false;
				break;
			}
		}
		return rValue;
	}
	
	private NonGenericListenerCollection<List<Entry<IMethodDescription, Score>>> selectionChanged = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Entry<IMethodDescription, Score>>> eventSelectionChanged() {
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
		
//		composite = new Composite(this, SWT.NONE);
//		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
//		composite.setSize(0, 100);
//		 
//		GridLayout gl_composite = new GridLayout(2, false);
//		gl_composite.marginWidth = 10;
//		composite.setLayout(gl_composite);
//		
//		showFilterPart = new Button(composite, SWT.NONE);
//		showFilterPart.setText("Show filters");
//		
//		showDualListPart = new Button(composite, SWT.NONE);
//		showDualListPart.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		showDualListPart.setText("Show ordering list");
		
		Tree tree = new Tree(this, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 1));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		viewer = new TreeViewer(tree);
		viewer.setUseHashlookup(true);
		
		final TreeViewerColumn column0 = new TreeViewerColumn(viewer, SWT.LEFT);
		column0.getColumn().setText("Score");
		column0.getColumn().setWidth(300);
		
		column0.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				if (entry.getValue().isDefinit()) {
					cell.setText(entry.getValue().getValue().toString());
				} else {
					cell.setText("undefined");
				}
			}
		});
		
	    final TreeViewerColumn column1 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column1.getColumn().setText("Signature");
	    column1.getColumn().setWidth(300);
	    
	    column1.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				cell.setText(entry.getKey().getId().getSignature());
			}
	    });
	    
	    final TreeViewerColumn column2 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column2.getColumn().setText("Interactivity");
	    column2.getColumn().setWidth(300);
	    
	    column2.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				if (entry.getKey().isInteractive()) {
					cell.setText("User feedback enabled");
					cell.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
				} else {
					cell.setText("User feedback disabled");
					cell.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
				}
			}
	    });
	    
	    final TreeViewerColumn column3 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column3.getColumn().setText("Parent type");
	    column3.getColumn().setWidth(300);
	    
	    column3.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				cell.setText(entry.getKey().getId().getParentType());
			}
	    });
	    
	    final TreeViewerColumn column4 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column4.getColumn().setText("Path");
	    column4.getColumn().setWidth(300);
	    
	    column4.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				cell.setText(entry.getKey().getLocation().getAbsolutePath());
			}
	    });
	    
	    final TreeViewerColumn column5 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column5.getColumn().setText("Position");
	    column5.getColumn().setWidth(300);
	    
	    column5.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				cell.setText(entry.getKey().getLocation().getBegining().toString());
			}
	    });
	    
	    final TreeViewerColumn column6 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column6.getColumn().setText("Context size");
	    column6.getColumn().setWidth(300);
	    
	    column6.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				cell.setText(String.valueOf(entry.getKey().getContext().size()));
			}
	    });
	    
	    final TreeViewerColumn column7 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column7.getColumn().setText("Last action");
	    column7.getColumn().setWidth(300);
	    
	    column7.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				if (entry.getValue().getLastAction() != null) {
					cell.setText(entry.getValue().getLastAction().getChange().name());
				} else {
					cell.setText("No actions have been performed on this element yet");
				}			
			}
	    });
	    
	    final TreeViewerColumn column8 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column8.getColumn().setText("Name");
	    column8.getColumn().setWidth(300);
	    
	    column8.setLabelProvider(new CellLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = (Entry<IMethodDescription, Score>) cell.getElement();
				cell.setText(entry.getKey().getId().getName());
			}
	    });
	    
	    viewer.setContentProvider(new CodeElementContentProvider());
	    
	    /*
	    noItemsToDisplayLabel = new Label(composite, SWT.CENTER);
	    noItemsToDisplayLabel.setAlignment(SWT.CENTER);
	    GridData gd_noItemsToDisplayLabel = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
	    gd_noItemsToDisplayLabel.horizontalIndent = 20;
	    noItemsToDisplayLabel.setLayoutData(gd_noItemsToDisplayLabel);
	    noItemsToDisplayLabel.setText("\nThere are no source code items to display. Please check if you have set the filters in a way that hides all items or if you have marked all items as not suspicious.");
	    noItemsToDisplayLabel.setVisible(false);
		*/
//		showFilterPart.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				openFiltersPage.invoke(new EmptyEvent());
//				
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {	
//			}
//			
//		});
//		
//		showDualListPart.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				openDualListPage.invoke(new EmptyEvent());
//				
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {	
//			}
//			
//		});
		
		
	
	}
		
	
	

	private NonGenericListenerCollection<EmptyEvent> openFiltersPage = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<EmptyEvent> eventOpenFiltersPage() {
		return openFiltersPage;
	}
	
	private NonGenericListenerCollection<EmptyEvent> openDualListPage = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<EmptyEvent> eventOpenDualListPage() {
		return openDualListPage;
	}

	private NonGenericListenerCollection<IMethodDescription> navigateToRequired = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IMethodDescription> eventNavigateToRequired() {
		return navigateToRequired;
	}

	private NonGenericListenerCollection<Entry<IMethodDescription, Score>> navigateToContext = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Entry<IMethodDescription, Score>> eventNavigateToContext() {
		return navigateToContext;
	}

	private NonGenericListenerCollection<IMethodDescription> openDetailsRequired = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IMethodDescription> eventOpenDetailsRequired() {
		return openDetailsRequired;
	}

	public void adddMenuToTreeViewer() {
		
		MenuManager menuMgr = new MenuManager();

        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                if (viewer.getSelection().isEmpty()) {
                    return;
                }

                if (viewer.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                    
                    for (Object selectionElement : selection.toList()) {
                    	@SuppressWarnings("unchecked")
						Entry<IMethodDescription, Score> element = (Entry<IMethodDescription, Score>) selectionElement;
                    	
                    	if (element.getValue().isDefinit()) {
                    		manager.add(null);
                    	} else {
                    		manager.add(null);
                    	}
                    }
                }
            }
        });
        menuMgr.setRemoveAllWhenShown(true);
        viewer.getControl().setMenu(menu);
	}

	public void setMethodScore(Map<IMethodDescription, Score> scores) {
		viewer.setInput(scores);
		System.out.println(viewer.getTree().getItems().length);
		this.adddMenuToTreeViewer();
	}

	public void clearMethodScores() {
		viewer.getControl().setMenu(null);
		viewer.setInput(null);
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
		// It sets the parent of popup menu on the given !!parent's shell!!,
		// because the late parent setters it is not possible to instantiate these
		// before.
		contextMenu = new Menu(cardsComposite);
		nonInteractiveContextMenu = new Menu(cardsComposite);

		addFeedbackOptions(options, contextMenu);
		addDisabledFeedbackOptions(nonInteractiveContextMenu);
		addNavigationOptions(contextMenu);
		addNavigationOptions(nonInteractiveContextMenu);
		addDetailsOptions(contextMenu);
		addDetailsOptions(nonInteractiveContextMenu);
	}

	private Action openDetailsLink = new Action() {

		@Override
		public String getText() {
			return "Open details...";
		}

		@Override
		public void run() {
			for (Object item : viewer.getStructuredSelection().toList()) {
				@SuppressWarnings("unchecked")
				Entry<IMethodDescription, Score> element = (Entry<IMethodDescription, Score>) item;
				IMethodDescription sourceItem = element.getKey();
				if (sourceItem.hasDetailsLink()) {
					openDetailsRequired.invoke(sourceItem);
				}
			}
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return ImageDescriptor.createFromImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/open-details16.png"));
		}
		
	};
	
	private void addDetailsOptions(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem openDetails = new MenuItem(menu, SWT.NONE);
		menu.addMenuListener(new MenuListener() {

			@Override
			public void menuShown(MenuEvent e) {
				if (checkSelectedNotNull()) {
					for (Entry<IMethodDescription, Score> item : selectedList) {
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
			public void menuHidden(MenuEvent e) {
			}
		});
		openDetails.setText("Open details...");
		openDetails.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/open-details16.png"));
		openDetails.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (checkSelectedNotNull()) {
					for (Entry<IMethodDescription, Score> item : selectedList) {
						IMethodDescription sourceItem = item.getKey();
						if (sourceItem.hasDetailsLink()) {
							openDetailsRequired.invoke(sourceItem);
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void addDisabledFeedbackOptions(Menu menu) {
		MenuItem noFeedback = new MenuItem(menu, SWT.NONE);
		noFeedback.setText("(User feedback is disabled or score is undefined)");
		noFeedback.setToolTipText(
				"User feedback is disabled for some of the selected items. Remove these items from the selection to reenable it.");
		noFeedback.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/feedback-disabled.png"));
		noFeedback.setEnabled(false);
	}

	private void addNavigationOptions(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem navigateToSelected = new MenuItem(menu, SWT.None);
		navigateToSelected.setText("Navigate to selected");
		navigateToSelected
				.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/go-to-selected16.png"));
		navigateToSelected.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				requestNavigateToAllSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
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
			item.setText(
					option.getTitle() + (option.getSideEffect() != SideEffect.NOTHING ? " (terminal choice)" : ""));
			item.setToolTipText(option.getDescription());
			item.setData(option);
			if (option.getKind().getIconPath() != null) {
				item.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", option.getKind().getIconPath()));
			}
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (checkSelectedNotNull()) {
						if (checkSelectedInteractive()) {
							if (option.getId().equals("CONTEXT_BASED_OPTION")) {
								if (checkSelectedUndefined()) {
									List<IMethodDescription> subjects = selectedList.stream()
											.map(selection -> selection.getKey()).collect(Collectors
											.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
									customOptionSelected.invoke(subjects);
								}
							} else {
								Map<IMethodDescription, Defineable<Double>> subjects = new HashMap<>();
								try {
									for (Entry<IMethodDescription, Score> element : selectedList) {
										assert element instanceof Entry<?, ?>;
										subjects.put(element.getKey(),
												new Defineable<Double>(element.getValue().getValue()));
									}

									UserFeedback feedback = new UserFeedback(option, subjects);
									optionSelected.invoke(feedback);
								} catch (UnsupportedOperationException undefinedScore) {
									MessageDialog.open(MessageDialog.ERROR, null, "Unsupported feedback",
											"Choosing undefined elements to be faulty is unsupported.", SWT.NONE);
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
		//cardsComposite.setVisible(!show);
		//noItemsToDisplayLabel.setVisible(show);
	}

	private NonGenericListenerCollection<IUserFeedback> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventOptionSelected() {
		return optionSelected;
	}

	private NonGenericListenerCollection<List<IMethodDescription>> customOptionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<IMethodDescription>> eventCustomOptionSelected() {
		return customOptionSelected;
	}
}
