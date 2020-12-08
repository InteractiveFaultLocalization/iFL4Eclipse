package org.eclipse.sed.ifl.view;

import java.util.List;


import org.eclipse.sed.ifl.control.ItemMoveObject;
import org.eclipse.sed.ifl.control.score.Sortable;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.DualListPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DualListView<TItem extends Sortable> extends View implements IEmbeddable, IEmbedee {

	private DualListPart<TItem> dualListPart;

	public DualListView() {
		this.dualListPart = (DualListPart<TItem>) getPart();
	}

	private IViewPart getPart() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(DualListPart.ID);

		if (view != null || page.isPartVisible(view)) {
			page.hideView(view);
		}
		EU.tryUnchecked(() -> page.showView(DualListPart.ID));
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
		dualListPart.eventAttributeListSelectionRequested().add(attributeListSelectionRequestedListener);
		dualListPart.eventSortingListSelectionRequested().add(sortingListSelectionRequestedListener);
		dualListPart.eventAttributeListButtonPressed().add(attributeListButtonPressedListener);
		dualListPart.eventAttributeListChangeRequested().add(attributeListChangeRequestedListener);
		dualListPart.eventSortingListButtonPressed().add(sortingListButtonPressedListener);
		dualListPart.eventSortingListChangeRequested().add(sortingListChangeRequestedListener);
		dualListPart.eventOrderingDirectionChanged().add(orderingDirectionChangedListener);
		
	}

	private void removeUIListeners() {
		dualListPart.eventAttributeListSelectionRequested().remove(attributeListSelectionRequestedListener);
		dualListPart.eventSortingListSelectionRequested().remove(sortingListSelectionRequestedListener);
		dualListPart.eventAttributeListButtonPressed().remove(attributeListButtonPressedListener);
		dualListPart.eventAttributeListChangeRequested().remove(attributeListChangeRequestedListener);
		dualListPart.eventSortingListButtonPressed().remove(sortingListButtonPressedListener);
		dualListPart.eventSortingListChangeRequested().remove(sortingListChangeRequestedListener);
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


	private NonGenericListenerCollection<Sortable> orderingDirectionChanged = new NonGenericListenerCollection<>();

	public NonGenericListenerCollection<Sortable> eventOrderingDirectionChanged() {
		return orderingDirectionChanged;
	}

	private IListener<Sortable> orderingDirectionChangedListener = orderingDirectionChanged::invoke;

	private NonGenericListenerCollection<ItemMoveObject<TItem>> attributeListChangeRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventAttributeListChangeRequested() {
		return attributeListChangeRequested;
	}
	
	private IListener<ItemMoveObject<TItem>> attributeListChangeRequestedListener = attributeListChangeRequested::invoke;
	
	private NonGenericListenerCollection<ItemMoveObject<TItem>> attributeListButtonPressed = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventAttributeListButtonPressed() {
		return attributeListButtonPressed;
	}

	private IListener<ItemMoveObject<TItem>> attributeListButtonPressedListener = attributeListButtonPressed::invoke;

	private NonGenericListenerCollection<ItemMoveObject<TItem>> sortingListChangeRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventSortingListChangeRequested() {
		return sortingListChangeRequested;
	}

	private IListener<ItemMoveObject<TItem>> sortingListChangeRequestedListener = sortingListChangeRequested::invoke;
	
	private NonGenericListenerCollection<ItemMoveObject<TItem>> sortingListButtonPressed = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventSortingListButtonPressed() {
		return sortingListButtonPressed;
	}

	private IListener<ItemMoveObject<TItem>> sortingListButtonPressedListener = sortingListButtonPressed::invoke;

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
		System.out.println("View "+ event);
	};
	
	private NonGenericListenerCollection<Integer> sortingListSelectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSortingListSelectionRequested() {
		return sortingListSelectionRequested;
	}

	private IListener<Integer> sortingListSelectionRequestedListener = event -> {
		dualListPart.setSortingTableSelection(event);
		System.out.println("View "+ event);
	};

}
