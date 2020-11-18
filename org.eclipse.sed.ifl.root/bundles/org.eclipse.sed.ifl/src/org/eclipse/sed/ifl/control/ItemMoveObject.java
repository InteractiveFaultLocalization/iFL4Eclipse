package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.control.score.Sortable;

public class ItemMoveObject<TItem extends Sortable> {

	private Sortable element;
	private int sourceIndex; // ha -1: listán belül mozgatunk, -2: töröljük az adott elemet.
	private int destinationIndex; // ha ez is -1: a lista összes elemét mozgatjuk/törüljük

	public ItemMoveObject(Sortable element, int sourceIndex, int destinationIndex) {
		this.element = element;
		this.sourceIndex = sourceIndex;
		this.destinationIndex = destinationIndex;
	}

	public Sortable getItem() {
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
