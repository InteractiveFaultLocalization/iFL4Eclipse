package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.control.Control;
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
		//getView().eventMoveInsideListRequested().add(selectionRequestedListener);

	}

	public void removeUIListeners() {
		getView().eventMoveBetweenListsRequested().add(moveBetweenListsRequestedListener);
		getView().eventMoveInsideListRequested().add(moveInsideListRequestedListener);
		//getView().eventMoveInsideListRequested().remove(selectionRequestedListener);

	}

	private NonGenericListenerCollection<SelectionEvent> selectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<SelectionEvent> eventSelectionRequested() {
		return selectionRequested;
	}

	private IListener<SelectionEvent> selectionRequestedListener = selectionRequested::invoke;

	private NonGenericListenerCollection<Event> moveBetweenListsRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Event> eventMoveBetweenListsRequested() {
		return moveBetweenListsRequested;
	}

	private IListener<Event> moveBetweenListsRequestedListener = moveBetweenListsRequested::invoke;

	private NonGenericListenerCollection<Event> moveInsideListRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Event> eventMoveInsideListRequested() {
		return moveInsideListRequested;
	}

	private IListener<Event> moveInsideListRequestedListener = moveInsideListRequested::invoke;

}
