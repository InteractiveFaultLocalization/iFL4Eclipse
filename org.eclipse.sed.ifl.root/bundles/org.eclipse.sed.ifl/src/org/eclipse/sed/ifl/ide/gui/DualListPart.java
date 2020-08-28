package org.eclipse.sed.ifl.ide.gui;

import java.util.ArrayList;
import javax.inject.*;

import org.eclipse.sed.ifl.control.ItemMoveObject;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

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

	
	private TItem selectedItem;
	private int selectedIndex;
	private int newIndex;
	private TItem swap;
	private SelectionLocation whichList;
	private String itemString;
	private int itemIndex;
	private ItemMoveObject<TItem> moveItem;

	@FunctionalInterface
	public interface elementStringer<TItem> {
		String getAsString(TItem t);
	}

	private Label leftLabel;
	private Label rightLabel;
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
	private GridLayout gridLayout;

	public DualListPart() {
		System.out.println("dual list part ctr");
	}

	private void addUIElements(Composite parent) {
		selectedItem = null;
		selectedIndex = 0;
		newIndex = 0;
		swap = null;
		whichList = whichList.UNSELECTED;

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

		Label leftLabel = new Label(parent, SWT.NONE);
		leftLabel.setText("Left List");

		leftLabel.setData(labelData);

		new Label(parent, SWT.NONE).setText("");

		Label rightLabel = new Label(parent, SWT.NONE);
		rightLabel.setText("Right List");

		rightLabel.setData(labelData);

		new Label(parent, SWT.NONE).setText("");

		listLeft = new List(parent, SWT.V_SCROLL);
		listLeft.setLayoutData(listData);

		allRight = new Button(parent, SWT.PUSH);
		allRight.setText("--> -->");
		allRight.setLayoutData(buttonData);

		listRight = new List(parent, SWT.V_SCROLL);
		listRight.setLayoutData(listData);

		allUp = new Button(parent, SWT.PUSH);
		allUp.setText("Move to Top");
		allUp.setLayoutData(buttonData);

		oneRight = new Button(parent, SWT.PUSH);
		oneRight.setText("-->");
		oneRight.setLayoutData(buttonData);

		oneUp = new Button(parent, SWT.PUSH);
		oneUp.setText("One Up");
		oneUp.setLayoutData(buttonData);

		oneLeft = new Button(parent, SWT.PUSH);
		oneLeft.setText("<--");
		oneLeft.setLayoutData(buttonData);

		oneDown = new Button(parent, SWT.PUSH);
		oneDown.setText("One Down");
		oneDown.setLayoutData(buttonData);

		allLeft = new Button(parent, SWT.PUSH);
		allLeft.setText("<-- <--");
		allLeft.setLayoutData(buttonData);

		allDown = new Button(parent, SWT.PUSH);
		allDown.setText("Move to bottom");
		allDown.setLayoutData(buttonData);

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

	public String getArrayElementbyIndex(ArrayList<TItem> source, int extractIndex, elementStringer function) {
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

	public void refresh() {
		listLeft.removeAll();
		for (TItem item : arrayLeft) {
			listLeft.add(String.valueOf(item));
		}

		listRight.removeAll();
		for (TItem item : arrayRight) {
			listRight.add(String.valueOf(item));
		}
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

	public void moveBetweenAll(ArrayList<TItem> source, ArrayList<TItem> destination) {
		int length = source.size();
		for (TItem item : source) {
			destination.add(item);
		}
		source.clear();
		whichList = whichList.UNSELECTED;
		DualListPart.this.refresh();
		listLeft.setSelection(-1);
		listRight.setSelection(-1);
	}

	public void moveBetweenOne(ArrayList<TItem> source, ArrayList<TItem> destination, int itemIndex) {
		selectedItem = source.get(itemIndex);
		int destinationIndex = destination.size(); // since size is calculated before adding item, we don't need to
													// subtract 1 from size
		destination.add(selectedItem);
		source.remove(selectedItem);
		this.moveItem = new ItemMoveObject<TItem>(source, destination, selectedItem, itemIndex,
				destinationIndex);
	}

	public int moveInside(ArrayList<TItem> source, int itemIndex, Widget selectedButton) {
		int length = source.size();
		selectedItem = source.get(itemIndex);

		if (selectedButton.equals(allUp))
			newIndex = 0;
		else if (selectedButton.equals(allDown))
			newIndex = length - 1;
		else if (selectedButton.equals(oneUp))
			newIndex = selectedIndex - 1;
		else if (selectedButton.equals(oneDown))
			newIndex = selectedIndex + 1;

		swap = source.get(newIndex);
		source.set(selectedIndex, swap);
		source.set(newIndex, selectedItem);
		this.moveItem= new ItemMoveObject<TItem>(source, source, selectedItem, itemIndex, newIndex);
		return newIndex;
	}

	public void refreshSelectionBetweenOne(List source, List destination) {
		DualListPart.this.refresh();
		source.setSelection(-1);
		destination.setSelection(destination.getItemCount() - 1);
	}

	public class moveBetweenListsListener implements Listener {
		@Override
		public void handleEvent(Event event) {

			if (event.widget.equals(allRight)) {
				moveBetweenAll(arrayLeft, arrayRight);
			} else if (event.widget.equals(allLeft)) {
				moveBetweenAll(arrayRight, arrayLeft);
			} else {
				switch (whichList) {
				case UNSELECTED:
					break;
				case LEFT:
					 moveBetweenOne(arrayLeft, arrayRight, itemIndex);
					whichList = whichList.LEFT;
					refreshSelectionBetweenOne(listLeft, listRight);
					break;
				case RIGHT:
					 moveBetweenOne(arrayRight, arrayLeft, itemIndex);
					whichList = whichList.RIGHT;
					refreshSelectionBetweenOne(listRight, listLeft);
					break;
				}
			}
			moveBetweenListsRequested.invoke(moveItem);
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
				break;
			}
			case RIGHT:
				newIndex = moveInside(arrayRight, itemIndex, selectedButton);
				DualListPart.this.refresh();
				listRight.setSelection(newIndex);
				break;
			}
			moveInsideListRequested.invoke(moveItem);
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		
		gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.makeColumnsEqualWidth = false;
		composite = parent;
		composite.setLayout(gridLayout);
		addUIElements( parent);
		listLeft.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				itemString = listLeft.getSelection()[0];
				itemIndex = arrayLeft.indexOf(itemString);
				whichList = whichList.LEFT;
				listRight.setSelection(-1);
				selectionRequested.invoke(itemIndex);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		listRight.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				itemString = listRight.getSelection()[0];
				itemIndex = arrayRight.indexOf(itemString);
				whichList = whichList.RIGHT;
				listLeft.setSelection(-1);
				selectionRequested.invoke(itemIndex);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		allRight.addListener(SWT.Selection, new moveBetweenListsListener());

		oneRight.addListener(SWT.Selection, new moveBetweenListsListener());

		oneLeft.addListener(SWT.Selection, new moveBetweenListsListener());

		allLeft.addListener(SWT.Selection, new moveBetweenListsListener());

		allUp.addListener(SWT.Selection, new moveInsideListListener());

		oneUp.addListener(SWT.Selection, new moveInsideListListener());

		oneDown.addListener(SWT.Selection, new moveInsideListListener());

		allDown.addListener(SWT.Selection, new moveInsideListListener());
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

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		this.getSite().getPage().hideView(this);
	}
}