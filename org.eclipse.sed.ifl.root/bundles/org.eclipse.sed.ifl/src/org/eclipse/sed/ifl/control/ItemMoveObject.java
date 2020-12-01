package org.eclipse.sed.ifl.control;


import org.eclipse.sed.ifl.control.score.Sortable;

public class ItemMoveObject<TItem extends Sortable> {
	
	

	private Sortable element;
	private int sourceIndex;
	private int destinationIndex; 
	private OperationType operationType;

	public ItemMoveObject(Sortable element, int sourceIndex, int destinationIndex, OperationType operationType) {
		this.element = element;
		this.sourceIndex = sourceIndex;
		this.destinationIndex = destinationIndex;
		this.operationType = operationType;
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
	
	public OperationType getOperationType() {
		return operationType;
	}
}
