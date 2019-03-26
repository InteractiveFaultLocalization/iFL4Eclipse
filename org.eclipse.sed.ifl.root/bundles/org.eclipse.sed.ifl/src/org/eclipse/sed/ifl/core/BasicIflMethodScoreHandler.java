package org.eclipse.sed.ifl.core;

import java.util.Arrays;
import java.util.List;
import org.eclipse.sed.ifl.bi.faced.MethodScoreHandler;
import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOption;
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
		if (!feedback.getChoise().getId().equals("YES")) {
			for (Option possibility : options) {
				if (feedback.getChoise().equals(possibility)) {
					this.scoreUpdated.invoke(possibility.apply(feedback, methodsScoreMap));
				}
			}
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

	private List<Option> options = Arrays.asList(
		new Option("SELECTED_FAULTY",
			"Selected items seem faulty",
			"I think that the selected items are faulty, hence I found the bug.",
			SideEffect.FOUND,
			"icons/option_F--.png"),
		new ContextBasedOption("SELECTED_NOT_SUSPICIOUS",
			"Selected items seem not suspicious",
			"Currently, I think that the selected items are not suspicious",
			"icons/option_0--.png",
			item -> new Defineable<Double>(0.0),
			null,
			null),
		new ContextBasedOption("SELECTED_AND_CONTEXT_NOT_SUSPICIOUS",
			"Context (including selected items) seem not suspicious",
			"Currently, I think that the selected items and its context are not suspicious.",
			"icons/option_00-.png",
			item -> new Defineable<Double>(0.0),
			item -> new Defineable<Double>(0.0),
			null),
		new ContextBasedOption("CONTEXT_SUSPICIOUS",
			"Only the context (excluding selected items) seems suspicious",
			"Currently, I think that the selected items are not but only their context are suspicious.",
			"icons/option_0-0.png",
			item -> new Defineable<Double>(0.0),
			null,
			item -> new Defineable<Double>(0.0)),
		new ContextBasedOption("SELECTED_AND_CONTEXT_SUSPICIOUS",
			"Only the context (including selected items) seems suspicious",
			"Currently, I think that only the selected items and their context are suspicious.",
			"icons/option_--0.png",
			null,
			null,
			item -> new Defineable<Double>(0.0)));
	
	@Override
	public Iterable<Option> getProvidedOptions() {
		return options;
	}

}
