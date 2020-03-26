package org.eclipse.sed.ifl.util.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	
	public static Map<IMethodDescription, Defineable<Double>> collectContext(
			Map<IMethodDescription, Defineable<Double>> subjects,
			Map<IMethodDescription, Defineable<Double>> allScores) {
		Map<IMethodDescription, Defineable<Double>> contextMap = new HashMap<IMethodDescription,Defineable<Double>>();
		subjects.keySet().stream()
		.flatMap(subject -> subject.getContext().stream())
		.forEach(id -> {
			for (Entry<IMethodDescription,Defineable<Double>> entry : allScores.entrySet()) {
				if (entry.getKey().getId().equals(id)) {
					contextMap.put(entry.getKey(), entry.getValue());
					break;
				}
			}
		});
		return contextMap;
	}
	
	
	public static Map<IMethodDescription, Defineable<Double>> collectOther(
			Map<IMethodDescription, Defineable<Double>> allScores,
			Map<IMethodDescription, Defineable<Double>> selected,
			Map<IMethodDescription, Defineable<Double>> context) {
		Map<IMethodDescription, Defineable<Double>> otherMap = new HashMap<IMethodDescription,Defineable<Double>>();
		otherMap.putAll(allScores);
		otherMap.entrySet().removeAll(selected.entrySet());
		otherMap.entrySet().removeAll(context.entrySet());
		return otherMap;
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
