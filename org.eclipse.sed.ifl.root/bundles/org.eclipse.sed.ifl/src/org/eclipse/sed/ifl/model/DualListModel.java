package org.eclipse.sed.ifl.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sed.ifl.control.score.Sortable;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class DualListModel extends EmptyModel {
	
	private ObservableList<Sortable> attributeList = FXCollections.observableArrayList(
			 new Callback<Sortable, Observable[]>() {
                 @Override
                 public Observable[] call(Sortable param) {
                     return new Observable[]{
                             param.nameProperty(),
                             param.directionProperty()
                     };
                 }
             });
	private ObservableList<Sortable> sortingList = FXCollections.observableArrayList(
			new Callback<Sortable, Observable[]>() {
                @Override
                public Observable[] call(Sortable param) {
                    return new Observable[]{
                            param.nameProperty(),
                            param.directionProperty()
                    };
                }
            });
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
		attributeListSelectionChangedListener.invoke(attributeListSelection);
	}

	public void setAttributeList(ObservableList<Sortable> attributeList) {
		this.attributeList = attributeList;
		attributeListSelectionChangedListener.invoke(attributeListSelection);
		sortingListSelectionChangedListener.invoke(sortingListSelection);
	}

	public ObservableList<Sortable> getSortingList() {
		return sortingList;
	}
	
	public void setSortingListSelection(int selection) {
		this.sortingListSelection = selection;
		sortingListSelectionChangedListener.invoke(sortingListSelection);
	}

	public void addToSortingList(List<Sortable> sortables) {
		for(Sortable sortable: sortables) {
			this.sortingList.add(sortable);
			this.attributeList.remove(sortable);
		}
		setAttributeListSelection(-1);
		setSortingListSelection(this.sortingList.size());
	}
	
	public void removeFromSortingList(List<Sortable> sortables) {
		for(Sortable sortable: sortables) {
			this.sortingList.remove(sortable);
			this.attributeList.add(sortable);
		}
		setSortingListSelection(-1);
		setAttributeListSelection(this.attributeList.size());
		
	}
	
	public void moveInsideSortingList(Sortable sortable, int sourceIndex, int destinationIndex) {
		assert (destinationIndex >= 0 && destinationIndex <= this.sortingList.size()-1);
		if(destinationIndex < 0) {
			destinationIndex = 0;
		}
		if(destinationIndex >= this.sortingList.size()) {
			destinationIndex = this.sortingList.size()-1;
		}
		Sortable swap = this.sortingList.get(destinationIndex);
		this.sortingList.set(destinationIndex, sortable);
		this.sortingList.set(sourceIndex, swap);
		setSortingListSelection(destinationIndex);
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
	
	private ListChangeListener<Sortable> changeListener = new ListChangeListener<Sortable>() {
		public void onChanged(Change<? extends Sortable> c) {
			while(c.next()) {
				if(c.getList().equals(attributeList)) {
					attributeListChangedListener.invoke(attributeList);
				} else if (c.getList().equals(sortingList)) {
					sortingListChangedListener.invoke(sortingList);
				}
				else {
					throw new UnsupportedOperationException("This list is not supported by this class.");
				}
			}
		}
	};
}
	