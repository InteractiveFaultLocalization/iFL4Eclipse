package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.control.score.Sortable;

public class ItemMoveObject<TItem extends Sortable> {

	private Sortable element;
	private int sourceIndex; // ha -1: list�n bel�l mozgatunk, -2: t�r�lj�k az adott elemet.
	private int destinationIndex; // ha ez is -1: a lista �sszes elem�t mozgatjuk/t�r�lj�k

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
