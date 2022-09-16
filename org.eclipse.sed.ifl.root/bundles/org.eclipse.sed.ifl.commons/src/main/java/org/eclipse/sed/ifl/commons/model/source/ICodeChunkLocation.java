package org.eclipse.sed.ifl.commons.model.source;

public interface ICodeChunkLocation {
	public String getAbsolutePath();
	
	public Position getBegining();
	
	public Position getEnd();
}
