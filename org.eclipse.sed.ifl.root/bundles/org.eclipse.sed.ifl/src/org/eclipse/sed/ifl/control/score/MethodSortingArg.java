package org.eclipse.sed.ifl.control.score;

public class MethodSortingArg implements Sortable {
	
	private SortingDirection direction;
	
	private String name;
	
	public MethodSortingArg(String name, SortingDirection direction) {
		this.name = name;
		this.direction = direction;
	}
	
	@Override
	public SortingDirection getSortingDirection() {
		return this.direction;
	}

	@Override
	public void setSortingDirection(SortingDirection direction) {
		this.direction = direction;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
