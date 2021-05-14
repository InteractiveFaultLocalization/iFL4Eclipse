package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class StringFilter extends ScoreFilter {
	
	private StringRule rule;

	public StringFilter(Boolean enabled, StringRule rule) {
		super(enabled);
		this.rule = rule;
		domain = rule.getDomain();
		text = rule.getValue();
		exactMatches = rule.isMatches();
		isCaseSensitive = rule.isCaseSensitive();
		isRegex = rule.isRegex();
	}

	private String domain;
	private String text;
	private boolean exactMatches;
	private boolean isCaseSensitive;
	private boolean isRegex;
	
	public void setName(String text) {
		this.text = text;
	}
	
	@Override
	protected boolean check(Entry<IMethodDescription, Score> arg0) {
		
		boolean rValue = false;
		
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
		
		boolean matchingCheck = false;
		
		if(!isCaseSensitive) {
			String lowerTarget = target.toLowerCase();
			String lowerText = text.toLowerCase();
			
			if(!exactMatches) {
				matchingCheck = lowerTarget.contains(lowerText);
			} else {
				matchingCheck = lowerTarget.equals(lowerText);
			}
		} else {
			if (!exactMatches) {
				matchingCheck = target.contains(text);
			} else {
				matchingCheck = target.equals(text);
			}
		}
		
		boolean regexCheck = false;
		
		if(isRegex) {
			matchingCheck = false;
			Pattern pattern = Pattern.compile(text);
			Matcher regexMatcher = pattern.matcher(target);
			regexCheck = regexMatcher.find();
		}
		
		rValue = matchingCheck || regexCheck;
		
		if(rule.isNegated()) {
			rValue = !rValue;
		}
		
		return rValue;
	}

	public Rule getRule() {
		return this.rule;
	}
}
