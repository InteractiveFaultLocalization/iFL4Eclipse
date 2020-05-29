package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public class StringFilter extends ScoreFilter {
	
	private StringRule rule;

	public StringFilter(Boolean enabled, StringRule rule) {
		super(enabled);
		this.rule = rule;
		domain = rule.getDomain();
		text = rule.getValue();
		contains = rule.isContains();
		exactMatches = rule.isMatches();
		isCaseSensitive = rule.isCaseSensitive();
		isRegex = rule.isRegex();
	}

	private String domain;
	private String text;
	private boolean contains;
	private boolean exactMatches;
	private boolean isCaseSensitive;
	private boolean isRegex;
	
	public void setName(String text) {
		this.text = text;
	}
	
	@Override
	protected boolean check(Entry<IMethodDescription, Score> arg0) {
		
		String target = null;
		
		switch (this.domain) {
		case "Name": target = arg0.getKey().getId().getName();
			break;
		case "Signature": target = arg0.getKey().getId().getSignature();
			break;
		case "Parent type": target = arg0.getKey().getId().getParentType();
			break;
		case "Path": target = arg0.getKey().getLocation().getAbsolutePath();
			break;
		}
		
		boolean containsCheck = contains ? target.contains(text) : !target.contains(text);
		boolean exactMatchCheck = exactMatches ? target.contentEquals(text) : containsCheck;
		boolean caseSensitiveCheck = isCaseSensitive ? target.equals(text) : containsCheck;
		boolean regexCheck = true;
		
		if(isRegex) {
			Pattern pattern = Pattern.compile(text);
			Matcher regexMatcher = pattern.matcher(target);
			regexCheck = regexMatcher.matches();
		}
		
		System.out.println(arg0.getKey().getId().getSignature());
		System.out.println("target: " + target + ", contains: " + containsCheck + ", exact match: " + exactMatchCheck 
				+ ", case sens: " + caseSensitiveCheck + ", regex: " + regexCheck +", resulting in: " + (containsCheck && exactMatchCheck && caseSensitiveCheck && regexCheck));
		
		return containsCheck && exactMatchCheck && caseSensitiveCheck && regexCheck;
	}

	public Rule getRule() {
		return this.rule;
	}
}
