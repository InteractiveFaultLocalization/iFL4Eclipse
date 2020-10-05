package org.eclipse.sed.ifl.ide.gui;

import java.util.ArrayList;
import javax.inject.*;

import org.eclipse.sed.ifl.ide.gui.element.DualListElement;
import org.eclipse.sed.ifl.control.ItemMoveObject;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

enum SelectionLocation {
	LEFT, RIGHT, UNSELECTED
}

public class DualListPart<TItem> extends ViewPart implements IEmbeddable, IEmbedee {

	public static final String ID = "org.eclipse.sed.ifl.views.IFLDualListView";

	@Inject
	IWorkbench workbench;

	private Composite composite;

	private ArrayList<TItem> arrayLeft;
	private ArrayList<TItem> arrayRight;

	private static ArrayList<DualListElement> arrayLeftBackup = new ArrayList<>();
	private static ArrayList<DualListElement> arrayRightBackup = new ArrayList<>();

	private static Boolean orderingEnabled = false;

	private TItem selectedItem;
	private int newIndex;
	private TItem swap;
	private SelectionLocation whichList;
	private int itemIndex;
	private int toggleIndex;
	private ItemMoveObject<TItem> moveItem;
	private DualListElement currentElement;

	@FunctionalInterface
	public interface elementStringer<TItem> {
		String getAsString(TItem t);
	}

	private Label leftLabel;
	private Label rightLabel;
	private Label infoLabel;
	private Label descendingLabel;
	private List listLeft;
	private List listRight;
	private Button allRight;
	private Button allUp;
	private Button oneRight;
	private Button oneUp;
	private Button oneLeft;
	private Button oneDown;
	private Button allLeft;
	private Button allDown;
	private Button scoreToggle;
	private Button nameToggle;
	private Button signatureToggle;
	private Button parentTypeToggle;
	private Button pathToggle;
	private Button contextSizeToggle;
	private Button positionToggle;
	private Button interactivityToggle;
	private Button lastActionToggle;
	private Image allRightImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_all_right.png");
	private Image allLeftImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_all_left.png");
	private Image allUpImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_to_top.png");
	private Image allDownImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_to_bottom.png");
	private Image oneRightImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_one_right.png");
	private Image oneLeftImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_one_left.png");
	private Image oneUpImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_up.png");
	private Image oneDownImage = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/move_down.png");
	private GridLayout gridLayout;

	public DualListPart() {
		System.out.println("dual list part ctr");

	}

	private void addUIElements(Composite parent) {
		selectedItem = null;
		currentElement = null;
		newIndex = 0;
		swap = null;
		whichList = SelectionLocation.UNSELECTED;

		arrayLeft = new ArrayList<TItem>();
		arrayRight = new ArrayList<TItem>();

		GridData listData = new GridData();
		listData.horizontalAlignment = SWT.CENTER;
		listData.verticalAlignment = SWT.FILL;
		listData.verticalSpan = 16;

		GridData buttonData = new GridData();
		buttonData.horizontalAlignment = SWT.CENTER;
		buttonData.verticalAlignment = SWT.CENTER;

		GridData labelData = new GridData();
		labelData.horizontalAlignment = SWT.CENTER;
		labelData.verticalAlignment = SWT.TOP;

		infoLabel = new Label(parent, SWT.NONE);
		infoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		infoLabel.setText("Load some defined scores to enable ordering.");

		new Label(parent, SWT.NONE).setText("");

		leftLabel = new Label(parent, SWT.NONE);
		leftLabel.setText("Attributes");
		leftLabel.setVisible(false);
		leftLabel.setData(labelData);

		new Label(parent, SWT.NONE).setText("");

		rightLabel = new Label(parent, SWT.NONE);
		rightLabel.setText("Attributes");
		rightLabel.setVisible(false);
		rightLabel.setData(labelData);

		new Label(parent, SWT.NONE).setText("");

		listLeft = new List(parent, SWT.V_SCROLL);
		listLeft.setLayoutData(listData);
		listLeft.setVisible(false);
		listLeft.setEnabled(false);

		allRight = new Button(parent, SWT.PUSH);
		allRight.setImage(allRightImage);
		allRight.setLayoutData(buttonData);
		allRight.setSize(40, 40);
		allRight.setVisible(false);
		allRight.setEnabled(false);

		listRight = new List(parent, SWT.V_SCROLL);
		listRight.setLayoutData(listData);
		listRight.setVisible(false);
		listRight.setEnabled(false);

		allUp = new Button(parent, SWT.PUSH);
		allUp.setImage(allUpImage);
		allUp.setLayoutData(buttonData);
		allUp.setSize(40, 40);
		allUp.setVisible(false);
		allUp.setEnabled(false);

		oneRight = new Button(parent, SWT.PUSH);
		oneRight.setImage(oneRightImage);
		oneRight.setLayoutData(buttonData);
		oneRight.setSize(40, 40);
		oneRight.setVisible(false);
		oneRight.setEnabled(false);

		oneUp = new Button(parent, SWT.PUSH);
		oneUp.setImage(oneUpImage);
		oneUp.setLayoutData(buttonData);
		oneUp.setSize(40, 40);
		oneUp.setVisible(false);
		oneUp.setEnabled(false);

		oneLeft = new Button(parent, SWT.PUSH);
		oneLeft.setImage(oneLeftImage);
		oneLeft.setLayoutData(buttonData);
		oneLeft.setSize(40, 40);
		oneLeft.setVisible(false);
		oneLeft.setEnabled(false);

		oneDown = new Button(parent, SWT.PUSH);
		oneDown.setImage(oneDownImage);
		oneDown.setLayoutData(buttonData);
		oneDown.setSize(40, 40);
		oneDown.setVisible(false);
		oneDown.setEnabled(false);

		allLeft = new Button(parent, SWT.PUSH);
		allLeft.setImage(allLeftImage);
		allLeft.setLayoutData(buttonData);
		allLeft.setSize(40, 40);
		allLeft.setVisible(false);
		allLeft.setEnabled(false);

		allDown = new Button(parent, SWT.PUSH);
		allDown.setImage(allDownImage);
		allDown.setLayoutData(buttonData);
		allDown.setSize(40, 40);
		allDown.setVisible(false);
		allDown.setEnabled(false);

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

		descendingLabel = new Label(parent, SWT.NONE);
		descendingLabel.setData(labelData);
		descendingLabel.setText("Sort by descending order: ");
		descendingLabel.setVisible(false);

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

		new Label(parent, SWT.NONE).setText("");

		scoreToggle = new Button(parent, SWT.TOGGLE);
		scoreToggle.setText("Score");
		scoreToggle.setLayoutData(buttonData);
		scoreToggle.setSize(40, 40);
		scoreToggle.setVisible(false);
		scoreToggle.setEnabled(false);

		nameToggle = new Button(parent, SWT.TOGGLE);
		nameToggle.setText("Name");
		nameToggle.setLayoutData(buttonData);
		nameToggle.setSize(40, 40);
		nameToggle.setVisible(false);
		nameToggle.setEnabled(false);

		signatureToggle = new Button(parent, SWT.TOGGLE);
		signatureToggle.setText("Signature");
		signatureToggle.setLayoutData(buttonData);
		signatureToggle.setSize(40, 40);
		signatureToggle.setVisible(false);
		signatureToggle.setEnabled(false);

		parentTypeToggle = new Button(parent, SWT.TOGGLE);
		parentTypeToggle.setText("Parent Type");
		parentTypeToggle.setLayoutData(buttonData);
		parentTypeToggle.setSize(40, 40);
		parentTypeToggle.setVisible(false);
		parentTypeToggle.setEnabled(false);

		pathToggle = new Button(parent, SWT.TOGGLE);
		pathToggle.setText("Path");
		pathToggle.setLayoutData(buttonData);
		pathToggle.setSize(40, 40);
		pathToggle.setVisible(false);
		pathToggle.setEnabled(false);

		contextSizeToggle = new Button(parent, SWT.TOGGLE);
		contextSizeToggle.setText("Context Size");
		contextSizeToggle.setLayoutData(buttonData);
		contextSizeToggle.setSize(40, 40);
		contextSizeToggle.setVisible(false);
		contextSizeToggle.setEnabled(false);

		positionToggle = new Button(parent, SWT.TOGGLE);
		positionToggle.setText("Position");
		positionToggle.setLayoutData(buttonData);
		positionToggle.setSize(40, 40);
		positionToggle.setVisible(false);
		positionToggle.setEnabled(false);

		interactivityToggle = new Button(parent, SWT.TOGGLE);
		interactivityToggle.setText("Interactivity");
		interactivityToggle.setLayoutData(buttonData);
		interactivityToggle.setSize(40, 40);
		interactivityToggle.setVisible(false);
		interactivityToggle.setEnabled(false);

		lastActionToggle = new Button(parent, SWT.TOGGLE);
		lastActionToggle.setText("Last Action");
		lastActionToggle.setLayoutData(buttonData);
		lastActionToggle.setSize(40, 40);
		lastActionToggle.setVisible(false);
		lastActionToggle.setEnabled(false);
	}

	@Override
	public void embed(IEmbeddable embedded) {
		embedded.setParent(composite);
	}

	@Override
	public void setParent(Composite parent) {
		composite.setParent(parent);
	}

	public ArrayList<TItem> getArrayLeft() {
		return arrayLeft;
	}

	public ArrayList<TItem> getArrayRight() {
		return arrayRight;
	}

	public String getArrayElementbyIndex(ArrayList<TItem> source, int extractIndex, elementStringer<TItem> function) {
		TItem extractedItem = source.get(extractIndex);
		String extractedString = function.getAsString(extractedItem);
		return extractedString;
	}

	public String getLeftLabel() {
		return leftLabel.getText();
	}

	public void setLeftLabel(String labelText) {
		this.leftLabel.setText(labelText);
	}

	public String getRightLabel() {
		return rightLabel.getText();
	}

	public void setRightLabel(String labelText) {
		this.rightLabel.setText(labelText);
	}

	public String getInfpLabel() {
		return infoLabel.getText();
	}

	public void setInfoLabel(String labelText) {
		this.infoLabel.setText(labelText);
	}

	public String getAllRightText() {
		return allRight.getText();
	}

	public void setAllRightText(String buttonText) {
		this.allRight.setText(buttonText);
	}

	public String getAllUpText() {
		return allUp.getText();
	}

	public void setAllUpText(String buttonText) {
		this.allUp.setText(buttonText);
	}

	public String getOneRightText() {
		return oneRight.getText();
	}

	public void setOneRightText(String buttonText) {
		this.oneRight.setText(buttonText);
	}

	public String getOneUpText() {
		return oneUp.getText();
	}

	public void setOneUpText(String buttonText) {
		this.oneUp.setText(buttonText);
	}

	public String getOneLeftText() {
		return oneLeft.getText();
	}

	public void setOneLeftText(String buttonText) {
		this.oneLeft.setText(buttonText);
	}

	public String getOneDownText() {
		return oneDown.getText();
	}

	public void setOneDownText(String buttonText) {
		this.oneDown.setText(buttonText);
	}

	public String getAllLeftText() {
		return allLeft.getText();
	}

	public void setAllLeftText(String buttonText) {
		this.allLeft.setText(buttonText);
	}

	public String getAllDownText() {
		return allDown.getText();
	}

	public void setAllDownText(String buttonText) {
		this.allDown.setText(buttonText);
	}

	public String getScoreToggleText() {
		return scoreToggle.getText();
	}

	public void setScoreToggleText(String buttonText) {
		this.scoreToggle.setText(buttonText);
	}

	public String getNameToggleText() {
		return nameToggle.getText();
	}

	public void setNameToggleText(String buttonText) {
		this.nameToggle.setText(buttonText);
	}

	public String getSignatureToggleText() {
		return signatureToggle.getText();
	}

	public void setSignatureToggleText(String buttonText) {
		this.signatureToggle.setText(buttonText);
	}

	public String getParentTypeToggleText() {
		return parentTypeToggle.getText();
	}

	public void setParentTypeToggleText(String buttonText) {
		this.parentTypeToggle.setText(buttonText);
	}

	public String getPathToggleText() {
		return pathToggle.getText();
	}

	public void setPathToggleText(String buttonText) {
		this.pathToggle.setText(buttonText);
	}

	public String getContextSizeToggleText() {
		return contextSizeToggle.getText();
	}

	public void setContextSizeToggleText(String buttonText) {
		this.contextSizeToggle.setText(buttonText);
	}

	public String getPositionToggleText() {
		return positionToggle.getText();
	}

	public void setPositionToggleText(String buttonText) {
		this.positionToggle.setText(buttonText);
	}

	public String getInteractivityToggleText() {
		return interactivityToggle.getText();
	}

	public void setInteractivityToggleText(String buttonText) {
		this.interactivityToggle.setText(buttonText);
	}

	public String getLastActionToggleText() {
		return lastActionToggle.getText();
	}

	public void setLastActionToggleText(String buttonText) {
		this.lastActionToggle.setText(buttonText);
	}

	public void itemizer(String item) {
		TItem lElement = (TItem) item;
		leftAdd(lElement);
	}

	public void refresh() {
		listLeft.removeAll();
		arrayLeftBackup.clear();
		for (TItem item : arrayLeft) {
			DualListElement element = (DualListElement) item;
			listLeft.add(element.getName());
			arrayLeftBackup.add((DualListElement) item);
		}

		listRight.removeAll();
		arrayRightBackup.clear();
		for (TItem item : arrayRight) {
			DualListElement element = (DualListElement) item;
			listRight.add(element.getName());
			arrayRightBackup.add((DualListElement) item);
		}

		listRefreshRequested.invoke(arrayRight);
	}

	public void reload() {
		listLeft.removeAll();
		arrayLeft.clear();
		for (DualListElement item : arrayLeftBackup) {
			listLeft.add(item.getName());
			arrayLeft.add((TItem) item);
		}

		listRight.removeAll();
		arrayRight.clear();
		for (DualListElement item : arrayRightBackup) {
			listRight.add(item.getName());
			TItem tItem = (TItem) item;
			arrayRight.add((TItem) item);
			switchToggle(item.getName());
		}

		listRefreshRequested.invoke(arrayRight);
	}

	public void leftAdd(TItem item) {
		arrayLeft.add(item);
		this.refresh();
	}

	public void leftRemove(TItem item) {
		arrayLeft.remove(item);
		this.refresh();
	}

	public void rightAdd(TItem item) {
		arrayRight.add(item);
		this.refresh();
	}

	public void rightRemove(TItem item) {
		arrayRight.remove(item);
		this.refresh();
	}

	public void switchToggle(String argument) {
		switch (argument) {
		case "Score":
			scoreToggle.setVisible(!scoreToggle.getVisible());
			scoreToggle.setEnabled(!scoreToggle.getEnabled());
			break;
		case "Name":
			nameToggle.setVisible(!nameToggle.getVisible());
			nameToggle.setEnabled(!nameToggle.getEnabled());
			break;
		case "Signature":
			signatureToggle.setVisible(!signatureToggle.getVisible());
			signatureToggle.setEnabled(!signatureToggle.getEnabled());
			break;
		case "Parent Type":
			parentTypeToggle.setVisible(!parentTypeToggle.getVisible());
			parentTypeToggle.setEnabled(!parentTypeToggle.getEnabled());
			break;
		case "Path":
			pathToggle.setVisible(!pathToggle.getVisible());
			pathToggle.setEnabled(!pathToggle.getEnabled());
			break;
		case "Context Size":
			contextSizeToggle.setVisible(!contextSizeToggle.getVisible());
			contextSizeToggle.setEnabled(!contextSizeToggle.getEnabled());
			break;
		case "Position":
			positionToggle.setVisible(!positionToggle.getVisible());
			positionToggle.setEnabled(!positionToggle.getEnabled());
			break;
		case "Interactivity":
			interactivityToggle.setVisible(!interactivityToggle.getVisible());
			interactivityToggle.setEnabled(!interactivityToggle.getEnabled());
			break;
		case "Last Action":
			lastActionToggle.setVisible(!lastActionToggle.getVisible());
			lastActionToggle.setEnabled(!lastActionToggle.getEnabled());
			break;
		}

	}

	public void moveBetweenAll(ArrayList<TItem> source, ArrayList<TItem> destination) {
		for (TItem item : source) {
			destination.add(item);
			currentElement = (DualListElement) item;
			switchToggle(currentElement.getName());
		}
		source.clear();
		whichList = SelectionLocation.UNSELECTED;

	}

	public void moveBetweenOne(ArrayList<TItem> source, ArrayList<TItem> destination, int itemIndex) {
		selectedItem = source.get(itemIndex);
		int destinationIndex = destination.size(); // since size is calculated before adding item, we don't need to
													// subtract 1 from size
		destination.add(selectedItem);
		source.remove(selectedItem);
		currentElement = (DualListElement) selectedItem;
		switchToggle(currentElement.getName());
		this.moveItem = new ItemMoveObject<TItem>(source, destination, selectedItem, itemIndex, destinationIndex);

	}

	public int moveInside(ArrayList<TItem> source, int itemIndex, Widget selectedButton) {
		int length = source.size();
		selectedItem = source.get(itemIndex);

		if (selectedButton.equals(allUp))
			newIndex = 0;
		else if (selectedButton.equals(allDown))
			newIndex = length - 1;
		else if (selectedButton.equals(oneUp))
			newIndex = itemIndex - 1;
		else if (selectedButton.equals(oneDown))
			newIndex = itemIndex + 1;

		swap = source.get(newIndex);
		source.set(itemIndex, swap);
		source.set(newIndex, selectedItem);
		this.moveItem = new ItemMoveObject<TItem>(source, source, selectedItem, itemIndex, newIndex);
		return newIndex;
	}

	public void refreshSelectionBetweenOne(List source, List destination) {
		DualListPart.this.refresh();
		source.setSelection(-1);
		int newSelection = destination.getItemCount() - 1;
		destination.setSelection(newSelection);
		this.moveItem.setDestinationIndex(newSelection);
	}

	public class moveBetweenListsListener implements Listener {
		@Override
		public void handleEvent(Event event) {

			if (event.widget.equals(allRight)) {
				moveBetweenAll(arrayLeft, arrayRight);
				DualListPart.this.refresh();
			} else if (event.widget.equals(allLeft)) {
				moveBetweenAll(arrayRight, arrayLeft);
				DualListPart.this.refresh();
			} else {
				switch (whichList) {
				case UNSELECTED:
					break;
				case LEFT:
					moveBetweenOne(arrayLeft, arrayRight, itemIndex);
					whichList = SelectionLocation.RIGHT;
					refreshSelectionBetweenOne(listLeft, listRight);
					itemIndex = arrayRight.size() - 1;
					break;
				case RIGHT:
					moveBetweenOne(arrayRight, arrayLeft, itemIndex);
					whichList = SelectionLocation.LEFT;
					refreshSelectionBetweenOne(listRight, listLeft);
					itemIndex = arrayLeft.size() - 1;
					break;
				}
			}

			moveBetweenListsRequested.invoke(moveItem);
			selectionRequested.invoke(itemIndex);
		}
	}

	public class moveInsideListListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			Widget selectedButton = event.widget;

			switch (whichList) {
			case UNSELECTED:
				break;
			case LEFT: {
				newIndex = moveInside(arrayLeft, itemIndex, selectedButton);
				DualListPart.this.refresh();
				listLeft.setSelection(newIndex);
				itemIndex = newIndex;
				break;
			}
			case RIGHT:
				newIndex = moveInside(arrayRight, itemIndex, selectedButton);
				DualListPart.this.refresh();
				listRight.setSelection(newIndex);
				itemIndex = newIndex;
				break;
			}
			moveInsideListRequested.invoke(moveItem);
			selectionRequested.invoke(moveItem.getDestinationIndex());
		}

	}

	@Override
	public void createPartControl(Composite parent) {

		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.makeColumnsEqualWidth = false;
		composite = parent;
		composite.setLayout(gridLayout);
		addUIElements(parent);

		if (arrayLeftBackup.isEmpty() && arrayRightBackup.isEmpty()) {

			TItem score = (TItem) new DualListElement("Score", false); // set to false after UI is completed!
			TItem name = (TItem) new DualListElement("Name", false);
			TItem signature = (TItem) new DualListElement("Signature", false);
			TItem parentType = (TItem) new DualListElement("Parent Type", false);
			TItem path = (TItem) new DualListElement("Path", false);
			TItem contextSize = (TItem) new DualListElement("Context Size", false);
			TItem position = (TItem) new DualListElement("Position", false);
			TItem interactivity = (TItem) new DualListElement("Interactivity", false);
			TItem lastAction = (TItem) new DualListElement("Last Action", false);

			leftAdd(score);
			leftAdd(name);
			leftAdd(signature);
			leftAdd(parentType);
			leftAdd(path);
			leftAdd(contextSize);
			leftAdd(position);
			leftAdd(interactivity);
			leftAdd(lastAction);

		}

		else {
			this.reload();
		}

		listLeft.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				itemIndex = listLeft.indexOf(listLeft.getSelection()[0]);
				whichList = SelectionLocation.LEFT;
				listRight.setSelection(-1);
				selectionRequested.invoke(itemIndex);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		listRight.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				itemIndex = listRight.indexOf(listRight.getSelection()[0]);
				whichList = SelectionLocation.RIGHT;
				listLeft.setSelection(-1);
				selectionRequested.invoke(itemIndex);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		SelectionListener toggleListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				String buttonText = source.getText();
				toggleIndex = listRight.indexOf(buttonText);
				currentElement = (DualListElement) arrayRight.get(toggleIndex);
				currentElement.setDescending(!currentElement.isDescending());
				arrayRight.set(toggleIndex, (TItem) currentElement);
				refresh();

			}
		};

		allRight.addListener(SWT.Selection, new moveBetweenListsListener());

		oneRight.addListener(SWT.Selection, new moveBetweenListsListener());

		oneLeft.addListener(SWT.Selection, new moveBetweenListsListener());

		allLeft.addListener(SWT.Selection, new moveBetweenListsListener());

		allUp.addListener(SWT.Selection, new moveInsideListListener());

		oneUp.addListener(SWT.Selection, new moveInsideListListener());

		oneDown.addListener(SWT.Selection, new moveInsideListListener());

		allDown.addListener(SWT.Selection, new moveInsideListListener());

		scoreToggle.addSelectionListener(toggleListener);

		nameToggle.addSelectionListener(toggleListener);

		signatureToggle.addSelectionListener(toggleListener);

		parentTypeToggle.addSelectionListener(toggleListener);

		pathToggle.addSelectionListener(toggleListener);

		contextSizeToggle.addSelectionListener(toggleListener);

		positionToggle.addSelectionListener(toggleListener);

		interactivityToggle.addSelectionListener(toggleListener);

		lastActionToggle.addSelectionListener(toggleListener);

		if (orderingEnabled.booleanValue()) {
			enableOrdering();
		}

	}

	private NonGenericListenerCollection<ArrayList> listRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ArrayList> eventListRefreshRequested() {
		return listRefreshRequested;
	}

	private NonGenericListenerCollection<Integer> selectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSelectionRequested() {
		return selectionRequested;
	}

	private NonGenericListenerCollection<ItemMoveObject> moveBetweenListsRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject> eventMoveBetweenListsRequested() {
		return moveBetweenListsRequested;
	}

	private NonGenericListenerCollection<ItemMoveObject> moveInsideListRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject> eventMoveInsideListRequested() {
		return moveInsideListRequested;
	}

	public void enableOrdering() {

		infoLabel.setText("Order your scores by (multiple) attributes.");
		leftLabel.setVisible(true);
		rightLabel.setVisible(true);
		descendingLabel.setVisible(true);

		listLeft.setVisible(true);
		listLeft.setEnabled(true);
		listRight.setVisible(true);
		listRight.setEnabled(true);

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
