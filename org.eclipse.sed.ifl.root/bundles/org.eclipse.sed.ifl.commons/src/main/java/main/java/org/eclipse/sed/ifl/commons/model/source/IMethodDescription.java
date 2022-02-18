package org.eclipse.sed.ifl.commons.model.source;

import java.util.List;

public interface IMethodDescription {
	public MethodIdentity getId();
	
	public ICodeChunkLocation getLocation();
	
	public List<MethodIdentity> getContext();
	
	public String getDetailsLink();
	
	public void setDetailsLink(String link);
	
	public boolean hasDetailsLink();
	
	public boolean isInteractive();
	
	public void setInteractivity(boolean interactivity);
}
