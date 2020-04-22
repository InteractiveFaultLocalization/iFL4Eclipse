package org.eclipse.sed.ifl.control.score;

public enum SortingArg {
	Score, Name, Signature, ParentType, Path, Position, ContextSize, LastAction, Interactivity;
	
	private boolean descending;
	
	public Boolean isDescending() {
		return descending;
	}
	
	public void setDescending(Boolean value) {
		descending = value;
	}
}
