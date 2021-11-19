package org.eclipse.sed.ifl.view;

import java.util.List;

import org.eclipse.sed.ifl.control.score.Sortable;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.DualListPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DualListView<TItem extends Sortable> extends View implements IEmbeddable, IEmbedee {

	private DualListPart<TItem> dualListPart;

	@SuppressWarnings("unchecked")
	public DualListView() {
		this.dualListPart = (DualListPart<TItem>) getPart();
	}

	private IViewPart getPart() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(DualListPart.ID);

		if (view != null || page.isPartVisible(view)) {
			page.hideView(view);
		}
		EU.tryUnchecked(() -> page.showView(DualListPart.ID, null, IWorkbenchPage.VIEW_CREATE));
		view = page.findView(DualListPart.ID);
		if (view == null) {
			throw new RuntimeException();
		} else {
			return view;
		}
	}

	@Override
	public void init() {
		initUIListeners();
		super.init();
	}

	@Override
	public void teardown() {
		removeUIListeners();
		super.teardown();
	}

	private void initUIListeners() {
		dualListPart.eventAddAllToSortingListRequested().add(addAllToSortingListRequestedListener);
		dualListPart.eventAddOneToSortingListRequested().add(addOneToSortingListRequestedListener);
		dualListPart.eventRemoveAllFromSortingListRequested().add(removeAllFromSortingListRequestedListener);
		dualListPart.eventRemoveOneFromSortingListRequested().add(removeOneFromSortingListRequestedListener);
		dualListPart.eventMoveOneDownInSortingListRequested().add(moveOneDownInSortingListRequestedListener);
		dualListPart.eventMoveOneUpInSortingListRequested().add(moveOneUpInSortingListRequestedListener);
		dualListPart.eventMoveToTopInSortingListRequested().add(moveToTopInSortingListRequestedListener);
		dualListPart.eventMoveToBottomInSortingListRequested().add(moveToBottomInSortingListRequestedListener);
		dualListPart.eventAttributeListSelectionRequested().add(attributeListSelectionRequestedListener);
		dualListPart.eventSortingListSelectionRequested().add(sortingListSelectionRequestedListener);
		dualListPart.eventOrderingDirectionChanged().add(orderingDirectionChangedListener);

	}

	private void removeUIListeners() {
		dualListPart.eventAddAllToSortingListRequested().remove(addAllToSortingListRequestedListener);
		dualListPart.eventAddOneToSortingListRequested().remove(addOneToSortingListRequestedListener);
		dualListPart.eventRemoveAllFromSortingListRequested().remove(removeAllFromSortingListRequestedListener);
		dualListPart.eventRemoveOneFromSortingListRequested().remove(removeOneFromSortingListRequestedListener);
		dualListPart.eventMoveOneDownInSortingListRequested().remove(moveOneDownInSortingListRequestedListener);
		dualListPart.eventMoveOneUpInSortingListRequested().remove(moveOneUpInSortingListRequestedListener);
		dualListPart.eventMoveToTopInSortingListRequested().remove(moveToTopInSortingListRequestedListener);
		dualListPart.eventMoveToBottomInSortingListRequested().remove(moveToBottomInSortingListRequestedListener);
		dualListPart.eventAttributeListSelectionRequested().remove(attributeListSelectionRequestedListener);
		dualListPart.eventSortingListSelectionRequested().remove(sortingListSelectionRequestedListener);
		dualListPart.eventOrderingDirectionChanged().remove(orderingDirectionChangedListener);
	}

	public void attributeListRefresh(List<Sortable> attributeList) {
		attributeListRefreshRequestedListener.invoke(attributeList);
	}

	public void sortingListRefresh(List<Sortable> sortingList) {
		sortingListRefreshRequestedListener.invoke(sortingList);
	}

	public void attributeListSelectionRefresh(int selection) {
		attributeListSelectionRequestedListener.invoke(selection);
	}

	public void sortingListSelectionRefresh(int selection) {
		sortingListSelectionRequestedListener.invoke(selection);
	}

	@Override
	public void embed(IEmbeddable embedded) {
		dualListPart.embed(embedded);

	}

	@Override
	public void setParent(Composite parent) {
		dualListPart.setParent(parent);

	}

	@SuppressWarnings("unchecked")
	public void showDualListPart() {
		removeUIListeners();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			this.dualListPart = (DualListPart<TItem>) page.showView(DualListPart.ID);
		} catch (PartInitException e) {
			System.out.println("Could not open dual list view.");
		}
		initUIListeners();
	}

	public void enableOrdering() {
		dualListPart.enableOrdering();
	}

	public void close() {
		if (dualListPart.getSite().getPart() != null) {
			dualListPart.getSite().getPage().hideView(dualListPart);
		}
	}

	private NonGenericListenerCollection<EmptyEvent> addAllToSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<EmptyEvent> eventAddAllToSortingListRequested() {
		return addAllToSortingListRequested;
	}

	private IListener<EmptyEvent> addAllToSortingListRequestedListener = addAllToSortingListRequested::invoke;

	private NonGenericListenerCollection<Sortable> addOneToSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventAddOneToSortingListRequested() {
		return addOneToSortingListRequested;
	}

	private IListener<Sortable> addOneToSortingListRequestedListener = addOneToSortingListRequested::invoke;

	private NonGenericListenerCollection<Sortable> removeOneFromSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventRemoveOneFromSortingListRequested() {
		return removeOneFromSortingListRequested;
	}

	private IListener<Sortable> removeOneFromSortingListRequestedListener = removeOneFromSortingListRequested::invoke;

	private NonGenericListenerCollection<EmptyEvent> removeAllFromSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<EmptyEvent> eventRemoveAllFromSortingListRequested() {
		return removeAllFromSortingListRequested;
	}

	private IListener<EmptyEvent> removeAllFromSortingListRequestedListener = removeAllFromSortingListRequested::invoke;

	private NonGenericListenerCollection<Sortable> moveToTopInSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventMoveToTopInSortingListRequested() {
		return moveToTopInSortingListRequested;
	}

	private IListener<Sortable> moveToTopInSortingListRequestedListener = moveToTopInSortingListRequested::invoke;

	private NonGenericListenerCollection<Sortable> moveOneUpInSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventMoveOneUpInSortingListRequested() {
		return moveOneUpInSortingListRequested;
	}

	private IListener<Sortable> moveOneUpInSortingListRequestedListener = moveOneUpInSortingListRequested::invoke;

	private NonGenericListenerCollection<Sortable> moveToBottomInSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventMoveToBottomInSortingListRequested() {
		return moveToBottomInSortingListRequested;
	}

	private IListener<Sortable> moveToBottomInSortingListRequestedListener = moveToBottomInSortingListRequested::invoke;

	private NonGenericListenerCollection<Sortable> moveOneDownInSortingListRequested = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventMoveOneDownInSortingListRequested() {
		return moveOneDownInSortingListRequested;
	}

	private IListener<Sortable> moveOneDownInSortingListRequestedListener = moveOneDownInSortingListRequested::invoke;

	private NonGenericListenerCollection<Sortable> orderingDirectionChanged = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventOrderingDirectionChanged() {
		return orderingDirectionChanged;
	}

	private IListener<Sortable> orderingDirectionChangedListener = orderingDirectionChanged::invoke;


	private NonGenericListenerCollection<Integer> selectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSelectionRequested() {
		return selectionRequested;

	}

	private NonGenericListenerCollection<List<Sortable>> attributeListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventAttributeListRefreshRequested() {
		return attributeListRefreshRequested;
	}

	private IListener<List<Sortable>> attributeListRefreshRequestedListener = event -> {
		dualListPart.setAttributeTable(event);
	};

	private NonGenericListenerCollection<List<Sortable>> sortingListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventSortingListRefreshRequested() {
		return sortingListRefreshRequested;
	}

	private IListener<List<Sortable>> sortingListRefreshRequestedListener = event -> {
		dualListPart.setSortingTable(event);
	};

	private NonGenericListenerCollection<Integer> attributeListSelectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventAttributeListSelectionRequested() {
		return attributeListSelectionRequested;
	}

	private IListener<Integer> attributeListSelectionRequestedListener = event -> {
		dualListPart.setAttributeTableSelection(event);
	};

	private NonGenericListenerCollection<Integer> sortingListSelectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSortingListSelectionRequested() {
		return sortingListSelectionRequested;
	}

	private IListener<Integer> sortingListSelectionRequestedListener = event -> {
		dualListPart.setSortingTableSelection(event);
	};

	public void terminatePart() {
		dualListPart.terminate();
	}
}
