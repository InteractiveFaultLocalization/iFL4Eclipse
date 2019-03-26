package org.eclipse.sed.ifl.model.user.interaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class ContextBasedOption extends Option {

	private Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateSelected;
	private Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateContext;
	private Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateOther;
	
	public ContextBasedOption(
		String id, String title, String description,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateSelected,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateContext,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateOthers) {
		super(id, title, description);
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	public ContextBasedOption(
		String id, String title, String description, SideEffect sideEffect, String iconPath,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateSelected,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateContext,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateOthers) {
		super(id, title, description, sideEffect, iconPath);
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	public ContextBasedOption(
		String id, String title, String description, SideEffect sideEffect,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateSelected,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateContext,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateOthers) {
		super(id, title, description, sideEffect);
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	public ContextBasedOption(
		String id, String title, String description, String iconPath,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateSelected,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateContext,
		Function<List<IMethodDescription>, Map<IMethodDescription, Defineable<Double>>> updateOthers) {
		super(id, title, description, iconPath);
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	public Map<IMethodDescription, Defineable<Double>> apply(IUserFeedback feedback, Map<IMethodDescription, Defineable<Double>> allScores) {
		Map<IMethodDescription, Defineable<Double>> newScores = new HashMap<>();
		List<IMethodDescription> selected = feedback.getSubjects();
		List<IMethodDescription> context = collectContext(feedback, allScores);
		List<IMethodDescription> other = collectOther(allScores, selected, context);
		
		newScores.putAll(updateSelected.apply(selected));
		newScores.putAll(updateContext.apply(context));
		newScores.putAll(updateOther.apply(other));
		
		return newScores;
	}

	private List<IMethodDescription> collectOther(Map<IMethodDescription, Defineable<Double>> allScores,
			List<IMethodDescription> selected, List<IMethodDescription> context) {
		List<IMethodDescription> other = new ArrayList<>();
		outer: for (IMethodDescription desc : allScores.keySet()) {
			List<IMethodDescription> union = new ArrayList<>(selected);
			union.addAll(context);
			for (IMethodDescription method : union) {
				if (method.equals(desc)) {
					continue outer;
				}
			}
			other.add(desc);
		}
		return other;
	}

	private List<IMethodDescription> collectContext(IUserFeedback feedback,
			Map<IMethodDescription, Defineable<Double>> allScores) {
		List<IMethodDescription> context = new ArrayList<>();
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
		return context;
	}
}
