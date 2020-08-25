package org.eclipse.sed.ifl.control.score;

import java.util.List;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.ide.gui.DualListPart.moveBetweenListsListener;
import org.eclipse.sed.ifl.ide.gui.DualListPart.moveInsideListListener;
import org.eclipse.sed.ifl.model.DualListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.sed.ifl.view.DualListView;

public class DualListControl extends Control<DualListModel, DualListView> {

	public void showDualListPart() {
		getView().showDualListPart();
	}

	public void init() {
		super.init();
	}

	private void initUIListeners() {

		getView().buttonAllRight().addListener(SWT.Selection, new moveBetweenListsListener()); // ugyanaz a hiba mint a
																								// View-ban

		getView().buttonOneRight().addListener(SWT.Selection, new moveBetweenListsListener());

		getView().buttonOneLeft().addListener(SWT.Selection, new moveBetweenListsListener());

		getView().buttonAllLeft().addListener(SWT.Selection, new moveBetweenListsListener());

		getView().buttonAllUp().addListener(SWT.Selection, new moveInsideListListener());

		getView().buttonOneUp().addListener(SWT.Selection, new moveInsideListListener());

		getView().buttonOneDown().addListener(SWT.Selection, new moveInsideListListener());

		getView().buttonAllDown().addListener(SWT.Selection, new moveInsideListListener());

	}

	public void removeUIListeners() {

		getView().buttonAllRight().removeListener(SWT.Selection, new moveBetweenListsListener()); // Deja Vu :/
																						
		getView().buttonOneRight().removeListener(SWT.Selection, new moveBetweenListsListener());

		getView().buttonOneLeft().removeListener(SWT.Selection, new moveBetweenListsListener());

		getView().buttonAllLeft().removeListener(SWT.Selection, new moveBetweenListsListener());

		getView().buttonAllUp().removeListener(SWT.Selection, new moveInsideListListener());

		getView().buttonOneUp().removeListener(SWT.Selection, new moveInsideListListener());

		getView().buttonOneDown().removeListener(SWT.Selection, new moveInsideListListener());

		getView().buttonAllDown().removeListener(SWT.Selection, new moveInsideListListener());

	}

	public void teardown() {
		removeUIListeners();
		super.teardown();
	}

}
