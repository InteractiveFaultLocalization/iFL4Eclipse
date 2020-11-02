package org.eclipse.sed.ifl.control;

import java.util.ArrayList;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ItemMoveObject<TItem> {

	private Table sourceTable;
	private Table destinationTable;
	private SortingArg element;
	private int sourceIndex; // ha -1: listán belül mozgatunk, -2: töröljük az adott elemet.
	private int destinationIndex; // ha ez is -1: a lista összes elemét mozgatjuk/törüljük
	
	private ArrayList<SortingArg> sourceArray;
	private ArrayList<SortingArg> destinationArray;

	public ItemMoveObject(Table sourceTable, Table destinationTable, SortingArg element, int sourceIndex,
			int destinationIndex) {
		this.sourceTable = sourceTable;
		this.destinationTable = destinationTable;
		this.sourceArray = tableToArray(sourceTable);
		this.destinationArray = tableToArray(destinationTable);
		this.element = element;
		this.sourceIndex = sourceIndex;
		this.destinationIndex = destinationIndex;
	}
	
	private ArrayList<SortingArg>tableToArray(Table table){
		TableItem tableItems[] = table.getItems();
		ArrayList<SortingArg> arrayList = new ArrayList<SortingArg>();
		for(TableItem item : tableItems) {
			SortingArg argument = (SortingArg) item.getData();
			arrayList.add(argument);
		}
		return arrayList;
	}

	public Table getSourceTable() {
		return sourceTable;
	}

	public Table getDestinationTable() {
		return destinationTable;
	}
	
	public ArrayList<SortingArg> getSourceArray() {
		return sourceArray;
	}

	public ArrayList<SortingArg> getDestinationArray() {
		return destinationArray;
	}

	public SortingArg getItem() {
		return element;
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

}
