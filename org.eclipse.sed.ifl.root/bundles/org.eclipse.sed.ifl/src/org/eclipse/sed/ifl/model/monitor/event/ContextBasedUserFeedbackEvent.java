package org.eclipse.sed.ifl.model.monitor.event;


import java.util.Map;

import org.eclipse.sed.ifl.model.monitor.resource.CodeElement;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.sed.ifl.util.wrapper.CustomValue;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.Relativeable;

public class ContextBasedUserFeedbackEvent extends Event {
//TODO subject lehetne egy másik map újMap<String,régiMap<>>
	//resources put-nál a subject string lenne a selection
	public ContextBasedUserFeedbackEvent(Map<IMethodDescription, Defineable<Double>> subjects, Relativeable<Defineable<Double>> relativeableValue, String selection) {
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
