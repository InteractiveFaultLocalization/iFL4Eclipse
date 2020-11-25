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

	public void setAttributeList(ObservableList<Sortable> attributeList) {
		this.attributeList = attributeList;
		attributeListChangedListener.invoke(attributeList);
	}

	public ObservableList<Sortable> getSortingList() {
		return sortingList;
	}

	public void setSortingList(ObservableList<Sortable> sortingList) {
		this.sortingList = sortingList;
		sortingListChangedListener.invoke(sortingList);
		orderingRefreshedListener.invoke(sortingList);
	}
	
	private NonGenericListenerCollection<Sortable> orderingDirectionChangedListener = new NonGenericListenerCollection<>();
	
	public NonGenericListenerCollection<Sortable> orderingDirectionChanged() {
		return orderingDirectionChangedListener;
	}

	private NonGenericListenerCollection<List<Sortable>> attributeListChangedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventAttributeListChanged() {
		return attributeListChangedListener;
	}
	
	private NonGenericListenerCollection<List<Sortable>> sortingListChangedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventSortingListChanged() {
		return sortingListChangedListener;
	}
	
	private NonGenericListenerCollection<List<Sortable>> orderingRefreshedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Sortable>> eventOrderingRefresh() {
		return orderingRefreshedListener;
	}
	
	private ListChangeListener<Sortable> changeListener = c -> {
		while(c.next()) {
			if(c.getList().equals(attributeList)) {
				attributeListChangedListener.invoke(attributeList);
			} else if (c.getList().equals(sortingList)) {
				sortingListChangedListener.invoke(sortingList);
			}
		}
	};
}
	