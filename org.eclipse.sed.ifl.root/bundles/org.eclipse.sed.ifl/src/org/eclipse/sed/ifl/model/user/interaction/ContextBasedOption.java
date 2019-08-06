package org.eclipse.sed.ifl.model.user.interaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.items.IMethodDescriptionCollectionUtil;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class ContextBasedOption extends Option {

	private Function<IMethodDescription, Defineable<Double>> updateSelected;
	private Function<IMethodDescription, Defineable<Double>> updateContext;
	private Function<IMethodDescription, Defineable<Double>> updateOther;
	
	public ContextBasedOption(
		String id, String title, String description,
		Function<IMethodDescription, Defineable<Double>> updateSelected,
		Function<IMethodDescription, Defineable<Double>> updateContext,
		Function<IMethodDescription, Defineable<Double>> updateOthers) {
		super(id, title, description);
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	public ContextBasedOption(
		String id, String title, String description, SideEffect sideEffect, OptionKind kind,
		Function<IMethodDescription, Defineable<Double>> updateSelected,
		Function<IMethodDescription, Defineable<Double>> updateContext,
		Function<IMethodDescription, Defineable<Double>> updateOthers) {
		super(id, title, description, sideEffect, kind);
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	public ContextBasedOption(
		String id, String title, String description, SideEffect sideEffect,
		Function<IMethodDescription, Defineable<Double>> updateSelected,
		Function<IMethodDescription, Defineable<Double>> updateContext,
		Function<IMethodDescription, Defineable<Double>> updateOthers) {
		super(id, title, description, sideEffect);
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	public ContextBasedOption(
		String id, String title, String description, OptionKind kind,
		Function<IMethodDescription, Defineable<Double>> updateSelected,
		Function<IMethodDescription, Defineable<Double>> updateContext,
		Function<IMethodDescription, Defineable<Double>> updateOthers) {
		super(id, title, description, kind);
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	private Map<IMethodDescription, Defineable<Double>> applyAll(Function<IMethodDescription, Defineable<Double>> function, List<IMethodDescription> items) {
		Map<IMethodDescription, Defineable<Double>> result = new HashMap<>();
		for (IMethodDescription item : items) {
			result.put(item, function.apply(item));
		}
		return result;
	}
	
	@Override
	public Map<IMethodDescription, Defineable<Double>> apply(IUserFeedback feedback, Map<IMethodDescription, Defineable<Double>> allScores) {
		Map<IMethodDescription, Defineable<Double>> newScores = new HashMap<>();
		List<IMethodDescription> selected = null;
		List<IMethodDescription> context = null;
		List<IMethodDescription> other = null;
		if (updateSelected != null) {
			selected = feedback.getSubjects();
			newScores.putAll(applyAll(updateSelected, selected));
		}
		if (updateContext != null) {
			context = IMethodDescriptionCollectionUtil.collectContext(feedback.getSubjects(), allScores);
			newScores.putAll(applyAll(updateContext, context));
		}
		if (updateOther != null) {
			if (selected == null) {
				selected = feedback.getSubjects();
			}
			if (context == null) {
				context = IMethodDescriptionCollectionUtil.collectContext(feedback.getSubjects(), allScores);
			}
			other = IMethodDescriptionCollectionUtil.collectOther(allScores, selected, context);
			newScores.putAll(applyAll(updateOther, other));
		}
		return newScores;
	}
}
