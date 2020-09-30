package org.eclipse.sed.ifl.control;

import java.util.ArrayList;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.DualListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.view.DualListView;

public class DualListControl<TItem> extends Control<DualListModel, DualListView> {

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
		getView().eventListRefreshRequested().add(listRefreshRequestedListener);

	}

	public void removeUIListeners() {
		getView().eventMoveBetweenListsRequested().add(moveBetweenListsRequestedListener);
		getView().eventMoveInsideListRequested().add(moveInsideListRequestedListener);
		getView().eventSelectionRequested().remove(selectionRequestedListener);
		getView().eventListRefreshRequested().remove(listRefreshRequestedListener);

	}

	
	public void enableOrdering() {
		getView().enableOrdering();
	}
	
	private NonGenericListenerCollection<ArrayList> listRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ArrayList> eventlistRefreshRequested() {
		return listRefreshRequested;
	}

	private IListener<ArrayList> listRefreshRequestedListener = listRefreshRequested::invoke;
	
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
