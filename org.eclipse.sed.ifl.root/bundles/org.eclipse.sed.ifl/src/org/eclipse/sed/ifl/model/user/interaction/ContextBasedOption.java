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

	@Override
	public Map<IMethodDescription, Defineable<Double>> apply(IUserFeedback feedback, Map<IMethodDescription, Defineable<Double>> allScores) {
		Map<IMethodDescription, Defineable<Double>> newScores = new HashMap<>();
		List<IMethodDescription> selected = null;
		List<IMethodDescription> context = null;
		List<IMethodDescription> other = null;
		if (updateSelected != null) {
			selected = feedback.getSubjects();
			newScores.putAll(updateSelected.apply(selected));
		}
		if (updateContext != null) {
			context = collectContext(feedback, allScores);
			newScores.putAll(updateContext.apply(context));
		}
		if (updateOther != null) {
			if (selected == null) {
				selected = feedback.getSubjects();
				newScores.putAll(updateSelected.apply(selected));
			}
			if (context == null) {
				context = collectContext(feedback, allScores);
				newScores.putAll(updateContext.apply(context));
			}
			other = collectOther(allScores, selected, context);
			newScores.putAll(updateOther.apply(other));
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
