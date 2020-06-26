package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public class BooleanFilter extends ScoreFilter {

	private String domain;
	private boolean value;
	private BooleanRule rule;
	
	public BooleanFilter(Boolean enabled, BooleanRule rule) {
		super(enabled);
		this.rule = rule;
		this.domain = rule.getDomain();
		this.value = rule.isValue();
	}

	@Override
	protected boolean check(Entry<IMethodDescription, Score> arg0) {
		boolean rValue = false;
		
		rValue = arg0.getKey().isInteractive();
		
		return value ? rValue : !rValue;
	}

	public Rule getRule() {
		return this.rule;
	}
}
