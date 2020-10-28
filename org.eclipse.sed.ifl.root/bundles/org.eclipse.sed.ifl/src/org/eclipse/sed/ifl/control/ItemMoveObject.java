package org.eclipse.sed.ifl.control;

import java.util.ArrayList;
import org.eclipse.sed.ifl.ide.gui.element.DualListElement;

public class ItemMoveObject<TItem> {

	private ArrayList<DualListElement<TItem>> sourceList;
	private ArrayList<DualListElement<TItem>> destinationList;
	private DualListElement<TItem> element;
	private int sourceIndex;
	private int destinationIndex;

	public ItemMoveObject(ArrayList<DualListElement<TItem>> sourceList, ArrayList<DualListElement<TItem>> destinationList, DualListElement<TItem> element, int sourceIndex,
			int destinationIndex) {
		this.sourceList = sourceList;
		this.destinationList = destinationList;
		this.element = element;
		this.sourceIndex = sourceIndex;
		this.destinationIndex = destinationIndex;
	}

	public ArrayList<DualListElement<TItem>> getSourceList() {
		return sourceList;
	}

	public ArrayList<DualListElement<TItem>> getDestinationList() {
		return destinationList;
	}

	public DualListElement<TItem> getItem() {
		return element;
	}

	public int getSourceIndex() {
		return sourceIndex;
	}

	public int getDestinationIndex() {
		return destinationIndex;
	}
	
	public void setDestinationIndex(int destinationIndex) {
		this.destinationIndex = destinationIndex;
	}

}
