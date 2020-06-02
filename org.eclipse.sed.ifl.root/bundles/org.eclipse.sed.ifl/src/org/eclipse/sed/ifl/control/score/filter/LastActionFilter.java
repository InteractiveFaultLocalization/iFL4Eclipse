package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.ScoreHistoryControl;
import org.eclipse.sed.ifl.ide.gui.icon.ScoreStatus;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public class LastActionFilter extends ScoreFilter {

	private String domain;
	private ScoreStatus status;
	private ScoreHistoryControl history;
	private LastActionRule rule;
	
	public LastActionFilter(Boolean enabled, LastActionRule rule, ScoreHistoryControl history) {
		super(enabled);
		this.rule = rule;
		this.domain = rule.getDomain();
		this.status = rule.getStatus();
		this.history = history;
	}

	@Override
	protected boolean check(Entry<IMethodDescription, Score> arg0) {
		
		boolean rValue = true;
		if(history.getLastOf(arg0.getKey()) == null) {
			return false;
		}
		
		switch(this.status) {
		case INCREASED: rValue = history.getLastOf(arg0.getKey()).getChange().equals(ScoreStatus.INCREASED);
			break;
		case DECREASED: rValue = history.getLastOf(arg0.getKey()).getChange().equals(ScoreStatus.DECREASED);
			break;
		case UNDEFINED: rValue = history.getLastOf(arg0.getKey()).getChange().equals(ScoreStatus.UNDEFINED);
			break;
		case NONE: rValue = history.getLastOf(arg0.getKey()).getChange().equals(ScoreStatus.NONE);
			break;
		default:
			break;
		}
		
		return rValue;
	}

	public Rule getRule() {
		return this.rule;
	}
}
