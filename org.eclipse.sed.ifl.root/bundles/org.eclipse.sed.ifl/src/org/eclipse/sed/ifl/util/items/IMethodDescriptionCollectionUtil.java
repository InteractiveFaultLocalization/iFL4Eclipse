package org.eclipse.sed.ifl.util.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.sed.ifl.util.wrapper.Defineable;

import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;


public class IMethodDescriptionCollectionUtil {
	
	private static List<IMethodDescription> nonInteractiveContextList;
	
	public static List<IMethodDescription> getNonInteractiveContext(){
		return nonInteractiveContextList;
	}
	
	public static List<IMethodDescription> collectContext(
			List<IMethodDescription> subjects,
			Map<IMethodDescription, Defineable<Double>> allScores) {
		//nonInteractiveContextList.clear();
		Set<IMethodDescription> context = new HashSet<>();
		subjects.stream()
		.flatMap(subject -> subject.getContext().stream())
		.forEach(id -> {
			for (IMethodDescription desc : allScores.keySet()) {
				if (desc.getId().equals(id)) {
					/*if(!desc.isInteractive()) {
						nonInteractiveContextList.add(desc);
					} else {
					*/
						context.add(desc);
						break;
					}
				}
			
		});
	/*if(!nonInteractiveContextList.isEmpty()) {
		boolean highLightRequest = MessageDialog.open(
				MessageDialog.QUESTION, null,
				"Non-interactive methods removed",
				"Your selection or the context of it contains non-interactive elements. "
				+ "The score of non-interactive elements will not be affected by your feedback. "
				+ "Would you like to highlight the affected non-interactive methods?"
				, SWT.NONE);
		if (!highLightRequest) {
			nonInteractiveContextList.clear();
		}
		*/
		for(IMethodDescription method : subjects) {
			context.remove(method);
		}
	
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
		for(Entry<IMethodDescription,Defineable<Double>> entry : subjects.entrySet()) {
			contextMap.remove(entry.getKey());
		}
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
