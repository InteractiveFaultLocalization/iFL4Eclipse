package org.eclipse.sed.ifl.model.user.interaction;

public enum SideEffect {
	FOUND(true), NOTHING(false);
	
	private boolean successful = false;
	
	private SideEffect(boolean successful) {
		this.successful = successful;
	}
	
	public boolean isSuccessFul() {
		return successful;
	}
}
