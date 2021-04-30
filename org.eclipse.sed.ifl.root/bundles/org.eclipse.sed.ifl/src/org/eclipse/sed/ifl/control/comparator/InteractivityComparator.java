package org.eclipse.sed.ifl.control.comparator;

import java.util.Comparator;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public class InteractivityComparator implements Comparator<Entry<IMethodDescription, Score>> {
	

	@Override
	public int compare(Entry<IMethodDescription, Score> o1, Entry<IMethodDescription, Score> o2) {
		return Boolean.compare(o1.getKey().isInteractive(), o2.getKey().isInteractive());
	}

}
