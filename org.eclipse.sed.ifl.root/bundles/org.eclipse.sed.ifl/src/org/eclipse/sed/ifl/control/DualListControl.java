package org.eclipse.sed.ifl.control;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.score.Sortable;
import org.eclipse.sed.ifl.model.DualListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.view.DualListView;

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
		getView().eventAddAllToSortingListRequested().add(addAllToSortingListRequestedListener);
		getView().eventAddOneToSortingListRequested().add(addOneToSortingListRequestedListener);
		getView().eventRemoveAllFromSortingListRequested().add(removeAllFromSortingListRequestedListener);
		getView().eventRemoveOneFromSortingListRequested().add(removeOneFromSortingListRequestedListener);
		getView().eventMoveOneDownInSortingListRequested().add(moveOneDownInSortingListRequestedListener);
		getView().eventMoveOneUpInSortingListRequested().add(moveOneUpInSortingListRequestedListener);
		getView().eventMoveToTopInSortingListRequested().add(moveToTopInSortingListRequestedListener);
		getView().eventMoveToBottomInSortingListRequested().add(moveToBottomInSortingListRequestedListener);
		getView().eventOrderingDirectionChanged().add(orderingDirectionChangedListener);
	}

	private void initModelListeners() {
		getModel().eventAttributeListChanged().add(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().add(sortingListRefreshRequestedListener);
		getModel().eventAttributeListSelectionChanged().add(attributeListSelectionRequestedListener);
		getModel().eventSortingListSelectionChanged().add(sortingListSelectionRequestedListener);
	}

	public void removeViewListeners() {
		getView().eventAddAllToSortingListRequested().remove(addAllToSortingListRequestedListener);
		getView().eventAddOneToSortingListRequested().remove(addOneToSortingListRequestedListener);
		getView().eventRemoveAllFromSortingListRequested().remove(removeAllFromSortingListRequestedListener);
		getView().eventRemoveOneFromSortingListRequested().remove(removeOneFromSortingListRequestedListener);
		getView().eventMoveOneDownInSortingListRequested().remove(moveOneDownInSortingListRequestedListener);
		getView().eventMoveOneUpInSortingListRequested().remove(moveOneUpInSortingListRequestedListener);
		getView().eventMoveToTopInSortingListRequested().remove(moveToTopInSortingListRequestedListener);
		getView().eventMoveToBottomInSortingListRequested().remove(moveToBottomInSortingListRequestedListener);
		getView().eventOrderingDirectionChanged().remove(orderingDirectionChangedListener);
	}

	public void removeModelListeners() {
		getModel().eventAttributeListChanged().remove(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().remove(sortingListRefreshRequestedListener);
		getModel().eventAttributeListSelectionChanged().remove(attributeListSelectionRequestedListener);
		getModel().eventSortingListSelectionChanged().remove(sortingListSelectionRequestedListener);
	}

	public void enableOrdering() {
		getView().enableOrdering();
		refreshAttributeListOnView();
	}

	private NonGenericListenerCollection<Sortable> addAllToSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventAddAllToSortingListRequested() {
		return addAllToSortingListRequested;
	}

	private IListener<EmptyEvent> addAllToSortingListRequestedListener = event -> {
		List<Sortable> attributeList = getModel().getAttributeList().stream().collect(Collectors.toList());
		getModel().addToSortingList(attributeList);
	};

	private NonGenericListenerCollection<Sortable> addOneToSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventAddOneToSortingListRequested() {
		return addOneToSortingListRequested;
	}

	private IListener<Sortable> addOneToSortingListRequestedListener = event -> {
		if (event != null) {
			List<Sortable> attributeList = new ArrayList<Sortable>();
			attributeList.add(event);
			getModel().addToSortingList(attributeList);
		}
		else
			throw new UnsupportedOperationException("Valid item to move is not selected.");
	};

	private NonGenericListenerCollection<Sortable> removeOneFromSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventRemoveOneFromSortingListRequested() {
		return removeOneFromSortingListRequested;
	}

	private IListener<Sortable> removeOneFromSortingListRequestedListener = event -> {
		if (event != null) {
			List<Sortable> sortingList = new ArrayList<Sortable>();
			sortingList.add(event);
			getModel().removeFromSortingList(sortingList);
		}
		else
			throw new UnsupportedOperationException("Valid item to move is not selected.");

	};

	private NonGenericListenerCollection<Sortable> removeAllFromSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventRemoveAllFromSortingListRequested() {
		return removeAllFromSortingListRequested;
	}

	private IListener<EmptyEvent> removeAllFromSortingListRequestedListener = event -> {
		List<Sortable> sortingList = getModel().getSortingList().stream().collect(Collectors.toList());
		getModel().removeFromSortingList(sortingList);
	};

	private NonGenericListenerCollection<Sortable> moveToTopInSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventMoveToTopInSortingListRequested() {
		return moveToTopInSortingListRequested;
	}

	private IListener<Sortable> moveToTopInSortingListRequestedListener = event -> {
		if (event != null) {
			int sourceIndex = getModel().getSortingList().indexOf(event);
			getModel().moveInsideSortingList(event, sourceIndex, 0);
		} else
			throw new UnsupportedOperationException("Valid item to move is not selected.");
	};

	private NonGenericListenerCollection<Sortable> moveOneUpInSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventMoveOneUpInSortingListRequested() {
		return moveOneUpInSortingListRequested;
	}

	private IListener<Sortable> moveOneUpInSortingListRequestedListener = event -> {
		if (event != null) {
			int sourceIndex = getModel().getSortingList().indexOf(event);
			getModel().moveInsideSortingList(event, sourceIndex, sourceIndex - 1);
		} else
			throw new UnsupportedOperationException("Valid item to move is not selected.");
	};

	private NonGenericListenerCollection<Sortable> moveToBottomInSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventMoveToBottomInSortingListRequested() {
		return moveToBottomInSortingListRequested;
	}

	private IListener<Sortable> moveToBottomInSortingListRequestedListener = event -> {
		if (event != null) {
			int sourceIndex = getModel().getSortingList().indexOf(event);
			getModel().moveInsideSortingList(event, sourceIndex, getModel().getSortingList().size() - 1);
		} else
			throw new UnsupportedOperationException("Valid item to move is not selected.");
	};

	private NonGenericListenerCollection<Sortable> moveOneDownInSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventmoveOneDownInSortingListRequested() {
		return moveOneDownInSortingListRequested;
	}

	private IListener<Sortable> moveOneDownInSortingListRequestedListener = event -> {
		if (event != null) {
			int sourceIndex = getModel().getSortingList().indexOf(event);
			getModel().moveInsideSortingList(event, sourceIndex, sourceIndex + 1);
		} else
			throw new UnsupportedOperationException("Valid item to move is not selected.");
	};

	private NonGenericListenerCollection<Sortable> orderingDirectionChanged = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventOrderingDirectionChanged() {
		return orderingDirectionChanged;
	}

	private IListener<Sortable> orderingDirectionChangedListener = event -> {
		Sortable originalArg = getModel().getSortingList().stream()
				.filter(original -> event.equals(original))
				.findAny()
				.orElse(null);
		if(originalArg != null) {
			//System.out.println("before:   " + ((Object)originalArg.directionProperty).toString());
			//System.out.println("before:   " + Integer.toHexString(((Object)originalArg.directionProperty).hashCode()));
			originalArg.setSortingDirection(originalArg.getSortingDirection().equals(Sortable.SortingDirection.Ascending)
					? Sortable.SortingDirection.Descending
					: Sortable.SortingDirection.Ascending);
			//TODO: find out why it is necessary to explicitly call the directionProperty of the Sortable object for the ObservableList
			// to detect the changes
			System.out.println(originalArg.directionProperty);
			//System.out.println(((Object)originalArg.directionProperty).toString());
			//System.out.println(Integer.toHexString(((Object)originalArg.directionProperty).hashCode()));
		}
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

	// Right

	private NonGenericListenerCollection<List<Sortable>> sortingListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventSortingListRefreshRequested() {
		return sortingListRefreshRequested;
	}

	private NonGenericListenerCollection<List<Sortable>> updateSorting = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventUpdateSorting() {
		return updateSorting;
	}

	private IListener<List<Sortable>> sortingListRefreshRequestedListener = event -> {
		updateSorting.invoke(event);
		getView().sortingListRefresh(event);
	};

	private NonGenericListenerCollection<Integer> attributeListSelectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventAttributeListSelectionRequested() {
		return attributeListSelectionRequested;
	}

	private IListener<Integer> attributeListSelectionRequestedListener = event -> {
		getView().attributeListSelectionRefresh(event);
	};

	private NonGenericListenerCollection<Integer> sortingListSelectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSortingListSelectionRequested() {
		return sortingListSelectionRequested;
	}

	private IListener<Integer> sortingListSelectionRequestedListener = event -> {
		getView().sortingListSelectionRefresh(event);
	};

	public void close() {
		getView().close();
	}

}
