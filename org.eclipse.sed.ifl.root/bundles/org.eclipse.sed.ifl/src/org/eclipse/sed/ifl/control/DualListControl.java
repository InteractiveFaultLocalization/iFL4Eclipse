package org.eclipse.sed.ifl.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.model.DualListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.view.DualListView;

public class DualListControl<TItem> extends Control<DualListModel, DualListView> {

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
	}

	private void initModelListeners() {
		getModel().eventAttributeListChanged().add(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().add(sortingListRefreshRequestedListener);
	}
	
	public void removeUIListeners() {
		getView().eventAttributeListChangeRequested().remove(attributeListChangeRequestedListener);
		getView().eventSortingListChangeRequested().remove(sortingListChangeRequestedListener);
	}
	
	public void removeModelListeners() {
		getModel().eventAttributeListChanged().remove(attributeListRefreshRequestedListener);
		getModel().eventSortingListChanged().remove(sortingListRefreshRequestedListener);
	}

	public void enableOrdering() {
		getView().enableOrdering();
	}
	
	private NonGenericListenerCollection<List<SortingArg>> attributeListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<SortingArg>> eventAttributeListRefreshRequested() {
		return attributeListRefreshRequested;
	}

	private IListener<List<SortingArg>> attributeListRefreshRequestedListener = attributeListRefreshRequested::invoke;
	
	private NonGenericListenerCollection<List<SortingArg>> sortingListRefreshRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<SortingArg>> eventSortingListRefreshRequested() {
		return sortingListRefreshRequested;
	}
	
	private IListener<List<SortingArg>> sortingListRefreshRequestedListener = sortingListRefreshRequested::invoke;
	
	private IListener<valami> attributeListChangeRequestedListener = event -> {
		//TODO: bejövõ event megmondja, hogy milyen item hova lett mozgatva, és itt lenne a logika, ami összerakja az új listát
		ArrayList<SortingArg> newAttributeList = new ArrayList<SortingArg>();
		getModel().setAttributeList(newAttributeList);
	};
	
	private IListener<valami> sortingListChangeRequestedListener = event -> {
		//TODO: bejövõ event megmondja, hogy milyen item hova lett mozgatva, és itt lenne a logika, ami összerakja az új listát
		ArrayList<SortingArg> newSortingList = new ArrayList<SortingArg>();
		getModel().setSortingList(newSortingList); 
	};
}
