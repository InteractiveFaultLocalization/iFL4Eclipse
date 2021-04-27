package org.eclipse.sed.ifl.commons.model.source;

import java.util.List;
import java.util.Map;

public interface IMethodDescription {
	public MethodIdentity getId();
	
	public ICodeChunkLocation getLocation();
	
	public List<MethodIdentity> getContext();
	
	public void setLines(Map<Long, Score> lines);
	
	public void addLine(long lineNumber, Score score);
	
	public void removeLine(Line line);
}
