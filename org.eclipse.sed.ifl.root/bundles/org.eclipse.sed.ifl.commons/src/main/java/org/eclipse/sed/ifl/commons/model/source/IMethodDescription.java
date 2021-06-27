package org.eclipse.sed.ifl.commons.model.source;

import java.util.List;
import java.util.Map;

public interface IMethodDescription {
	public MethodIdentity getId();
	
	public ICodeChunkLocation getLocation();
	
	public List<MethodIdentity> getContext();
	
	public String getDetailsLink();
	
	public void setDetailsLink(String link);
	
	public boolean hasDetailsLink();
	
	public boolean isInteractive();
	
	public void setInteractivity(boolean interactivity);
	
	public Map<Line, Score> getLines();
	
	public void addLines(Map<Long, Score> lines);
	
	public void addLine(long lineNumber, Score score);
	
	public void removeLine(long lineNumber);
}
