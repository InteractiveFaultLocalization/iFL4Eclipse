package org.eclipse.sed.ifl.model.source;

public class Position {
	private int row;
	private int column;
		
	public Position(int row, int column) {
		super();
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}	
}
