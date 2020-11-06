package org.eclipse.sed.ifl.model.monitor.event;

import java.util.HashMap;
import java.util.List;

import org.eclipse.sed.ifl.model.monitor.resource.CodeElement;

import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class SelectionChangedEvent extends Event {

	public SelectionChangedEvent(List<IMethodDescription> items) {
		super(new HashMap<>());
		for (IMethodDescription item : items) {
			resources.put(new CodeElement(item.getId().getKey()), "new-selection");
		}
	}

}
