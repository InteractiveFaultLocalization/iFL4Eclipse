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
	
	
	public String getDomain() {
		String rString = "";
		switch(this) {
		case Score:
			rString = "Score";
			break;
		case Name:
			rString = "Name";
			break;
		case Signature:
			rString = "Signature";
			break;
		case ParentType:
			rString = "Parent type";
			break;
		case Path:
			rString = "Path";
			break;
		case Position:
			rString = "Position";
			break;
		case ContextSize:
			rString = "Context size";
			break;
		case LastAction:
			rString = "Last action";
			break;
		case Interactivity:
			rString = "Interactivity";
			break;
		}
		return rString;
	}
}
