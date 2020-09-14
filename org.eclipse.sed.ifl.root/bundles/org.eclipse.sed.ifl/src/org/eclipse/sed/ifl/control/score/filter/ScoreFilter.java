package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;
import java.util.function.Predicate;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public abstract class ScoreFilter implements Predicate<Entry<IMethodDescription, Score>> {
	
	public ScoreFilter(Boolean enabled) {
		setEnabled(enabled);
	}
	
	private Boolean enabled;
	
	private boolean ignored=false;

	public void Disable() {
		enabled = false;
	}
	
	public void Enable() {
		enabled = true;
	}
	
	public void setEnabled(boolean value) {
		enabled = value;
	}
	
	public boolean getIgnored() {
		return this.ignored;
	}
	
	public void setIgnored(boolean value) {
		ignored = value;
	}
	
	@Override
	public final boolean test(Entry<IMethodDescription, Score> arg0) {
		if (enabled) {
			return check(arg0);
		} else {
			return true;
		}
	}

	protected abstract boolean check(Entry<IMethodDescription, Score> arg0);

	public abstract Rule getRule();
}
