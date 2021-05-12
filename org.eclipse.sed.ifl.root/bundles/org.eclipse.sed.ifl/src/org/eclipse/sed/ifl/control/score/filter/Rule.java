package org.eclipse.sed.ifl.control.score.filter;

public interface Rule {
	
	public String getDomain();
	
	public void setNegated(boolean value);
	
	public boolean isNegated();
	
}
