package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class ScoreListUI extends Composite {

	private TreeViewer viewer;

	@SuppressWarnings("unchecked")
	private void requestNavigateToAllSelection() {
		for (Entry<IMethodDescription, Score> selected : (List<Entry<IMethodDescription, Score>>)viewer.getStructuredSelection().toList()) {
			String path = selected.getKey().getLocation().getAbsolutePath();
			int offset = selected.getKey().getLocation().getBegining().getOffset();
			System.out.println("navigation requested to: " + path + ":" + offset);
			IMethodDescription entry = selected.getKey();
			navigateToRequired.invoke(entry);
		}
	}

	@SuppressWarnings("unchecked")
	private void requestNavigateToContextSelection() {
		for(Entry<IMethodDescription, Score> selected : (List<Entry<IMethodDescription, Score>>)viewer.getStructuredSelection().toList()) {
			navigateToContext.invoke(selected);
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
		setLayout(new GridLayout(3, false));
		
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
					//cell.setText(entry.getValue().getLastAction().getChange().name());
					cell.setImage(checkLastAction(entry.getValue().getLastAction()));
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
                    	@SuppressWarnings("unchecked")
						Entry<IMethodDescription, Score> element = (Entry<IMethodDescription, Score>) selectionElement;
                    	
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
		viewer.setInput(scores);
		System.out.println(viewer.getTree().getItems().length);
		this.adddMenuToTreeViewer(feedbackActions);
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
									.map(selection -> ((Entry<IMethodDescription,Score>) selection).getKey()).collect(Collectors
									.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
							customOptionSelected.invoke(subjects);
					} else {
						Map<IMethodDescription, Defineable<Double>> subjects = new HashMap<>();
						try {
							for (Entry<IMethodDescription, Score> element : (List<Entry<IMethodDescription,Score>>) viewer.getStructuredSelection().toList()) {
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
