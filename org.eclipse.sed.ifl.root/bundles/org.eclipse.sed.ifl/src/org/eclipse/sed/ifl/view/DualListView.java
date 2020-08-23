package org.eclipse.sed.ifl.view;

import java.util.List;

import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.control.score.filter.BooleanRule;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.LastActionRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.SortRule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
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

public class DualListView extends View implements IEmbeddable, IEmbedee {

	private DualListPart dualListPart;

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

		// listának külön listener

		/*
		 * 
		 * 
		 * dualListPart.allRight.addListener(SWT.Selection, new
		 * moveBetweenListsListener());
		 * 
		 * dualListPart.oneRight.addListener(SWT.Selection, new
		 * moveBetweenListsListener());
		 * 
		 * dualListPart.oneLeft.addListener(SWT.Selection, new
		 * moveBetweenListsListener());
		 * 
		 * dualListPart.allLeft.addListener(SWT.Selection, new
		 * moveBetweenListsListener());
		 * 
		 * dualListPart.allUp.addListener(SWT.Selection, new moveInsideListListener());
		 * 
		 * dualListPart.oneUp.addListener(SWT.Selection, new moveInsideListListener());
		 * 
		 * dualListPart.oneDown.addListener(SWT.Selection, new
		 * moveInsideListListener());
		 * 
		 * dualListPart.allDown.addListener(SWT.Selection, new
		 * moveInsideListListener());
		 * 
		 */

	}

	private void removeUIListeners() {

	// listának külön listener

	/*
	 * 
	 * oneRight.removeListener(SWT.Selection, new moveBetweenListsListener());
	 * 
	 * oneLeft.removeListener(SWT.Selection, new moveBetweenListsListener());
	 * 
	 * allLeft.removeListener(SWT.Selection, new moveBetweenListsListener());
	 * 
	 * allUp.removeListener(SWT.Selection, new moveInsideListListener());
	 * 
	 * oneUp.removeListener(SWT.Selection, new moveInsideListListener());
	 * 
	 * oneDown.removeListener(SWT.Selection, new moveInsideListListener());
	 * 
	 * allDown.removeListener(SWT.Selection, new moveInsideListListener()); }
	 * 
	 */

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

}
