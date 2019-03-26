package org.eclipse.sed.ifl.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.eclipse.sed.ifl.bi.faced.MethodScoreHandler;
import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class BasicIflMethodScoreHandler extends MethodScoreHandler {

	public BasicIflMethodScoreHandler(IMavenExecutor executor) {
		super(executor);
	}

	@Override
	public void init(Iterable<IMethodDescription> initialMethods) {
		//TODO implement this in a meaningful way 
	}

	@Override
	public void updateScore(IUserFeedback feedback) {
		if (!feedback.getChoise().getId().equals("YES")) {
			this.scoreUpdated.invoke(options.get(feedback.getChoise()).apply(feedback));
		}
	}

	@Override
	public void updateMethod(IMethodDescription method) {
		new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void addMethod(IMethodDescription method) {
		new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void removeMethod(IMethodDescription method) {
		new UnsupportedOperationException("Not implemented yet");
	}

	private Map<Option, Function<IUserFeedback, Map<IMethodDescription, Defineable<Double>>>> options =
		Maps.<Option, Function<IUserFeedback, Map<IMethodDescription, Defineable<Double>>>>builder()
		.put(
			new Option("SELECTED_FAULTY",
				"Selected items seem faulty",
				"I think that the selected items are faulty, hence I found the bug.",
				SideEffect.FOUND,
				"icons/item-faulty16.png"),
			null)
		.put(
			new Option("SELECTED_AND_CONTEXT_SUSPICIOUS",
				"Context (including selected items) seem not suspicious.",
				"Currently, I think that the selected items and its context are not suspicious.",
				"icons/context-notfaulty16.png"),
			feedback -> {
				Map<IMethodDescription, Defineable<Double>> map = new HashMap<IMethodDescription, Defineable<Double>>();
				for (IMethodDescription subject : feedback.getSubjects()) {
					if (methodsScoreMap.get(subject).isDefinit()) {
						map.put(subject, new Defineable<>(0.0));
					}
					for (MethodIdentity contextSubject : subject.getContext()) {
						for (IMethodDescription iDesc : methodsScoreMap.keySet()) {
							if (iDesc.getId().equals(contextSubject)) {
								if (methodsScoreMap.get(iDesc).isDefinit()) {
									map.put(iDesc, new Defineable<>(0.0));
								}
							}
						}
					}
				}
				return map;
			})
		.put(
			new Option("SELECTED_NOT_SUSPICIOUS",
				"Selected items seem not suspicious",
				"Currently, I think that the selected items are not suspicious.",
				"icons/item-notfaulty16.png"),
			feedback -> {
				Map<IMethodDescription, Defineable<Double>> map = new HashMap<IMethodDescription, Defineable<Double>>();
				for (IMethodDescription subject : feedback.getSubjects()) {
					if (methodsScoreMap.get(subject).isDefinit()) {
						map.put(subject, new Defineable<>(0.0));
					}
				}
				return map;
			})
		.put(
			new Option("CONTEXT_SUSPICIOUS",
				"Context (excluding selected items) seem suspicious.",
				"Currently, I am sure that the selected items are not but its context are suspicious.",
				"icons/down32.png"),
			feedback -> {
				Map<IMethodDescription, Defineable<Double>> map = new HashMap<IMethodDescription, Defineable<Double>>();
				List<MethodIdentity> allContext = new ArrayList<>(); 
				for (IMethodDescription subject : feedback.getSubjects()) {
					if (methodsScoreMap.get(subject).isDefinit()) {
						map.put(subject, new Defineable<>(0.0));
					}
					allContext.addAll(subject.getContext());
				}
				for (MethodIdentity contextMethod : allContext) {
					for (Entry<IMethodDescription, Defineable<Double>> entry : methodsScoreMap.entrySet()) {
						if (!entry.getKey().getId().equals(contextMethod)) {
							map.put(entry.getKey(), new Defineable<Double>(0.0));
						}
					}
				}
				return map;
			})
		.build();
	
	@Override
	public Iterable<Option> getProvidedOptions() {
		return options.keySet();
	}

}
