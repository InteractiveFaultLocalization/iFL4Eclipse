package org.eclipse.sed.ifl.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sed.ifl.control.score.Sortable;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DualListModel extends EmptyModel {
	
	private ObservableList<Sortable> attributeList = FXCollections.observableArrayList();
	private ObservableList<Sortable> sortingList = FXCollections.observableArrayList();
	private int attributeListSelection = -1;
	private int sortingListSelection = -1;
	
	public DualListModel(ArrayList<Sortable> attributes) {
		for(Sortable arg : attributes) {
			this.attributeList.add(arg);
		}
		
		this.attributeList.addListener(changeListener);
		this.sortingList.addListener(changeListener);
	}
	
	public ObservableList<Sortable> getAttributeList() {
		return attributeList;
	}
	
	public void setAttributeListSelection(int selection) {
		this.attributeListSelection = selection;
	}

	public void setAttributeList(ObservableList<Sortable> attributeList) {
		this.attributeList = attributeList;
		attributeListChangedListener.invoke(attributeList);
		attributeListSelectionChangedListener.invoke(attributeListSelection);
		sortingListSelectionChangedListener.invoke(sortingListSelection);
		System.out.println("Model "+ attributeListSelection + sortingListSelection);
	}

	public ObservableList<Sortable> getSortingList() {
		return sortingList;
	}
	
	public void setSortingListSelection(int selection) {
		this.sortingListSelection = selection;
	}

	public void setSortingList(ObservableList<Sortable> sortingList) {
		this.sortingList = sortingList;
		sortingListChangedListener.invoke(sortingList);
		attributeListSelectionChangedListener.invoke(attributeListSelection);
		sortingListSelectionChangedListener.invoke(sortingListSelection);
		System.out.println("Model "+ attributeListSelection + sortingListSelection);
	}

	private NonGenericListenerCollection<List<Sortable>> attributeListChangedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventAttributeListChanged() {
		return attributeListChangedListener;
	}
	
	private NonGenericListenerCollection<List<Sortable>> sortingListChangedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventSortingListChanged() {
		return sortingListChangedListener;
	}
	
	private NonGenericListenerCollection<Integer> attributeListSelectionChangedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventAttributeListSelectionChanged() {
		return attributeListSelectionChangedListener;
	}
	
	private NonGenericListenerCollection<Integer> sortingListSelectionChangedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventSortingListSelectionChanged() {
		return sortingListSelectionChangedListener;
	}
	
	private ListChangeListener<Sortable> changeListener = c -> {
		while(c.next()) {
			if(c.getList().equals(attributeList)) {
				attributeListChangedListener.invoke(attributeList);
			} else if (c.getList().equals(sortingList)) {
				sortingListChangedListener.invoke(sortingList);
			}
			else {
				throw new UnsupportedOperationException("This list is ot supported by this class.");
			}
		}
	};
}
	