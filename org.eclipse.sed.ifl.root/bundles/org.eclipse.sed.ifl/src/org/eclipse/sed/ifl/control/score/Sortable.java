package org.eclipse.sed.ifl.control.score;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Sortable {

	public StringProperty directionProperty = new SimpleStringProperty("Ascending");
	public StringProperty nameProperty = new SimpleStringProperty("");
	
	public enum SortingDirection {
		Ascending, Descending;
	}
	
	public abstract Sortable.SortingDirection getSortingDirection();
	
	public abstract void setSortingDirection(Sortable.SortingDirection direction);
	
	public abstract String getName();

	public StringProperty nameProperty() {
		return nameProperty;
	}

	public StringProperty directionProperty() {
		return directionProperty;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sortable other = (Sortable) obj;
		if (nameProperty.get() == null) {
			if (other.nameProperty.get() != null)
				return false;
		} else if (!nameProperty.get().equals(other.nameProperty.get()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nameProperty == null) ? 0 : nameProperty.hashCode());
		return result;
	}
	
	
}
