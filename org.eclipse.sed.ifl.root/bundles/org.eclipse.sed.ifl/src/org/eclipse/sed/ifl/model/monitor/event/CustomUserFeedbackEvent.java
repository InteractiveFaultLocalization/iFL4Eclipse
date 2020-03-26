package org.eclipse.sed.ifl.model.monitor.event;


import java.util.List;

import org.eclipse.sed.ifl.model.monitor.resource.CodeElement;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.CustomUserFeedback;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.sed.ifl.util.wrapper.CustomValue;

public class CustomUserFeedbackEvent extends Event {

	public CustomUserFeedbackEvent(CustomUserFeedback feedback, CustomValue customValue) {
		super(Maps.<String, Object>builder()
				.put("selection", selection)
				.put("isAbsolute", feedback.isAbsoluteValue() ? "yes" : "no")
				.put("value", feedback.isAbsoluteValue() ? feedback.getAbsoluteValue() : "none")
				.unmodifiable(true).build());
		for (IMethodDescription subject : feedback.getSubjects()) {
			resources.put(new CodeElement(subject.getId().getKey()), "subject");
		}
	}

}
