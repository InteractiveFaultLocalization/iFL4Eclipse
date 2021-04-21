package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class LessOrEqualFilter extends ScoreFilter {

	public LessOrEqualFilter(Boolean enabled) {
		super(enabled);
	}

	private Double limit = 0.0;
	
	private String relation = ">=";
	
	public void setRelation(String text) {
		relation = text;
	}
	
	public void setLimit(Double value) {
		limit = value;
	}
	
	@Override
	protected boolean check(Entry<IMethodDescription, Score> arg0) {
		//return !arg0.getValue().isDefinit() || arg0.getValue().getValue() > limit;
		
		boolean rValue = true;
		switch (relation) {
		case ">=":
			rValue = !arg0.getValue().isDefinit() || arg0.getValue().getValue() >= limit;
			break;
		case "<=":
			rValue = !arg0.getValue().isDefinit() || arg0.getValue().getValue() <= limit;
			break;
		}
		return rValue;
	}

}
