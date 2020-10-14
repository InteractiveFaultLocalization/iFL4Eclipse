package org.eclipse.sed.ifl.ide.gui.element;



public class DualListElement<TItem> {
	
	private String name;
	private boolean descending;
	
	public DualListElement(TItem item, boolean descending) {
		this.name = item.toString();
		this.descending = descending;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isDescending() {
		return descending;
	}
	public void setDescending(boolean ascending) {
		this.descending = ascending;
	}
	
	
	
	}


