package org.eclipse.sed.ifl.core;

import java.util.Arrays;
import java.util.List;

import org.eclipse.sed.ifl.bi.faced.MethodScoreHandler;
import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
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
		this.scoreUpdated.invoke(new ScoreUpdateArgs(feedback.getChoise().apply(feedback, methodsScoreMap), feedback));
	}
	
	protected NonGenericListenerCollection<List<IMethodDescription>> highLightRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<IMethodDescription>> eventHighLightRequested() {
		return highLightRequested;
	
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
			OptionKind.CONTEXT_FXX),
		new Option("CONTEXT_BASED_OPTION",
			"Context based feedback",
			"Individually change the scores of selected, context and other items.",
			OptionKind.CONTEXT_BASED)
		);
	
	@Override
	public Iterable<Option> getProvidedOptions() {
		return options;
	}

}
