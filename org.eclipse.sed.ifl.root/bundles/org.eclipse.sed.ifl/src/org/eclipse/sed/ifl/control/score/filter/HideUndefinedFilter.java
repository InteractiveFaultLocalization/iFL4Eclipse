package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class HideUndefinedFilter extends ScoreFilter {

	public HideUndefinedFilter(Boolean enabled) {
		super(enabled);
	}

	@Override
	public boolean check(Entry<IMethodDescription, Score> entry) {
		return entry.getValue().isDefinit();
	}

	@Override
	public Rule getRule() {
		return null;
	}

}
