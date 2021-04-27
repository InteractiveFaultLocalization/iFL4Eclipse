package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.Score;

public class NameFilter extends ScoreFilter {

	public NameFilter(Boolean enabled) {
		super(enabled);
	}

	private String name = "";
	
	public void setName(String text) {
		name = text;
	}
	
	@Override
	protected boolean check(Entry<IMethodDescription, Score> arg0) {
		if (name == "") {
			return true;
		}
		return (arg0.getKey().getId().getName()).contains(name);
	}

}
