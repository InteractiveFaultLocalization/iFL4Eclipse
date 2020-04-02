package org.eclipse.sed.ifl.model.monitor.event;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.model.monitor.resource.CodeElement;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.Relativeable;

public class ScoreModifiedEvent extends Event {
//TODO subject lehetne egy másik map újMap<String,régiMap<>>
	//resources put-nál a subject string lenne a selection
	public ScoreModifiedEvent(Map<Relativeable<Defineable<Double>>, Map<IMethodDescription, Defineable<Double>>> loggingMap) {
		super(propertiesMapCreator(loggingMap));
		for (Entry<Relativeable<Defineable<Double>>, Map<IMethodDescription, Defineable<Double>>> entry : loggingMap.entrySet()) {
			for(IMethodDescription method : entry.getValue().keySet()) {
				resources.put(new CodeElement(method.getId().getKey()), entry.getKey().getSelection());
			}
		}
	}
	
	private static Map<String, Object> propertiesMapCreator(Map<Relativeable<Defineable<Double>>, Map<IMethodDescription, Defineable<Double>>> loggingMap) {
		Map<String, Object> returnMap = new HashMap<>();
		
		for(Relativeable<Defineable<Double>> relativeableValue : loggingMap.keySet()) {
			returnMap.put(relativeableValue.getSelection() + "_value", relativeableValue.getValue().getValue());
			returnMap.put(relativeableValue.getSelection() + "_isRelative", relativeableValue.isRelative() ? "yes" : "no");
		}
		
		return returnMap;
	}
}
