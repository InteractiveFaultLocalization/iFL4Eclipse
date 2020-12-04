package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.control.score.Sortable;

public class ItemMoveObject<TItem extends Sortable> {

	private int sourceIndex;
	private int destinationIndex;
	private OperationType operationType;

	public ItemMoveObject(int sourceIndex, int destinationIndex, OperationType operationType) {
		this.sourceIndex = sourceIndex;
		this.destinationIndex = destinationIndex;
		this.operationType = operationType;
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

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}
}
