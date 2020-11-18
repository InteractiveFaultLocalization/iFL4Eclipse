package org.eclipse.sed.ifl.control.score;

public interface Sortable {
	
	enum SortingDirection {
		Ascending, Descending;
	}
	
	public Sortable.SortingDirection getSortingDirection();
	
	public void setSortingDirection(Sortable.SortingDirection direction);
	
	public String getName();
}
