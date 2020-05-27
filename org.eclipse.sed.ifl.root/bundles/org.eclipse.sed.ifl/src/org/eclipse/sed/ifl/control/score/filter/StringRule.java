package org.eclipse.sed.ifl.control.score.filter;

public class StringRule implements Rule {

	private String domain;
	private boolean contains;
	private boolean matches;
	private boolean caseSensitive;
	private boolean regex;
	
	public StringRule(String domain, boolean contains, boolean matches, boolean caseSensitive, boolean regex) {
		this.domain = domain;
		this.contains = contains;
		this.matches = matches;
		this.caseSensitive = caseSensitive;
		this.regex = regex;
	}

	public boolean isContains() {
		return contains;
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

	@Override
	public String getDomain() {
		return domain;
	}

}
