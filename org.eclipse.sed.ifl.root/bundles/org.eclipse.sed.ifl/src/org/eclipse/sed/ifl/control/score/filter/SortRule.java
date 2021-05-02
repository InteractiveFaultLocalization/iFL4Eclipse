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
	
	@Override
	public String getDomain() {
		return this.domain;
	}

}
