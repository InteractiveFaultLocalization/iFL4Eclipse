package org.eclipse.sed.ifl.control.score;

public enum SortingArg {
	Score();
	
	private boolean descending;
	
	public Boolean isDescending() {
		return descending;
	}
	
	public void setDescending(Boolean value) {
		descending = value;
	}
}
