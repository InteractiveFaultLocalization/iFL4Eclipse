package org.eclipse.sed.ifl.model.user.interaction;


import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class CustomUserFeedback extends UserFeedback {

	private Map<IMethodDescription, Defineable<Double>> customFeedback;
	
	public void setCustomFeedback(Map<IMethodDescription, Defineable<Double>> customFeedback) {
		this.customFeedback = customFeedback;
	}

	public CustomUserFeedback(Option option, Map<IMethodDescription, Defineable<Double>> subjects) {
		super(option, subjects.keySet().stream()
				.collect(Collectors.toList()));
		this.customFeedback = subjects;
	}

	public Map<IMethodDescription, Defineable<Double>> getCustomFeedback(){
		return this.customFeedback;
	}
	
}
