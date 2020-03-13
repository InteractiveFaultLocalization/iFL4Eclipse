package org.eclipse.sed.ifl.model.monitor.event;

import java.util.HashMap;

import org.eclipse.sed.ifl.model.user.interaction.CustomUserFeedback;

public class CustomUserFeedbackEvent extends Event {

	public CustomUserFeedbackEvent(CustomUserFeedback feedback) {
		super(new HashMap<>());
		
	}

}
