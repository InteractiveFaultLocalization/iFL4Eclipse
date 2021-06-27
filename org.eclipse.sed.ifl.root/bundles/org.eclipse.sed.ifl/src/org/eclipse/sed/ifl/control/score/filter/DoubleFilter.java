package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.Score;

public class DoubleFilter extends ScoreFilter {

	private DoubleRule rule;

	public DoubleFilter(Boolean enabled, DoubleRule rule) {
		super(enabled);
		
		this.rule = rule;
		this.domain = rule.getDomain();
		this.limit = rule.getValue();
		this.relation = rule.getRelation();
	}

	private String domain;
	
	private Double limit = 0.0;
	
	private String relation = ">=";
	
	public void setLimit(Double value) {
		limit = value;
	}
	
	public void setRelation(String text) {
		relation = text;
	}
	
	@Override
	protected boolean check(Entry<IMethodDescription, Score> arg0) {
		boolean rValue = true;
		
		double target = 0.0;
		
		switch(this.domain) {
		case "Score":
			if(!arg0.getValue().isDefinit()) {
				return true;
			}
			target = arg0.getValue().getValue();
			break;
		case "Position":
			target = arg0.getKey().getLocation().getBegining().getOffset();
			break;
		case "Context size":
			target = arg0.getKey().getContext().size();
			break;
		}
		
		switch (relation) {
		case ">":
			rValue =  target > limit;
			break;
		case ">=":
			rValue =  target >= limit;
			break;
		case "=":
			rValue =  target == limit;
			break;
		case "<=":
			rValue =  target <= limit;
			break;
		case "<":
			rValue =  target < limit;
			break;
		}
		
		if(rule.isNegated()) {
			rValue = !rValue;
		}
		
		return rValue;
	}

	public Rule getRule() {
		return this.rule;
	}
}
