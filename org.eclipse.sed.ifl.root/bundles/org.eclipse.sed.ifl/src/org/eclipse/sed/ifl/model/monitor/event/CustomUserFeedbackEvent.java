package org.eclipse.sed.ifl.model.monitor.event;

import java.util.HashMap;

import org.eclipse.sed.ifl.model.monitor.resource.CodeElement;
import org.eclipse.sed.ifl.model.monitor.resource.Option;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.CustomUserFeedback;

public class CustomUserFeedbackEvent extends Event {

	public CustomUserFeedbackEvent(CustomUserFeedback feedback) {
		super(new HashMap<>());
		resources.put(new Option(feedback.getChoise().getId(), feedback.getChoise().getTitle()), "choise");
		for (IMethodDescription subject : feedback.getSubjects()) {
			resources.put(new CodeElement(subject.getId().getKey()), "subject");
		}
	}

}
