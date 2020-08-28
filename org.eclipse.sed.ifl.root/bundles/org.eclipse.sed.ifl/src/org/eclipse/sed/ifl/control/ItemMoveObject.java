package org.eclipse.sed.ifl.control;

import java.util.ArrayList;

public class ItemMoveObject<TItem> {

	private ArrayList<TItem> sourceList;
	private ArrayList<TItem> destinationList;
	private TItem item;
	private int sourceIndex;
	private int destinationIndex;

	public ItemMoveObject(ArrayList<TItem> sourceList, ArrayList<TItem> destinationList, TItem item, int sourceIndex,
			int destinationIndex) {
		this.sourceList = sourceList;
		this.destinationList = destinationList;
		this.item = item;
		this.sourceIndex = sourceIndex;
		this.destinationIndex = destinationIndex;
	}

	public ArrayList<TItem> getSourceList() {
		return sourceList;
	}

	public ArrayList<TItem> getDestinationList() {
		return destinationList;
	}

	public TItem getItem() {
		return item;
	}

	public int getSourceIndex() {
		return sourceIndex;
	}

	public int getDestinationIndex() {
		return destinationIndex;
	}
	
	

}
