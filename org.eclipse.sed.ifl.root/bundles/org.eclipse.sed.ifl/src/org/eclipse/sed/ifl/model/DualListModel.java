package org.eclipse.sed.ifl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DualListModel extends EmptyModel {
	
	private ObservableList<SortingArg> attributeList = FXCollections.observableArrayList();
	private ObservableList<SortingArg> sortingList = FXCollections.observableArrayList();
	private Map<String,SortingArg> elementMap = new HashMap<>(); 
	
	public DualListModel(ArrayList<SortingArg> attributes) {
		for(SortingArg arg : attributes) {
			this.attributeList.add(arg);
		}
		
		this.attributeList.addListener(changeListener);
		this.sortingList.addListener(changeListener);
	}
	
	public ObservableList<SortingArg> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(ObservableList<SortingArg> attributeList) {
		this.attributeList = attributeList;
		attributeListChangedListener.invoke(attributeList);
	}

	public ObservableList<SortingArg> getSortingList() {
		return sortingList;
	}

	public void setSortingList(ObservableList<SortingArg> sortingList) {
		this.sortingList = sortingList;
		sortingListChangedListener.invoke(sortingList);
	}

	private NonGenericListenerCollection<List<SortingArg>> attributeListChangedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<SortingArg>> eventAttributeListChanged() {
		return attributeListChangedListener;
	}
	
	private NonGenericListenerCollection<List<SortingArg>> sortingListChangedListener = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<SortingArg>> eventSortingListChanged() {
		return sortingListChangedListener;
	}
	
	private ListChangeListener<SortingArg> changeListener = c -> {
		while(c.next()) {
			if(c.getList().equals(attributeList)) {
				attributeListChangedListener.invoke(attributeList);
			} else if (c.getList().equals(sortingList)) {
				sortingListChangedListener.invoke(sortingList);
			}
		}
	};
}
	