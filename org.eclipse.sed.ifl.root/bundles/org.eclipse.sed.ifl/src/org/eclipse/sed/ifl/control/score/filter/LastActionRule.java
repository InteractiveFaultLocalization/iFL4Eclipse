package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map;

import org.eclipse.sed.ifl.ide.gui.icon.ScoreStatus;

public class LastActionRule implements Rule {

	private String domain;
	private ScoreStatus status;

	public LastActionRule(String domain, ScoreStatus status) {
		this.domain = domain;
		this.status = status;
	}

	public ScoreStatus getStatus() {
		return status;
	}

	@Override
	public String getDomain() {
		return domain;
	}

}
