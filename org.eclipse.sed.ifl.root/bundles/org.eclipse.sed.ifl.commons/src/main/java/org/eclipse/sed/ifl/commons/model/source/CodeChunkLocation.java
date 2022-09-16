package org.eclipse.sed.ifl.commons.model.source;

public class CodeChunkLocation implements ICodeChunkLocation {

	private String absolutePath;
	private Position begining;
	private Position end;
	
	public CodeChunkLocation(String absolutePath, Position begining, Position end) {
		super();
		this.absolutePath = absolutePath;
		this.begining = begining;
		this.end = end;
	}

	@Override
	public String getAbsolutePath() {
		return absolutePath;
	}

	@Override
	public Position getBegining() {
		return begining;
	}

	@Override
	public Position getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "CodeChunkLocation [absolutePath=" + absolutePath + ", begining=" + begining + ", end=" + end + "]";
	}
	
	

}
