package org.eclipse.sed.ifl.model.source;

public interface IMethodDescription {
	public MethodIdentity getId();
	
	public ICodeChunkLocation getLocation();
	
	public Iterable<IMethodDescription> getContext();
}
