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
		setAttributeList();
	}
	
	private NonGenericListenerCollection<Sortable> orderingDirectionChanged = new NonGenericListenerCollection<>();
	
	public NonGenericListenerCollection<Sortable> eventOrderingDirectionChanged() {
		return orderingDirectionChanged;
	}
	
	private IListener<Sortable> orderingDirectionChangedListener = event -> {
		ObservableList<Sortable> newSortingList = getModel().getSortingList();
		Sortable originalArg = event;
		originalArg.setSortingDirection(originalArg.getSortingDirection().equals(Sortable.SortingDirection.Ascending) ? 
				Sortable.SortingDirection.Descending : Sortable.SortingDirection.Ascending);
		int swapIndex = newSortingList.indexOf(originalArg);
		newSortingList.set(swapIndex,event);
		getModel().setSortingList(newSortingList);
	};
	
	//Left

	private NonGenericListenerCollection<List<Sortable>> attributeListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventAttributeListRefreshRequested() {
		return attributeListRefreshRequested;
	}

	private IListener<List<Sortable>> attributeListRefreshRequestedListener = event -> {
		System.out.println("Did the program get here?");
		getView().attributeListRefresh(event);
		for(Sortable sort : event) {
			System.out.println(sort);
		}
	};

	private void setAttributeList() {
		getView().attributeListRefresh(getModel().getAttributeList());
	}
	
	//Right
	
	private NonGenericListenerCollection<List<Sortable>> sortingListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventSortingListRefreshRequested() {
		return sortingListRefreshRequested;
	}

	private IListener<List<Sortable>> sortingListRefreshRequestedListener = event -> {
		getView().sortingListRefresh(event);
	};

	private IListener<ItemMoveObject<TItem>> attributeListChangeRequestedListener = event -> { //array from model
		ObservableList<Sortable> newAttributeList;
		if (event.getSourceIndex() == -2 && event.getDestinationIndex() == -1) {
			newAttributeList = null;
		} else if (event.getSourceIndex() == -2) {
			newAttributeList = getModel().getAttributeList();
			newAttributeList.remove(event.getDestinationIndex());
		} else if (event.getSourceIndex() == -1 && event.getDestinationIndex() == -1) {
			newAttributeList = getModel().getSortingList();
		} else if(event.getSourceIndex() == -1) {
			newAttributeList = getModel().getAttributeList();
			Sortable selectedArgument = event.getItem();
			int selectedIndex = newAttributeList.indexOf(selectedArgument);
			int swapIndex = event.getDestinationIndex();
			Sortable swapArgument = newAttributeList.get(swapIndex);
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
		ObservableList<Sortable> newSortingList;
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
