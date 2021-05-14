package org.eclipse.sed.ifl.control.score.filter;

public class StringRule implements Rule {

	private String domain;
	private String value;
	private boolean matches;
	private boolean caseSensitive;
	private boolean regex;
	private boolean negated = false;
	
	public StringRule(String domain, String value, boolean matches, boolean caseSensitive, boolean regex) {
		this.domain = domain;
		this.value = value;
		this.matches = matches;
		this.caseSensitive = caseSensitive;
		this.regex = regex;
	}

	public boolean isMatches() {
		return matches;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isRegex() {
		return regex;
	}

	public String getValue() {
		return value;
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
