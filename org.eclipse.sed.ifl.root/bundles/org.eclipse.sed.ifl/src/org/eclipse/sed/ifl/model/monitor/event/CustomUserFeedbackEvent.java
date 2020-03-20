package org.eclipse.sed.ifl.model.monitor.event;


import java.util.List;

import org.eclipse.sed.ifl.model.monitor.resource.CodeElement;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.sed.ifl.util.wrapper.CustomValue;

public class CustomUserFeedbackEvent extends Event {

	public CustomUserFeedbackEvent(List<IMethodDescription> subjects, CustomValue feedback, String selection) {
		super(Maps.<String, Object>builder()
				.put("selection", selection)
				.put("isAbsolute", feedback.isAbsoluteValue() ? "yes" : "no")
				.put("absoluteValue", feedback.isAbsoluteValue() ? feedback.getAbsoluteValue() : "none")
				.put("scaleValue", feedback.isAbsoluteValue() ? "absolute value" : feedback.getScaleValue())
				.unmodifiable(true).build());
		for (IMethodDescription subject : subjects) {
			resources.put(new CodeElement(subject.getId().getKey()), "subject");
		}
	}

}
