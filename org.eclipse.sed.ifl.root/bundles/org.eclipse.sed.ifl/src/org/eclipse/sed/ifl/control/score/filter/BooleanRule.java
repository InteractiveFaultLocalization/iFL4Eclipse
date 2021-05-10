package org.eclipse.sed.ifl.control.score.filter;

public class BooleanRule implements Rule {

	private String domain;
	private boolean value;
	private boolean negated = false;
	
	public boolean isValue() {
		return value;
	}

	public BooleanRule(String domain, boolean value) {
		super();
		this.domain = domain;
		this.value = value;
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public void setNegated(boolean value) {
		this.negated = value;
	}

	@Override
	public boolean isNegated() {
		return this.negated;
	}

}
