package org.eclipse.sed.ifl.commons.model.source;

public class Line {
	
	private long sourceFileLineNumber;
	private IMethodDescription method;
	
	public Line(long sourceFileLineNumber) {
		super();
		this.sourceFileLineNumber = sourceFileLineNumber;
	}
	
	public long getSourceFileLineNumber() {
		return sourceFileLineNumber;
	}
	public void setSourceFileLineNumber(long sourceFileLineNumber) {
		this.sourceFileLineNumber = sourceFileLineNumber;
	}
	public IMethodDescription getMethod() {
		return method;
	}
	public void setMethod(IMethodDescription method) {
		this.method = method;
	}

}
