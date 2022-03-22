package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import org.eclipse.sed.ifl.ide.gui.element.CodeElementContentProvider;
import org.eclipse.sed.ifl.ide.gui.treeview.ElementNode;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class ScoreListUI extends Composite {

	private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	private static final DecimalFormat LIMIT_FORMAT = new DecimalFormat("#0.0000", symbols);
	
	private TreeViewer viewer;

	@SuppressWarnings("unchecked")
	private void requestNavigateToAllSelection() {
		for (ElementNode selected : (List<ElementNode>)viewer.getStructuredSelection().toList()) {
			Entry<IMethodDescription, Score> codeElement = selected.getCodeElement();
			String path = codeElement.getKey().getLocation().getAbsolutePath();
			int offset = codeElement.getKey().getLocation().getBegining().getOffset();
			System.out.println("navigation requested to: " + path + ":" + offset);
			IMethodDescription entry = codeElement.getKey();
			navigateToRequired.invoke(entry);
		}
	}

	@SuppressWarnings("unchecked")
	private void requestNavigateToContextSelection() {
		for(ElementNode selected : (List<ElementNode>)viewer.getStructuredSelection().toList()) {
			Entry<IMethodDescription, Score> codeElement = selected.getCodeElement();
			navigateToContext.invoke(codeElement);
		}
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
		setLayout(new GridLayout());
		
		Tree tree = new Tree(this, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		tree.setLayoutData(gd_tree);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		viewer = new TreeViewer(tree);
		viewer.setUseHashlookup(true);
		
		final TreeViewerColumn column8 = new TreeViewerColumn(viewer, SWT.LEFT);
	    column8.getColumn().setText("Name");
	    column8.getColumn().setWidth(300);
	    
	    column8.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					cell.setText(entry.getKey().getId().getName());
				} else {
					cell.setText("Code elements");
				}
			}
	    });
		
	    final TreeViewerColumn column0 = new TreeViewerColumn(viewer, SWT.RIGHT);
		column0.getColumn().setText("Score");
		column0.getColumn().setWidth(300);
		
		column0.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					if (entry.getValue().isDefinit()) {
						LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
						cell.setText(LIMIT_FORMAT.format(entry.getValue().getValue()));
					} else {
						cell.setText("undefined");
					}
				}
			}
		});
		
	    final TreeViewerColumn column1 = new TreeViewerColumn(viewer, SWT.LEFT);
	    column1.getColumn().setText("Signature");
	    column1.getColumn().setWidth(300);
	    
	    column1.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					cell.setText(entry.getKey().getId().getSignature());
				}
			}
	    });
	    
	    final TreeViewerColumn column3 = new TreeViewerColumn(viewer, SWT.LEFT);
	    column3.getColumn().setText("Parent type");
	    column3.getColumn().setWidth(300);
	    
	    column3.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					cell.setText(entry.getKey().getId().getParentType());					
				}
			}
	    });
	    
	    final TreeViewerColumn column4 = new TreeViewerColumn(viewer, SWT.LEFT);
	    column4.getColumn().setText("Path");
	    column4.getColumn().setWidth(300);
	    
	    column4.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					cell.setText(entry.getKey().getLocation().getAbsolutePath());
				}
			}
	    });
	    
	    final TreeViewerColumn column5 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column5.getColumn().setText("Position");
	    column5.getColumn().setWidth(300);
	    
	    column5.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					cell.setText(entry.getKey().getLocation().getBegining().getOffset().toString());
				}
			}
	    });
	    
	    final TreeViewerColumn column6 = new TreeViewerColumn(viewer, SWT.RIGHT);
	    column6.getColumn().setText("Context size");
	    column6.getColumn().setWidth(300);
	    
	    column6.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					cell.setText(String.valueOf(entry.getKey().getContext().size()));
				}
			}
	    });
	    
	    final TreeViewerColumn column2 = new TreeViewerColumn(viewer, SWT.LEFT);
	    column2.getColumn().setText("Interactivity");
	    column2.getColumn().setWidth(300);
	    
	    column2.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					if (entry.getKey().isInteractive()) {
						cell.setText("User feedback enabled");
						cell.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
					} else {
						cell.setText("User feedback disabled");
						cell.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
					}
				}
			}
	    });
	    
	    final TreeViewerColumn column7 = new TreeViewerColumn(viewer, SWT.LEFT);
	    column7.getColumn().setText("Last action");
	    column7.getColumn().setWidth(300);
	    
	    column7.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Entry<IMethodDescription, Score> entry = ((ElementNode) (cell.getElement())).getCodeElement();
				if (entry != null) {
					if (entry.getValue().getLastAction() != null) {
						cell.setImage(checkLastAction(entry.getValue().getLastAction()));
					}	
				}
			}
	    });
	       
	    viewer.setContentProvider(new CodeElementContentProvider(viewer));
	}

	private void packColumns() {
		 final TreeColumn[] treeColumns = viewer.getTree().getColumns();

		 for (TreeColumn treeColumn : treeColumns) {
		     treeColumn.pack();
		 }
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

	public void adddMenuToTreeViewer(List<Action> feedbackActions) {
		
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
                    
                    boolean disabledMenu = false;
                    
                    for (Object selectionElement : selection.toList()) {
                    	Entry<IMethodDescription, Score> element = ((ElementNode) selectionElement).getCodeElement();
                    	
                    	if (!element.getValue().isDefinit() || !element.getKey().isInteractive()) {
                    		disabledMenu = true;
                    		break;
                    	}
                    }
                    
                    if (disabledMenu) {
                		manager.add(disabledFeedbackAction);
                	} else {
                		for (Action action : feedbackActions) {
                			manager.add(action);
                		}
                	}
                	
                	manager.add(navigateToSelectedAction);
            		manager.add(navigateToContextAction);
            		manager.add(openDetailsLink);
                }
            }
        });
        
        menuMgr.setRemoveAllWhenShown(true);
        viewer.getControl().setMenu(menu);
	}

	public void setMethodScore(Map<IMethodDescription, Score> scores) {
		ElementNode root = new ElementNode(null, null, null);
		for (Entry<IMethodDescription, Score> element : scores.entrySet()) {
			ElementNode elementNode = new ElementNode(root, null, element);
			root.addChild(elementNode);
		}
		viewer.setInput(root);
		System.out.println(viewer.getTree().getItems().length);
		this.adddMenuToTreeViewer(feedbackActions);
		packColumns();
	}

	public void clearMethodScores() {
		viewer.getControl().setMenu(null);
		viewer.setInput(null);
	}

	Menu contextMenu;
	Menu nonInteractiveContextMenu;

	public void createNonINteractiveContextMenu() {
		
	}

	public void createContexMenu(Iterable<Option> options) {
		feedbackActions = addFeedbackOptionsActions(options);
	}

	private List<Action> feedbackActions;
	
	private Action openDetailsLink = new Action() {

		@Override
		public String getText() {
			return "Open details...";
		}

		@Override
		public void run() {
			for (Object item : viewer.getStructuredSelection().toList()) {
				Entry<IMethodDescription, Score> element = ((ElementNode)item).getCodeElement();
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
	
	private Action disabledFeedbackAction = new Action() {

		@Override
		public String getText() {
			return "(User feedback is disabled or score is undefined)";
		}

		@Override
		public void run() {
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return ImageDescriptor.createFromImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/feedback-disabled16.png"));
		}
		
	};
	
	private Action navigateToSelectedAction = new Action() {

		@Override
		public String getText() {
			return "Navigate to selected";
		}

		@Override
		public void run() {
			requestNavigateToAllSelection();
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return ImageDescriptor.createFromImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/go-to-selected16.png"));
		}
		
	};
	
	
	private Action navigateToContextAction = new Action() {

		@Override
		public String getText() {
			return "Navigate to context";
		}

		@Override
		public void run() {
			requestNavigateToContextSelection();
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return ImageDescriptor.createFromImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/go-to-context16.png"));
		}
		
	};

	private List<Action> addFeedbackOptionsActions(Iterable<Option> options) {
		List<Action> resultList = new ArrayList<>();
		
		for (Option option : options) {
			
			Action action = new Action() {
				
				@Override
				public String getText() {
					return option.getTitle() + (option.getSideEffect() != SideEffect.NOTHING ? " (terminal choice)" : "");
				}

				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					if (option.getId().equals("CONTEXT_BASED_OPTION")) {
							List<IMethodDescription> subjects = (List<IMethodDescription>) viewer.getStructuredSelection().toList().stream()
									.map(selection -> ((ElementNode) selection).getCodeElement().getKey()).collect(Collectors
									.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
							customOptionSelected.invoke(subjects);
					} else {
						Map<IMethodDescription, Defineable<Double>> subjects = new HashMap<>();
						try {
							for (ElementNode element : (List<ElementNode>) viewer.getStructuredSelection().toList()) {
								Entry<IMethodDescription,Score> codeElement = element.getCodeElement();
								assert codeElement instanceof Entry<?, ?>;
								subjects.put(codeElement.getKey(),
										new Defineable<Double>(codeElement.getValue().getValue()));
							}

							UserFeedback feedback = new UserFeedback(option, subjects);
							optionSelected.invoke(feedback);
						} catch (UnsupportedOperationException undefinedScore) {
							MessageDialog.open(MessageDialog.ERROR, null, "Unsupported feedback",
									"Choosing undefined elements to be faulty is unsupported.", SWT.NONE);
						}
					}
				}

				@Override
				public ImageDescriptor getImageDescriptor() {
					if (option.getKind().getIconPath() != null) {
						return ImageDescriptor.createFromImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", option.getKind().getIconPath()));
					} else {
						return super.getImageDescriptor();
					}
				}
			};
			
			resultList.add(action);
		}
		
		return resultList;
	}
	
	private Image checkLastAction(Monument<Score, IMethodDescription, IUserFeedback> lastAction) {
		if (lastAction != null) {
			String iconPath = lastAction.getChange().getIconPath();
			if (iconPath != null) {
				Image icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", iconPath);
				
				return icon;
			}
		} 
		return null;
	}
	
	public void showNoItemsLabel(boolean show) {
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
