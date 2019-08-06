package org.eclipse.sed.ifl.util.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class IMethodDescriptionCollectionUtil {
	public static List<IMethodDescription> collectContext(
			List<IMethodDescription> subjects,
			Map<IMethodDescription, Defineable<Double>> allScores) {
		Set<IMethodDescription> context = new HashSet<>();
		subjects.stream()
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
	
	public static List<IMethodDescription> collectOther(Map<IMethodDescription, Defineable<Double>> allScores,
			List<IMethodDescription> selected, List<IMethodDescription> context) {
		Set<IMethodDescription> other = new HashSet<>();
		other.addAll(allScores.keySet());
		other.removeAll(selected);
		other.removeAll(context);
		return new ArrayList<>(other);
	}

}
