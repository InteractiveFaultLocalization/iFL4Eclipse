package org.eclipse.sed.ifl.control;

import java.util.List;

import org.eclipse.sed.ifl.control.score.Sortable;
import org.eclipse.sed.ifl.model.DualListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.view.DualListView;

import javafx.collections.ObservableList;

public class DualListControl<TItem extends Sortable> extends Control<DualListModel, DualListView<TItem>> {

	public void showDualListPart() {
		getView().showDualListPart();
	}

	public void init() {
		initUIListeners(); //InitView rename
		initModelListeners();
		super.init();
	}

	public void teardown() {
		removeUIListeners();
		removeModelListeners();
		super.teardown();
	}

	private void initUIListeners() {
		getView().eventAttributeListChangeRequested().add(attributeListChangeRequestedListener);
		getView().eventSortingListChangeRequested().add(sortingListChangeRequestedListener);
		getView().eventOrderingDirectionChanged().add(orderingDirectionChangedListener);
	}

	private void initModelListeners() {
		getModel().eventAttributeListChanged().add(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().add(sortingListRefreshRequestedListener);
		getModel().eventOrderingRefresh().add(orderingRefreshRequestedListener);
	}

	public void removeUIListeners() {
		getView().eventAttributeListChangeRequested().remove(attributeListChangeRequestedListener);
		getView().eventSortingListChangeRequested().remove(sortingListChangeRequestedListener);
		getView().eventOrderingDirectionChanged().remove(orderingDirectionChangedListener);
	}

	public void removeModelListeners() {
		getModel().eventAttributeListChanged().remove(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().remove(sortingListRefreshRequestedListener);
		getModel().eventOrderingRefresh().remove(orderingRefreshRequestedListener);
	}

	public void enableOrdering() {
		getView().enableOrdering();
		setAttributeList();
	}

	private NonGenericListenerCollection<Sortable> orderingDirectionChanged = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventOrderingDirectionChanged() {
		return orderingDirectionChanged;
	}

	private IListener<Sortable> orderingDirectionChangedListener = event -> {
		ObservableList<Sortable> newSortingList = getModel().getSortingList();
		Sortable originalArg = event;
		originalArg.setSortingDirection(originalArg.getSortingDirection().equals(Sortable.SortingDirection.Ascending)
				? Sortable.SortingDirection.Descending
				: Sortable.SortingDirection.Ascending);
		int swapIndex = newSortingList.indexOf(originalArg);
		newSortingList.set(swapIndex, event);
		getModel().setSortingList(newSortingList);
	};

	// Left

	private NonGenericListenerCollection<List<Sortable>> attributeListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventAttributeListRefreshRequested() {
		return attributeListRefreshRequested;
	}

	private IListener<List<Sortable>> attributeListRefreshRequestedListener = event -> {
		getView().attributeListRefresh(event);
	};

	private void setAttributeList() { //Rename: refresh vagy update (onView)
		getView().attributeListRefresh(getModel().getAttributeList());
	}

	// Right

	private NonGenericListenerCollection<List<Sortable>> sortingListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventSortingListRefreshRequested() {
		return sortingListRefreshRequested;
	}

	private IListener<List<Sortable>> sortingListRefreshRequestedListener = event -> {
		getView().sortingListRefresh(event);
	};
	
	private NonGenericListenerCollection<List<Sortable>> orderingRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventOrderingRefreshRequested() {
		return orderingRefreshRequested;
	}

	private IListener<List<Sortable>> orderingRefreshRequestedListener = orderingRefreshRequested::invoke;

	private IListener<ItemMoveObject<TItem>> attributeListChangeRequestedListener = event -> { 
		ObservableList<Sortable> newAttributeList;
		ObservableList<Sortable> sortingList = getModel().getSortingList();
		if (event.getOperationType().equals(OperationType.WIPE)) {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.clear();
		} else if (event.getOperationType().equals(OperationType.REMOVE)) {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.remove(event.getItem());
		} else if (event.getOperationType().equals(OperationType.MOVEALL)) {
			newAttributeList= getModel().getAttributeList();
			for(Sortable sortable : sortingList) {
				newAttributeList.add(sortable);
			}
		} else if (event.getOperationType().equals(OperationType.MOVEINSIDE)) {
			newAttributeList = getModel().getAttributeList();
			Sortable selectedArgument = event.getItem();
			int selectedIndex = newAttributeList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			Sortable swapArgument = newAttributeList.get(swapIndex);
			newAttributeList.set(selectedIndex, swapArgument);
			newAttributeList.set(swapIndex, event.getItem());
		}

		else  {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.add(event.getItem());
		}
		getModel().setAttributeList(newAttributeList);
	};

	private IListener<ItemMoveObject<TItem>> sortingListChangeRequestedListener = event -> {
		ObservableList<Sortable> newSortingList;
		ObservableList<Sortable> attributeList = getModel().getAttributeList();
		if (event.getOperationType().equals(OperationType.WIPE)) {
			newSortingList = getModel().getSortingList();
			newSortingList.clear();
		}
		else if (event.getOperationType().equals(OperationType.REMOVE)) {
			newSortingList = getModel().getSortingList();
			newSortingList.remove(event.getItem());
		} else if (event.getOperationType().equals(OperationType.MOVEALL)) {
			newSortingList = getModel().getSortingList();
			for(Sortable sortable : attributeList) {
				newSortingList.add(sortable);
			}
		} else if (event.getOperationType().equals(OperationType.MOVEINSIDE)) {
			newSortingList = getModel().getSortingList();
			Sortable selectedArgument = event.getItem();
			int selectedIndex = newSortingList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			Sortable swapArgument = newSortingList.get(swapIndex);
			newSortingList.set(selectedIndex, swapArgument);
			newSortingList.set(swapIndex, event.getItem());
		}

		else {
			newSortingList = getModel().getSortingList();
			newSortingList.add(event.getItem());
		}
		getModel().setSortingList(newSortingList);
	};
}
