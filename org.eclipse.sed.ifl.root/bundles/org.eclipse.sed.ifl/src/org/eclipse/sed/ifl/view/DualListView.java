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
		dualListPart.eventSelectionRequested().add(selectionRequestedListener);
		dualListPart.eventAttributeListChangeRequested().add(attributeListChangeRequestedListener);
		dualListPart.eventSortingListChangeRequested().add(sortingListChangeRequestedListener);
		dualListPart.eventOrderingDirectionChanged().add(orderingDirectionChangedListener);
	}

	private void removeUIListeners() {
		dualListPart.eventSelectionRequested().remove(selectionRequestedListener);
		dualListPart.eventAttributeListChangeRequested().remove(attributeListChangeRequestedListener);
		dualListPart.eventSortingListChangeRequested().remove(sortingListChangeRequestedListener);
		dualListPart.eventOrderingDirectionChanged().remove(orderingDirectionChangedListener);
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

	// refresht beépíteni

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

	private NonGenericListenerCollection<ItemMoveObject<TItem>> sortingListChangeRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ItemMoveObject<TItem>> eventSortingListChangeRequested() {
		return sortingListChangeRequested;
	}

	private IListener<ItemMoveObject<TItem>> sortingListChangeRequestedListener = sortingListChangeRequested::invoke;

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

	private IListener<Integer> selectionRequestedListener = selectionRequested::invoke;

}
