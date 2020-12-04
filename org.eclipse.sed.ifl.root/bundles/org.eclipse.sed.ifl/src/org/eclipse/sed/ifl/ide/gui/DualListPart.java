package org.eclipse.sed.ifl.ide.gui;

import java.util.ArrayList;
import java.util.List;
import javax.inject.*;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.sed.ifl.control.ItemMoveObject;
import org.eclipse.sed.ifl.control.OperationType;
import org.eclipse.sed.ifl.control.score.Sortable;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

enum SelectionLocation {
	LEFT, RIGHT, UNSELECTED
}

public class DualListPart<TItem extends Sortable> extends ViewPart implements IEmbeddable, IEmbedee {
	public static final String ID = "org.eclipse.sed.ifl.views.IFLDualListView";

	@Inject
	IWorkbench workbench;

	private Composite composite;
	private static Boolean orderingEnabled = false;
	private Sortable toggleElement;
	private int elementIndex;
	private ItemMoveObject<TItem> moveObject;

	private SelectionLocation whichList;
	private OperationType operationType;

	@FunctionalInterface
	public interface HumanReadable<TItem> {
		String getAsString(TItem t);
	}

	private Button allRight;
	private Button allUp;
	private Button oneRight;
	private Button oneUp;
	private Button oneLeft;
	private Button oneDown;
	private Button allLeft;
	private Button allDown;
	private GridLayout gridLayout;
	private Label infoLabel;
	private Table attributeTable;
	private Table sortingTable;
	private TableViewer attributeViewer;
	private TableViewer sortingViewer;

	public DualListPart() {
		System.out.println("dual list part ctr");

	}

	private void addUIElements(Composite parent) {
		toggleElement = null;
		elementIndex = 0;
		whichList = SelectionLocation.UNSELECTED;
		operationType = OperationType.ADD;

		GridData buttonData = new GridData();
		buttonData.horizontalAlignment = SWT.CENTER;
		buttonData.verticalAlignment = SWT.CENTER;

		GridData labelData = new GridData();
		labelData.horizontalAlignment = SWT.CENTER;
		labelData.verticalAlignment = SWT.TOP;

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 6;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.heightHint = 70;

		infoLabel = new Label(parent, SWT.NONE);
		infoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		infoLabel.setText("Load some defined scores to enable ordering.");

		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");
		new Label(parent, SWT.NONE).setText("");

		attributeViewer = new TableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		attributeViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn columnLeftName = new TableViewerColumn(attributeViewer, SWT.CENTER);
		columnLeftName.getColumn().setWidth(200);
		columnLeftName.getColumn().setText("Attribute");
		columnLeftName.getColumn().setResizable(true);
		columnLeftName.getColumn().setMoveable(true);
		columnLeftName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Sortable) element).getName();
			}
		});
		attributeViewer.getControl().setLayoutData(gridData);

		attributeTable = attributeViewer.getTable();
		attributeTable.setHeaderVisible(false);
		attributeTable.setLinesVisible(false);
		attributeTable.setVisible(false);
		attributeTable.setEnabled(false);

		new Label(parent, SWT.NONE).setText("");

		sortingViewer = new TableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		sortingViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn columnRightName = new TableViewerColumn(sortingViewer, SWT.CENTER);
		TableViewerColumn columnRightButton = new TableViewerColumn(sortingViewer, SWT.CENTER);
		columnRightName.getColumn().setWidth(200);
		columnRightName.getColumn().setText("Attribute");
		columnRightName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Sortable) element).getName();
			}
		});
		columnRightButton.getColumn().setWidth(200);
		columnRightButton.getColumn().setText("Ordering");
		columnRightButton.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {

				TableItem item = (TableItem) cell.getItem();
				toggleElement = (Sortable) item.getData();
				Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.TOGGLE);
				// Sortable helyett
				// egyedi kulcs
				if (toggleElement.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
					button.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/ascend.png"));
					button.setSelection(false);
				} else {
					button.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/descend.png"));
					button.setSelection(true);
				}
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				item.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						button.dispose();
						editor.dispose();
					}
				});
				editor.setEditor(button, item, cell.getColumnIndex());
				button.addSelectionListener(new sortDirectionImageListener());
				editor.layout();
			}

		});
		sortingViewer.getControl().setLayoutData(gridData);

		sortingTable = sortingViewer.getTable();
		sortingTable.setHeaderVisible(false);
		sortingTable.setLinesVisible(false);
		sortingTable.setVisible(false);
		sortingTable.setEnabled(false);

		new Label(parent, SWT.NONE).setText("");

		allRight = new Button(parent, SWT.PUSH);
		allRight.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_all_right.png"));
		allRight.setLayoutData(buttonData);
		allRight.setSize(40, 40);
		allRight.setVisible(false);
		allRight.setEnabled(false);

		allUp = new Button(parent, SWT.PUSH);
		allUp.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_to_top.png"));
		allUp.setLayoutData(buttonData);
		allUp.setSize(40, 40);
		allUp.setVisible(false);
		allUp.setEnabled(false);

		oneRight = new Button(parent, SWT.PUSH);
		oneRight.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_one_right.png"));
		oneRight.setLayoutData(buttonData);
		oneRight.setSize(40, 40);
		oneRight.setVisible(false);
		oneRight.setEnabled(false);

		oneUp = new Button(parent, SWT.PUSH);
		oneUp.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_up.png"));
		oneUp.setLayoutData(buttonData);
		oneUp.setSize(40, 40);
		oneUp.setVisible(false);
		oneUp.setEnabled(false);

		oneLeft = new Button(parent, SWT.PUSH);
		oneLeft.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_one_left.png"));
		oneLeft.setLayoutData(buttonData);
		oneLeft.setSize(40, 40);
		oneLeft.setVisible(false);
		oneLeft.setEnabled(false);

		oneDown = new Button(parent, SWT.PUSH);
		oneDown.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_down.png"));
		oneDown.setLayoutData(buttonData);
		oneDown.setSize(40, 40);
		oneDown.setVisible(false);
		oneDown.setEnabled(false);

		allLeft = new Button(parent, SWT.PUSH);
		allLeft.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_all_left.png"));
		allLeft.setLayoutData(buttonData);
		allLeft.setSize(40, 40);
		allLeft.setVisible(false);
		allLeft.setEnabled(false);

		allDown = new Button(parent, SWT.PUSH);
		allDown.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_to_bottom.png"));
		allDown.setLayoutData(buttonData);
		allDown.setSize(40, 40);
		allDown.setVisible(false);
		allDown.setEnabled(false);

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

	}

	@Override
	public void embed(IEmbeddable embedded) {
		embedded.setParent(composite);
	}

	@Override
	public void setParent(Composite parent) {
		composite.setParent(parent);
	}

	public String getArrayElementbyIndex(Table source, int extractIndex) {
		TableItem extractedItem = source.getItem(extractIndex);
		Sortable argument = (Sortable) extractedItem.getData();
		return argument.getName();
	}

	public String getInfoLabel() {
		return infoLabel.getText();
	}

	public void setInfoLabel(String labelText) {
		this.infoLabel.setText(labelText);
	}

	public Image getAllRightImage() {
		return allRight.getImage();
	}

	public void setAllRightImage(Image buttonImage) {
		this.allRight.setImage(buttonImage);
	}

	public Image getAllUpImage() {
		return allUp.getImage();
	}

	public void setAllUpImage(Image buttonImage) {
		this.allUp.setImage(buttonImage);
	}

	public Image getOneRightImage() {
		return oneRight.getImage();
	}

	public void setOneRightImage(Image buttonImage) {
		this.oneRight.setImage(buttonImage);
	}

	public Image getOneUpImage() {
		return oneUp.getImage();
	}

	public void setOneUpImage(Image buttonImage) {
		this.oneUp.setImage(buttonImage);
	}

	public Image getOneLeftImage() {
		return oneLeft.getImage();
	}

	public void setOneLeftImage(Image buttonImage) {
		this.oneLeft.setImage(buttonImage);
	}

	public Image getOneDownImage() {
		return oneDown.getImage();
	}

	public void setOneDownImage(Image buttonImage) {
		this.oneDown.setImage(buttonImage);
	}

	public Image getAllLeftImage() {
		return allLeft.getImage();
	}

	public void setAllLeftImage(Image buttonImage) {
		this.allLeft.setImage(buttonImage);
	}

	public Image getAllDownImage() {
		return allDown.getImage();
	}

	public void setAllDownImage(Image buttonImage) {
		this.allDown.setImage(buttonImage);
	}

	public ArrayList<Sortable> getSortingTable() {
		TableItem tableItems[] = this.sortingTable.getItems();
		ArrayList<Sortable> sortingList = new ArrayList<Sortable>();
		for (TableItem item : tableItems) {
			Sortable sortable = (Sortable) item.getData();
			sortingList.add(sortable);
		}
		return sortingList;
	}

	public void setSortingTable(List<Sortable> sortingList) {
		sortingTable.removeAll();
		sortingViewer.setInput(sortingList);
		sortingViewer.refresh();
	}

	public ArrayList<Sortable> getAttributeTable() {
		TableItem tableItems[] = this.attributeTable.getItems();
		ArrayList<Sortable> attributeList = new ArrayList<Sortable>();
		for (TableItem item : tableItems) {
			Sortable argument = (Sortable) item.getData();
			attributeList.add(argument);
		}
		return attributeList;
	}

	public void setAttributeTable(List<Sortable> argumentList) {
		attributeViewer.setInput(argumentList);
		attributeViewer.refresh();
	}

	public class sortDirectionImageListener implements SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Button source = (Button) e.getSource();
			if (!source.getSelection()) {
				source.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/ascend.png"));
				DualListPart.this.toggleElement.setSortingDirection(Sortable.SortingDirection.Descending);
			} else {
				source.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/descend.png"));
				DualListPart.this.toggleElement.setSortingDirection(Sortable.SortingDirection.Ascending);
			}
			orderingDirectionChangedListener.invoke(DualListPart.this.toggleElement);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	@Override
	public void createPartControl(Composite parent) {

		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.makeColumnsEqualWidth = false;
		composite = parent;
		composite.setLayout(gridLayout);
		addUIElements(parent);

		attributeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				elementIndex = attributeTable.getSelectionIndex();
				whichList = SelectionLocation.LEFT;
				sortingTable.setSelection(-1);
				selectionRequested.invoke(elementIndex);
			}
		});

		sortingViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				elementIndex = sortingTable.getSelectionIndex();
				whichList = SelectionLocation.RIGHT;
				attributeTable.setSelection(-1);
				selectionRequested.invoke(elementIndex);
			}
		});

		allRight.addListener(SWT.Selection, event -> {
			operationType = OperationType.MOVEALL;
			moveObject = new ItemMoveObject<TItem>(elementIndex, 0, operationType);
			sortingListChangeRequestedListener.invoke(moveObject);
			whichList = SelectionLocation.UNSELECTED;
		});

		oneRight.addListener(SWT.Selection, event -> {
			operationType = OperationType.ADD;
			moveObject = new ItemMoveObject<TItem>(elementIndex, 0, operationType);
			if (whichList == SelectionLocation.LEFT) {
				sortingListChangeRequestedListener.invoke(moveObject);
				whichList = SelectionLocation.RIGHT;
				elementIndex = sortingTable.getItemCount();
				selectionRequested.invoke(elementIndex);

			}
			if (whichList == SelectionLocation.RIGHT) {
				throw new UnsupportedOperationException("The selected item is already in the sorting list.");
			} else {
				throw new UnsupportedOperationException("No item is selected.");
			}
		});

		oneLeft.addListener(SWT.Selection, event -> {
			operationType = OperationType.ADD;
			moveObject = new ItemMoveObject<TItem>(elementIndex, 0, operationType);
			if (whichList == SelectionLocation.LEFT) {
				throw new UnsupportedOperationException("The selected item is already in the attribute list.");
			}
			if (whichList == SelectionLocation.RIGHT) {
				attributeListChangeRequestedListener.invoke(moveObject);
				whichList = SelectionLocation.LEFT;
				elementIndex = attributeTable.getItemCount();
				selectionRequested.invoke(elementIndex);
			} else {
				throw new UnsupportedOperationException("No item is selected.");
			}
		});

		allLeft.addListener(SWT.Selection, event -> {
			operationType = OperationType.MOVEALL;
			moveObject = new ItemMoveObject<TItem>(elementIndex, 0, operationType);
			attributeListChangeRequestedListener.invoke(moveObject);
			whichList = SelectionLocation.UNSELECTED;
		});

		allUp.addListener(SWT.Selection, event -> {
			operationType = OperationType.ALLUP;
			moveObject = new ItemMoveObject<TItem>(elementIndex, 0, operationType);
			if (whichList == SelectionLocation.LEFT) {
				attributeListButtonPressedListener.invoke(moveObject);
			}
			if (whichList == SelectionLocation.RIGHT) {
				sortingListButtonPressedListener.invoke(moveObject);
			} else {
				throw new UnsupportedOperationException("No item is selected.");
			}
		});

		oneUp.addListener(SWT.Selection, event -> {
			operationType = OperationType.ONEUP;
			moveObject = new ItemMoveObject<TItem>(elementIndex, 0, operationType);
			if (whichList == SelectionLocation.LEFT) {
				attributeListButtonPressedListener.invoke(moveObject);
			}
			if (whichList == SelectionLocation.RIGHT) {
				sortingListButtonPressedListener.invoke(moveObject);
			} else {
				throw new UnsupportedOperationException("No item is selected.");
			}
		});

		oneDown.addListener(SWT.Selection, event -> {
			operationType = OperationType.ONEDOWN;
			moveObject = new ItemMoveObject<TItem>(elementIndex, 0, operationType);
			if (whichList == SelectionLocation.LEFT) {
				attributeListButtonPressedListener.invoke(moveObject);
			}
			if (whichList == SelectionLocation.RIGHT) {
				sortingListButtonPressedListener.invoke(moveObject);
			} else {
				throw new UnsupportedOperationException("No item is selected.");
			}
		});

		allDown.addListener(SWT.Selection, event -> {
			operationType = OperationType.ALLDOWN;
			moveObject = new ItemMoveObject<TItem>(elementIndex, 0, operationType);
			if (whichList == SelectionLocation.LEFT) {
				attributeListButtonPressedListener.invoke(moveObject);
			}
			if (whichList == SelectionLocation.RIGHT) {
				sortingListButtonPressedListener.invoke(moveObject);
			} else {
				throw new UnsupportedOperationException("No item is selected.");
			}
		});

		if (orderingEnabled.booleanValue()) {
			enableOrdering();
		}

	}

	private NonGenericListenerCollection<Sortable> orderingDirectionChangedListener = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventOrderingDirectionChanged() {
		return orderingDirectionChangedListener;
	}

	private NonGenericListenerCollection<Integer> selectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSelectionRequested() {
		return selectionRequested;
	}

	private NonGenericListenerCollection<ItemMoveObject<TItem>> sortingListChangeRequestedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventSortingListChangeRequested() {
		return sortingListChangeRequestedListener;
	}

	private NonGenericListenerCollection<ItemMoveObject<TItem>> attributeListButtonPressedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventAttributeListButtonPressed() {
		return attributeListButtonPressedListener;
	}

	private NonGenericListenerCollection<ItemMoveObject<TItem>> attributeListChangeRequestedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventAttributeListChangeRequested() {
		return attributeListChangeRequestedListener;
	}

	private NonGenericListenerCollection<ItemMoveObject<TItem>> sortingListButtonPressedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventSortingListButtonPressed() {
		return sortingListButtonPressedListener;
	}

	private NonGenericListenerCollection<List<Sortable>> attributeListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventAttributeListRefreshRequested() {
		return attributeListRefreshRequested;
	}

	private NonGenericListenerCollection<List<Sortable>> sortingListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventSortingListRefreshRequested() {
		return sortingListRefreshRequested;
	}

	public void enableOrdering() {

		infoLabel.setText("Order your scores by (multiple) attributes.");
		attributeTable.setHeaderVisible(true);
		attributeTable.setLinesVisible(true);
		attributeTable.setVisible(true);
		attributeTable.setEnabled(true);
		sortingTable.setHeaderVisible(true);
		sortingTable.setLinesVisible(true);
		sortingTable.setVisible(true);
		sortingTable.setEnabled(true);

		allRight.setVisible(true);
		allRight.setEnabled(true);
		oneRight.setVisible(true);
		oneRight.setEnabled(true);
		allLeft.setVisible(true);
		allLeft.setEnabled(true);
		oneLeft.setVisible(true);
		oneLeft.setEnabled(true);
		allUp.setVisible(true);
		allUp.setEnabled(true);
		oneUp.setVisible(true);
		oneUp.setEnabled(true);
		allDown.setVisible(true);
		allDown.setEnabled(true);
		oneDown.setVisible(true);
		oneDown.setEnabled(true);

		orderingEnabled = true;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {

		this.getSite().getPage().hideView(this);
	}
}
