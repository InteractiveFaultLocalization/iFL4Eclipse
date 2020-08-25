package org.eclipse.sed.ifl.view;

import java.util.List;


import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.DualListPart;
import org.eclipse.sed.ifl.ide.gui.DualListPart.moveBetweenListsListener;
import org.eclipse.sed.ifl.ide.gui.DualListPart.moveInsideListListener;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
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

		dualListPart.getAllRight().addListener(SWT.Selection, new moveBetweenListsListener()); // I don't understand
																							   // this error.

		dualListPart.getOneRight().addListener(SWT.Selection, new moveBetweenListsListener());

		dualListPart.getOneLeft().addListener(SWT.Selection, new moveBetweenListsListener());

		dualListPart.getAllLeft().addListener(SWT.Selection, new moveBetweenListsListener());

		dualListPart.getAllUp().addListener(SWT.Selection, new moveInsideListListener());

		dualListPart.getOneUp().addListener(SWT.Selection, new moveInsideListListener());

		dualListPart.getOneDown().addListener(SWT.Selection, new moveInsideListListener());

		dualListPart.getAllDown().addListener(SWT.Selection, new moveInsideListListener());

	}

	private void removeUIListeners() {

		dualListPart.getAllRight().removeListener(SWT.Selection, new moveBetweenListsListener()); // Same thing here.
																								  // Also, how do I remove
																								  // the added listener?
		dualListPart.getOneRight().removeListener(SWT.Selection, new moveBetweenListsListener());

		dualListPart.getOneLeft().removeListener(SWT.Selection, new moveBetweenListsListener());

		dualListPart.getAllLeft().removeListener(SWT.Selection, new moveBetweenListsListener());

		dualListPart.getAllUp().removeListener(SWT.Selection, new moveInsideListListener());

		dualListPart.getOneUp().removeListener(SWT.Selection, new moveInsideListListener());

		dualListPart.getOneDown().removeListener(SWT.Selection, new moveInsideListListener());

		dualListPart.getAllDown().removeListener(SWT.Selection, new moveInsideListListener());

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
	
	public Button buttonAllRight() {
		return dualListPart.getAllRight();
	}
	
	public Button buttonAllLeft() {
		return dualListPart.getAllLeft();
	}
	
	public Button buttonOneRight() {
		return dualListPart.getOneRight();
	}
	
	public Button buttonOneLeft() {
		return dualListPart.getOneLeft();
	}
	
	public Button buttonAllUp() {
		return dualListPart.getAllUp();
	}
	
	public Button buttonOneUp() {
		return dualListPart.getOneUp();
	}
	
	public Button buttonOneDown() {
		return dualListPart.getOneDown();
	}
	
	public Button buttonAllDown() {
		return dualListPart.getAllDown();
	}

}
