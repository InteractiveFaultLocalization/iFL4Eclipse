package org.eclipse.sed.ifl.model.user.interaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
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
		String id, String title, String description, SideEffect sideEffect, String iconPath,
		Function<IMethodDescription, Defineable<Double>> updateSelected,
		Function<IMethodDescription, Defineable<Double>> updateContext,
		Function<IMethodDescription, Defineable<Double>> updateOthers) {
		super(id, title, description, sideEffect, iconPath);
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
		String id, String title, String description, String iconPath,
		Function<IMethodDescription, Defineable<Double>> updateSelected,
		Function<IMethodDescription, Defineable<Double>> updateContext,
		Function<IMethodDescription, Defineable<Double>> updateOthers) {
		super(id, title, description, iconPath);
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
			context = collectContext(feedback, allScores);
			newScores.putAll(applyAll(updateContext, context));
		}
		if (updateOther != null) {
			if (selected == null) {
				selected = feedback.getSubjects();
			}
			if (context == null) {
				context = collectContext(feedback, allScores);
			}
			other = collectOther(allScores, selected, context);
			newScores.putAll(applyAll(updateOther, other));
		}
		return newScores;
	}

	private List<IMethodDescription> collectOther(Map<IMethodDescription, Defineable<Double>> allScores,
			List<IMethodDescription> selected, List<IMethodDescription> context) {
		Set<IMethodDescription> other = new HashSet<>();
		other.addAll(allScores.keySet());
		other.removeAll(selected);
		other.removeAll(context);
		return new ArrayList<>(other);
	}

	private List<IMethodDescription> collectContext(IUserFeedback feedback,
			Map<IMethodDescription, Defineable<Double>> allScores) {
		Set<IMethodDescription> context = new HashSet<>();
		feedback.getSubjects().stream()
			.flatMap(subject -> subject.getContext().stream())
			.forEach(id -> {
				for (IMethodDescription desc : allScores.keySet()) {
					if (desc.getId().equals(id)) {
						context.add(desc);
						break;
					}
				}
			});
		return new ArrayList<>(context);
	}
}
