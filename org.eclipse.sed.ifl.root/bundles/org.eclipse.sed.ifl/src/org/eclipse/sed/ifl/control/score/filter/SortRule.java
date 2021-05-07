package org.eclipse.sed.ifl.control.score.filter;

import org.eclipse.sed.ifl.control.score.SortingArg;

public class SortRule implements Rule {

	public SortRule(String domain, SortingArg arg) {
		super();
		this.domain = domain;
		this.arg = arg;
	}

	public SortingArg getArg() {
		return arg;
	}

	public void setArg(SortingArg arg) {
		this.arg = arg;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	private String domain;
	private SortingArg arg;
	private boolean negated = false;
	
	@Override
	public String getDomain() {
		return this.domain;
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
