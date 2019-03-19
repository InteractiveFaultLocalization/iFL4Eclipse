package org.eclipse.sed.ifl.control.score;

public enum SortingArg {
	Score, Name, Signature, ParentType, Path, Position, ContextSize;
	
	private boolean descending;
	
	public Boolean isDescending() {
		return descending;
	}
	
	public void setDescending(Boolean value) {
		descending = value;
	}
}
