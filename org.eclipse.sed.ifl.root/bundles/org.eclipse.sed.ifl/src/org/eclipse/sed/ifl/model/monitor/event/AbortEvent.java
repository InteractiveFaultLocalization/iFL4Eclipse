package org.eclipse.sed.ifl.model.monitor.event;

import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;

public class AbortEvent extends UserFeedbackEvent {

	public AbortEvent(IUserFeedback feedback) {
		super(feedback);
	}

}
