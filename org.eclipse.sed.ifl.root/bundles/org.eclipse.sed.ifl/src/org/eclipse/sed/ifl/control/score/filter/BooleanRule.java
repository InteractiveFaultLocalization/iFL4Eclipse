package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map;

public class BooleanRule implements Rule {

	private String domain;
	private boolean value;
	
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

}
