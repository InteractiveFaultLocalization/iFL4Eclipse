package org.eclipse.sed.ifl.model.user.interaction;


import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class CustomUserFeedback extends UserFeedback {

	private Map<IMethodDescription, Defineable<Double>> subjectMap;
	
	public CustomUserFeedback(Option option, Map<IMethodDescription, Defineable<Double>> subjects) {
		super(option, subjects.keySet().stream()
				.collect(Collectors.toList()));
		this.subjectMap = subjects;
	}

	public Map<IMethodDescription, Defineable<Double>> getSubjectMap() {
		return this.subjectMap;
	}
}
