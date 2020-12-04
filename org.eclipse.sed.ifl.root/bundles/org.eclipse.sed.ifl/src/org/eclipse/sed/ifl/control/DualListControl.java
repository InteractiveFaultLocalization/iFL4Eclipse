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
		initViewListeners();
		initModelListeners();
		super.init();
	}

	public void teardown() {
		removeViewListeners();
		removeModelListeners();
		super.teardown();
	}

	private void initViewListeners() {
		getView().eventAttributeListButtonPressed().add(attributeListButtonPressedListener);
		getView().eventAttributeListChangeRequested().add(attributeListChangeRequestedListener);
		getView().eventSortingListButtonPressed().add(sortingListButtonPressedListener);
		getView().eventSortingListChangeRequested().add(sortingListChangeRequestedListener);
		getView().eventOrderingDirectionChanged().add(orderingDirectionChangedListener);
	}

	private void initModelListeners() {
		getModel().eventAttributeListChanged().add(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().add(sortingListRefreshRequestedListener);
		getModel().eventOrderingRefresh().add(orderingRefreshRequestedListener);
	}

	public void removeViewListeners() {
		getView().eventAttributeListButtonPressed().remove(attributeListButtonPressedListener);
		getView().eventAttributeListChangeRequested().remove(attributeListChangeRequestedListener);
		getView().eventSortingListButtonPressed().remove(sortingListButtonPressedListener);
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
		refreshAttributeListOnView();
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

	private void refreshAttributeListOnView() {
		getView().attributeListRefresh(getModel().getAttributeList());
	}

	private void changeSortingList(ItemMoveObject<TItem> moveObject) {
		sortingListChangeRequestedListener.invoke(moveObject);
	}

	private void changeAttributeList(ItemMoveObject<TItem> moveObject) {
		attributeListChangeRequestedListener.invoke(moveObject);
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

	private IListener<ItemMoveObject<TItem>> attributeListButtonPressedListener = event -> {
		ItemMoveObject<TItem> moveObject = event;
		int length = getModel().getAttributeList().size();
		int sourceIndex = event.getSourceIndex();
		if (event.getOperationType().equals(OperationType.ALLUP))
			moveObject.setDestinationIndex(0);
		else if (event.getOperationType().equals(OperationType.ALLDOWN))
			moveObject.setDestinationIndex(length - 1);
		else if (event.getOperationType().equals(OperationType.ONEUP))
			moveObject.setDestinationIndex(sourceIndex - 1);
		else if (event.getOperationType().equals(OperationType.ONEDOWN))
			moveObject.setDestinationIndex(sourceIndex + 1);

		OperationType operationType = OperationType.MOVEINSIDE;
		moveObject.setOperationType(operationType);
		changeAttributeList(moveObject);

	};

	private IListener<ItemMoveObject<TItem>> attributeListChangeRequestedListener = event -> {
		ObservableList<Sortable> newAttributeList;
		ObservableList<Sortable> sortingList = getModel().getSortingList();
		if (event.getOperationType().equals(OperationType.REMOVEALL)) {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.clear();
		} else if (event.getOperationType().equals(OperationType.REMOVE)) {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.remove(newAttributeList.get(event.getSourceIndex()));
		} else if (event.getOperationType().equals(OperationType.MOVEALL)) {
			newAttributeList = getModel().getAttributeList();
			for (Sortable sortable : sortingList) {
				newAttributeList.add(sortable);
			}
			OperationType operationType = OperationType.REMOVEALL;
			ItemMoveObject<TItem> moveObject = event;
			moveObject.setOperationType(operationType);
			changeSortingList(moveObject);

		} else if (event.getOperationType().equals(OperationType.MOVEINSIDE)) {
			newAttributeList = getModel().getAttributeList();
			Sortable selectedArgument = newAttributeList.get(event.getSourceIndex());
			int selectedIndex = newAttributeList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			Sortable swapArgument = newAttributeList.get(swapIndex);
			newAttributeList.set(selectedIndex, swapArgument);
			newAttributeList.set(swapIndex, selectedArgument);
		}

		else {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.add(sortingList.get(event.getSourceIndex()));
			OperationType operationType = OperationType.REMOVE;
			ItemMoveObject<TItem> moveObject = event;
			moveObject.setOperationType(operationType);
			changeSortingList(moveObject);
		}
		getModel().setAttributeList(newAttributeList);
	};

	private IListener<ItemMoveObject<TItem>> sortingListButtonPressedListener = event -> {
		ItemMoveObject<TItem> moveObject = event;
		int length = getModel().getSortingList().size();
		int sourceIndex = event.getSourceIndex();
		if (event.getOperationType().equals(OperationType.ALLUP))
			moveObject.setDestinationIndex(0);
		else if (event.getOperationType().equals(OperationType.ALLDOWN))
			moveObject.setDestinationIndex(length - 1);
		else if (event.getOperationType().equals(OperationType.ONEUP))
			moveObject.setDestinationIndex(sourceIndex - 1);
		else if (event.getOperationType().equals(OperationType.ONEDOWN))
			moveObject.setDestinationIndex(sourceIndex + 1);

		OperationType operationType = OperationType.MOVEINSIDE;
		moveObject.setOperationType(operationType);
		changeSortingList(moveObject);
	};

	private IListener<ItemMoveObject<TItem>> sortingListChangeRequestedListener = event -> {
		ObservableList<Sortable> newSortingList;
		ObservableList<Sortable> attributeList = getModel().getAttributeList();
		if (event.getOperationType().equals(OperationType.REMOVEALL)) {
			newSortingList = getModel().getSortingList();
			newSortingList.clear();
		} else if (event.getOperationType().equals(OperationType.REMOVE)) {
			newSortingList = getModel().getSortingList();
			newSortingList.remove(event.getSourceIndex());
		} else if (event.getOperationType().equals(OperationType.MOVEALL)) {
			newSortingList = getModel().getSortingList();
			for (Sortable sortable : attributeList) {
				newSortingList.add(sortable);
			}
			OperationType operationType = OperationType.REMOVEALL;
			ItemMoveObject<TItem> moveObject = event;
			moveObject.setOperationType(operationType);
			changeAttributeList(moveObject);
		} else if (event.getOperationType().equals(OperationType.MOVEINSIDE)) {
			newSortingList = getModel().getSortingList();
			Sortable selectedArgument = newSortingList.get(event.getSourceIndex());
			int selectedIndex = newSortingList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			Sortable swapArgument = newSortingList.get(swapIndex);
			newSortingList.set(selectedIndex, swapArgument);
			newSortingList.set(swapIndex, selectedArgument);
		}

		else {
			newSortingList = getModel().getSortingList();
			newSortingList.add(attributeList.get(event.getSourceIndex()));
			OperationType operationType = OperationType.REMOVE;
			ItemMoveObject<TItem> moveObject = event;
			moveObject.setOperationType(operationType);
			changeAttributeList(moveObject);
		}
		getModel().setSortingList(newSortingList);
	};
}
