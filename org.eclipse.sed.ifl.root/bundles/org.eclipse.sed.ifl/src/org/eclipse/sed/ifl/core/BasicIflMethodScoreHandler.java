package org.eclipse.sed.ifl.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sed.ifl.bi.faced.MethodScoreHandler;
import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
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
		Map<IMethodDescription, Defineable<Double>> map = new HashMap<IMethodDescription, Defineable<Double>>();
		if (feedback.getChoise().getId().equals("YES")) {
			// end session
		} else if (feedback.getChoise().getId().equals("NO")) {
			for (IMethodDescription subject : feedback.getSubjects()) {
				if (methodsScoreMap.get(subject).isDefinit()) {
					map.put(subject, new Defineable<>(0.0));
				} else {
//					map.put(subject, null);
				}
			}

		} else if (feedback.getChoise().getId().equals("NO_AND_NOT_SUSPICIOUS")) {

			for (IMethodDescription subject : feedback.getSubjects()) {
				if (methodsScoreMap.get(subject).isDefinit()) {
					map.put(subject, new Defineable<>(0.0));
				} else {
//					map.put(subject, null);
				}

				for (MethodIdentity contextSubject : subject.getContext()) {
					for (IMethodDescription iDesc : methodsScoreMap.keySet()) {
						if (iDesc.getId().equals(contextSubject)) {
							if (methodsScoreMap.get(iDesc).isDefinit()) {
								map.put(iDesc, new Defineable<>(0.0));
							} else {
//							map.put(contextSubject, null);
							}
						}
					}
				}

			}
		} else

		{
			new UnsupportedOperationException("invalid option");
		}

		if (!feedback.getChoise().getId().equals("YES"))
			this.scoreUpdated.invoke(map);
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

	@Override
	public Iterable<Option> getProvidedOptions() {
		List<Option> providedOptions = new ArrayList<Option>();

		providedOptions.add(new Option("YES",
				"This is faulty", "Select this option if the item is faulty.",
				SideEffect.FOUND,
				"icons/item-faulty16.png"));
		providedOptions.add(new Option("NO_AND_NOT_SUSPICIOUS",
				"Neither this, nor its context are faulty",
				"Select this option if neither the selected item, nor its context are faulty",
				"icons/context-notfaulty16.png"));
		providedOptions.add(new Option("NO",
				"This is not faulty",
				"Select this option if the item is not faulty",
				"icons/item-notfaulty16.png"));

		return providedOptions;
	}

}
