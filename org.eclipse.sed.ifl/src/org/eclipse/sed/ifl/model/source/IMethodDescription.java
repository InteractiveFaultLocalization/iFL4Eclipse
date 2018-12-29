package org.eclipse.sed.ifl.model.source;

import java.util.List;

public interface IMethodDescription {
	public MethodIdentity getId();
	
	public ICodeChunkLocation getLocation();
	
	public List<MethodIdentity> getContext();
}
