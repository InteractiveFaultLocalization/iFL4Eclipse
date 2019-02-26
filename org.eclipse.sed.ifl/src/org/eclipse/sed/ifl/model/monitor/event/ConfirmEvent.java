package org.eclipse.sed.ifl.model.monitor.event;

import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;

public class ConfirmEvent extends UserFeedbackEvent {

	public ConfirmEvent(IUserFeedback feedback) {
		super(feedback);
	}

}
