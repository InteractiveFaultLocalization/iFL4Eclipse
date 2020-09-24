package org.eclipse.sed.ifl.control.comparator;

import java.util.Comparator;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public class LastActionComparator implements Comparator<Entry<IMethodDescription, Score>> {

	@Override
	public int compare(Entry<IMethodDescription, Score> o1, Entry<IMethodDescription, Score> o2) {
		return (o1.getValue().getLastAction() == null || o2.getValue().getLastAction() == null ? 0
				: o1.getValue().getLastAction().getChange().compareTo(o2.getValue().getLastAction().getChange()));
	}

}
