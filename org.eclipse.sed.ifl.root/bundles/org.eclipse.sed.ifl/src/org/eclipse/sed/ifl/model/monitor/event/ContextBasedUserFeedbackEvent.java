package org.eclipse.sed.ifl.model.monitor.event;


import java.util.Map;

import org.eclipse.sed.ifl.model.monitor.resource.CodeElement;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.sed.ifl.util.wrapper.CustomValue;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class ContextBasedUserFeedbackEvent extends Event {

	public ContextBasedUserFeedbackEvent(Map<IMethodDescription, Defineable<Double>> subjects, CustomValue customValue, String selection) {
		super(Maps.<String, Object>builder()
				.put("selection", selection)
				.put("isAbsolute", customValue.isAbsolute() ? "yes" : "no")
				.put("value", customValue.getValue())
				.unmodifiable(true).build());
		for (IMethodDescription subject : subjects.keySet()) {
			resources.put(new CodeElement(subject.getId().getKey()), "subject");
		}
	}

}
