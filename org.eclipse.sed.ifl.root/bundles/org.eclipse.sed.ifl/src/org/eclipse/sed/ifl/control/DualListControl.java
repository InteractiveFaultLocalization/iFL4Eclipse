package org.eclipse.sed.ifl.control;


import java.util.List;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.model.DualListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.view.DualListView;


import javafx.collections.ObservableList;

public class DualListControl<TItem extends SortingArg> extends Control<DualListModel, DualListView<TItem>> {

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
		getView().eventAttributeListChangeRequested().add(attributeListChangeRequestedListener); 
		getView().eventSortingListChangeRequested().add(sortingListChangeRequestedListener);
		getView().eventOrderingDirectionChanged().add(orderingDirectionChangedListener);
	}

	private void initModelListeners() {
		getModel().eventAttributeListChanged().add(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().add(sortingListRefreshRequestedListener);
	}

	public void removeUIListeners() {
		getView().eventAttributeListChangeRequested().remove(attributeListChangeRequestedListener);
		getView().eventSortingListChangeRequested().remove(sortingListChangeRequestedListener);
		getView().eventOrderingDirectionChanged().remove(orderingDirectionChangedListener);
	}

	public void removeModelListeners() {
		getModel().eventAttributeListChanged().remove(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().remove(sortingListRefreshRequestedListener);
	}

	public void enableOrdering() {
		getView().enableOrdering();
	}
	
	private NonGenericListenerCollection<SortingArg> orderingDirectionChanged = new NonGenericListenerCollection<>();
	
	public NonGenericListenerCollection<SortingArg> eventOrderingDirectionChanged() {
		return orderingDirectionChanged;
	}
	
	private IListener<SortingArg> orderingDirectionChangedListener = event -> {
		ObservableList<SortingArg> newSortingList = getModel().getSortingList();
		SortingArg originalArg = event;
		originalArg.setDescending(!originalArg.isDescending());
		int swapIndex = newSortingList.indexOf(originalArg);
		newSortingList.set(swapIndex,event);
		getModel().setSortingList(newSortingList);
	};
	
	//Left

	private NonGenericListenerCollection<List<SortingArg>> attributeListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<SortingArg>> eventAttributeListRefreshRequested() {
		return attributeListRefreshRequested;
	}

	private IListener<List<SortingArg>> attributeListRefreshRequestedListener = attributeListRefreshRequested::invoke;

	
	//Right
	
	private NonGenericListenerCollection<List<SortingArg>> sortingListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<SortingArg>> eventSortingListRefreshRequested() {
		return sortingListRefreshRequested;
	}

	private IListener<List<SortingArg>> sortingListRefreshRequestedListener = sortingListRefreshRequested::invoke;

	private IListener<ItemMoveObject<TItem>> attributeListChangeRequestedListener = event -> { //array from model
		ObservableList<SortingArg> newAttributeList;
		if (event.getSourceIndex() == -2 && event.getDestinationIndex() == -1) {
			newAttributeList = null;
		} else if (event.getSourceIndex() == -2) {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.remove(event.getDestinationIndex());
		} else if (event.getSourceIndex() == -1 && event.getDestinationIndex() == -1) {
			newAttributeList = getModel().getSortingList();
		} else if(event.getSourceIndex() == -1) {
			newAttributeList = getModel().getAttributeList();
			SortingArg selectedArgument = event.getItem();
			int selectedIndex = newAttributeList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			SortingArg swapArgument = newAttributeList.get(swapIndex);
			newAttributeList.set(selectedIndex, swapArgument);
			newAttributeList.set(swapIndex, event.getItem());
		}
		
		else {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.add(event.getItem());
		}
		getModel().setAttributeList(newAttributeList);
	};

	private IListener<ItemMoveObject<TItem>> sortingListChangeRequestedListener = event -> {
		ObservableList<SortingArg> newSortingList;
		if (event.getSourceIndex() == -2 && event.getDestinationIndex() == -1) {
			newSortingList = null;
		}

		else if (event.getSourceIndex() == -2) {
			newSortingList = getModel().getSortingList();
			newSortingList.remove(event.getDestinationIndex());
		} else if (event.getSourceIndex() == -1 && event.getDestinationIndex() == -1) {
			newSortingList = getModel().getAttributeList();
		} else if(event.getSourceIndex() == -1) {
			newSortingList = getModel().getSortingList();
			SortingArg selectedArgument = event.getItem();
			int selectedIndex = newSortingList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			SortingArg swapArgument = newSortingList.get(swapIndex);
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
