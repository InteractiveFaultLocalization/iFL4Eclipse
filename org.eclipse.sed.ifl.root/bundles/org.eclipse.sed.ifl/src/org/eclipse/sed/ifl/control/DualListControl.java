package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.ItemMoveObject;
import org.eclipse.sed.ifl.model.DualListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Event;
import org.eclipse.sed.ifl.view.DualListView;

public class DualListControl extends Control<DualListModel, DualListView> {

	public void showDualListPart() {
		getView().showDualListPart();
	}

	public void init() {
		initUIListeners();
		super.init();
	}

	public void teardown() {
		removeUIListeners();
		super.teardown();
	}

	private void initUIListeners() {
		getView().eventMoveBetweenListsRequested().add(moveBetweenListsRequestedListener);
		getView().eventMoveInsideListRequested().add(moveInsideListRequestedListener);
		getView().eventSelectionRequested().add(selectionRequestedListener);

	}

	public void removeUIListeners() {
		getView().eventMoveBetweenListsRequested().add(moveBetweenListsRequestedListener);
		getView().eventMoveInsideListRequested().add(moveInsideListRequestedListener);
		getView().eventSelectionRequested().remove(selectionRequestedListener);

	}

	private NonGenericListenerCollection<Integer> selectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSelectionRequested() {
		return selectionRequested;
	}

	private IListener<Integer> selectionRequestedListener = selectionRequested::invoke;

	private NonGenericListenerCollection<ItemMoveObject> moveBetweenListsRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject> eventMoveBetweenListsRequested() {
		return moveBetweenListsRequested;
	}

	private IListener<ItemMoveObject> moveBetweenListsRequestedListener = moveBetweenListsRequested::invoke;

	private NonGenericListenerCollection<ItemMoveObject> moveInsideListRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject> eventMoveInsideListRequested() {
		return moveInsideListRequested;
	}

	private IListener<ItemMoveObject> moveInsideListRequestedListener = moveInsideListRequested::invoke;

}
