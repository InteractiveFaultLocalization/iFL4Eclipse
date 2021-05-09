package org.eclipse.sed.ifl.commons.model.source;

public class Line {
	
	private long sourceFileLineNumber;
	private IMethodDescription method;
	
	
	protected Line(long sourceFileLineNumber) {
		super();
		this.sourceFileLineNumber = sourceFileLineNumber;
	}
	
	protected Line(long sourceFileLineNumber, IMethodDescription method) {
		super();
		this.sourceFileLineNumber = sourceFileLineNumber;
		this.method = method;
	}

	public long getSourceFileLineNumber() {
		return sourceFileLineNumber;
	}
	protected void setSourceFileLineNumber(long sourceFileLineNumber) {
		this.sourceFileLineNumber = sourceFileLineNumber;
	}
	public IMethodDescription getMethod() {
		return method;
	}
	protected void setMethod(IMethodDescription method) {
		this.method = method;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + (int) (sourceFileLineNumber ^ (sourceFileLineNumber >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Line other = (Line) obj;
		if (sourceFileLineNumber != other.sourceFileLineNumber)
			return false;
		return true;
	}
}
