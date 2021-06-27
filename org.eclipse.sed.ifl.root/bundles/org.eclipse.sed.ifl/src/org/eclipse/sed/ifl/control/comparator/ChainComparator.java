package org.eclipse.sed.ifl.control.comparator;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.Score;

public class ChainComparator implements Comparator<Entry<IMethodDescription, Score>> {

	private List<Comparator<Entry<IMethodDescription, Score>>> comparators;

	public ChainComparator(List<Comparator<Entry<IMethodDescription, Score>>> comparators) {
		this.comparators = comparators;
	}

	@Override
	public int compare(Entry<IMethodDescription, Score> o1, Entry<IMethodDescription, Score> o2) {
		int result;
		for (Comparator<Entry<IMethodDescription, Score>> comparator : comparators) {
			 result = comparator.compare(o1, o2);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}
}
