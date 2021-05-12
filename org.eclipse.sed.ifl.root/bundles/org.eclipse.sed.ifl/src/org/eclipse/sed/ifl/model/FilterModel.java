package org.eclipse.sed.ifl.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class FilterModel extends EmptyModel {
	
	public FilterModel() {
	}
	
	public void setFilteredElements(Map<IMethodDescription, Score> elements) {
			for (Map.Entry<IMethodDescription, Score> entry : elements.entrySet()) {
				filteredElements.put(entry.getKey(), entry.getValue());
			}
		}

	private Map<IMethodDescription, Score> filteredElements = new HashMap<>();
	
}
