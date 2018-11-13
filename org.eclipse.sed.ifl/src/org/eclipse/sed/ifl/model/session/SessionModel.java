package org.eclipse.sed.ifl.model.session;

import org.eclipse.sed.ifl.model.EmptyModel;

public class SessionModel extends EmptyModel {
	private Boolean isSessionActive = false;

	public Boolean getIsSessionActive() {
		return isSessionActive;
	}

	public void setIsSessionActive(Boolean isSessionActive) {
		this.isSessionActive = isSessionActive;
	}
}
