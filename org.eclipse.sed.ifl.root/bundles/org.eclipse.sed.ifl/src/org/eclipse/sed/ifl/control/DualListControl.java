package org.eclipse.sed.ifl.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.model.DualListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.view.DualListView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DualListControl<TItem> extends Control<DualListModel, DualListView> {

	public void showDualListPart() {
		getView().showDualListPart();
	}

	public void init() {
		initUIListeners();
		initModelListeners();
		super.init();
	}

	public void teardown() {
		removeUIListeners();
		removeModelListeners();
		super.teardown();
	}

	private void initUIListeners() {
		getView().eventAttributeListChangeRequested().add(attributeListChangeRequestedListener); //TODO: fix this warning
		getView().eventSortingListChangeRequested().add(sortingListChangeRequestedListener);
	}

	private void initModelListeners() {
		getModel().eventAttributeListChanged().add(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().add(sortingListRefreshRequestedListener);
	}

	public void removeUIListeners() {
		getView().eventAttributeListChangeRequested().remove(attributeListChangeRequestedListener);
		getView().eventSortingListChangeRequested().remove(sortingListChangeRequestedListener);
	}

	public void removeModelListeners() {
		getModel().eventAttributeListChanged().remove(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().remove(sortingListRefreshRequestedListener);
	}

	public void enableOrdering() {
		getView().enableOrdering();
	}

	private NonGenericListenerCollection<List<SortingArg>> attributeListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<SortingArg>> eventAttributeListRefreshRequested() {
		return attributeListRefreshRequested;
	}

	private IListener<List<SortingArg>> attributeListRefreshRequestedListener = attributeListRefreshRequested::invoke;

	private NonGenericListenerCollection<List<SortingArg>> sortingListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<SortingArg>> eventSortingListRefreshRequested() {
		return sortingListRefreshRequested;
	}

	private IListener<List<SortingArg>> sortingListRefreshRequestedListener = sortingListRefreshRequested::invoke;

	private IListener<ItemMoveObject<TItem>> attributeListChangeRequestedListener = event -> {
		ArrayList<SortingArg> newAttributeList = new ArrayList<SortingArg>();
		if (event.getSourceIndex() == -2 && event.getDestinationIndex() == -1) {
			newAttributeList = null;
		} else if (event.getSourceIndex() == -2) {
			newAttributeList = event.getDestinationArray();
			newAttributeList.remove(event.getDestinationIndex());
		} else if (event.getSourceIndex() == -1 && event.getDestinationIndex() == -1) {
			newAttributeList = event.getSourceArray();
		} else if(event.getSourceIndex() == -1) {
			newAttributeList = event.getSourceArray();
			SortingArg selectedArgument = event.getItem();
			int selectedIndex = newAttributeList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			SortingArg swapArgument = newAttributeList.get(swapIndex);
			newAttributeList.set(selectedIndex, swapArgument);
			newAttributeList.set(swapIndex, event.getItem());
		}
		
		else {
			newAttributeList = event.getDestinationArray();
			newAttributeList.add(event.getItem());
		}
		ObservableList<SortingArg> newObservableAttributeList = FXCollections.observableArrayList(newAttributeList);
		getModel().setAttributeList(newObservableAttributeList);
	};

	private IListener<ItemMoveObject<TItem>> sortingListChangeRequestedListener = event -> {
		ArrayList<SortingArg> newSortingList = new ArrayList<SortingArg>();
		if (event.getSourceIndex() == -2 && event.getDestinationIndex() == -1) {
			newSortingList = null;
		}

		else if (event.getSourceIndex() == -2) {
			newSortingList = event.getDestinationArray();
			newSortingList.remove(event.getDestinationIndex());
		} else if (event.getSourceIndex() == -1 && event.getDestinationIndex() == -1) {
			newSortingList = event.getSourceArray();
		} else if(event.getSourceIndex() == -1) {
			newSortingList = event.getSourceArray();
			SortingArg selectedArgument = event.getItem();
			int selectedIndex = newSortingList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			SortingArg swapArgument = newSortingList.get(swapIndex);
			newSortingList.set(selectedIndex, swapArgument);
			newSortingList.set(swapIndex, event.getItem());
		}

		else {
			newSortingList = event.getDestinationArray();
			newSortingList.add(event.getItem());
		}
		ObservableList<SortingArg> newObservableSortingList = FXCollections.observableArrayList(newSortingList);
		getModel().setSortingList(newObservableSortingList);
	};
}
