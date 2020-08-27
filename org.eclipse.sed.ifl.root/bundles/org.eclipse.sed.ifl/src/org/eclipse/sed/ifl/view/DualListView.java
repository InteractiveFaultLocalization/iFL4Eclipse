package org.eclipse.sed.ifl.view;




import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.DualListPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DualListView extends View implements IEmbeddable, IEmbedee {

	private DualListPart<?> dualListPart;

	public DualListView() {
		this.dualListPart = (DualListPart) getPart();
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
		dualListPart.eventMoveBetweenListsRequested().add(moveBetweenListsRequestedListener);
		dualListPart.eventMoveInsideListRequested().add(moveInsideListRequestedListener);
		//dualListPart.eventMoveInsideListRequested().add(selectionRequestedListener); // does not work on SelectionEvent
	}

	private void removeUIListeners() {
		dualListPart.eventMoveBetweenListsRequested().remove(moveBetweenListsRequestedListener);
		dualListPart.eventMoveInsideListRequested().remove(moveInsideListRequestedListener);
		//dualListPart.eventMoveInsideListRequested().remove(selectionRequestedListener); //same
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
			this.dualListPart = (DualListPart) page.showView(DualListPart.ID);
		} catch (PartInitException e) {
			System.out.println("Could not open dual list view.");
		}
		initUIListeners();
	}

	public void close() {
		if (dualListPart.getSite().getPart() != null) {
			dualListPart.getSite().getPage().hideView(dualListPart);
		}
	}
	
	private NonGenericListenerCollection<SelectionEvent> selectionRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<SelectionEvent> eventSelectionRequested() {
		return selectionRequested;
	}

	private IListener<SelectionEvent> selectionRequestedListener = selectionRequested::invoke;
	
	private NonGenericListenerCollection<Event> moveBetweenListsRequested = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Event> eventMoveBetweenListsRequested() {
		return moveBetweenListsRequested;
	}
	
	private IListener<Event> moveBetweenListsRequestedListener = moveBetweenListsRequested::invoke;
	
	private NonGenericListenerCollection<Event> moveInsideListRequested = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Event> eventMoveInsideListRequested() {
		return moveInsideListRequested;
	}
	
	private IListener<Event> moveInsideListRequestedListener = moveInsideListRequested::invoke;
	
	

}
