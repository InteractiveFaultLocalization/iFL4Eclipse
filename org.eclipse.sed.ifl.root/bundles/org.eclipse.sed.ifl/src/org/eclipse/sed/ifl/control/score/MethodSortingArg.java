package org.eclipse.sed.ifl.control.score;

import javafx.beans.property.SimpleStringProperty;

public class MethodSortingArg extends Sortable {
	
	private SortingDirection direction;
	
	public MethodSortingArg(String name, SortingDirection direction) {
		this.direction = direction;
		this.nameProperty = new SimpleStringProperty(name);
		this.directionProperty = new SimpleStringProperty(direction.name());
	}
	
	@Override
	public SortingDirection getSortingDirection() {
		return this.direction;
	}

	@Override
	public void setSortingDirection(SortingDirection direction) {
		this.direction = direction;
		this.directionProperty.set(direction.name());
	}

	@Override
	public String getName() {
		return this.nameProperty.get();
	}

}
