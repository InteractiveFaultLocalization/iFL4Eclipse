package org.eclipse.sed.ifl.control.comparator;

import java.util.Comparator;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.Score;

public class PathComparator implements Comparator<Entry<IMethodDescription, Score>> {

	@Override
	public int compare(Entry<IMethodDescription, Score> o1, Entry<IMethodDescription, Score> o2) {
		return o1.getKey().getLocation().getAbsolutePath()
				.compareToIgnoreCase(o2.getKey().getLocation().getAbsolutePath());
	}

}
