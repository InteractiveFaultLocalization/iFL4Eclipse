package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

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
		boolean isScore = false;
		
		double target = 0.0;
		
		switch(this.domain) {
		case "Score":
			target = arg0.getValue().getValue();
			isScore = true;
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
		
		if(isScore) {
			rValue = rValue || !arg0.getValue().isDefinit();
		}
		
		
		return rValue;
	}

	public Rule getRule() {
		return this.rule;
	}
}
