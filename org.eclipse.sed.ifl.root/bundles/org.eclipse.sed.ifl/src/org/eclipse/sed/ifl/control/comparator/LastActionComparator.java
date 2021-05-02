package org.eclipse.sed.ifl.control.comparator;

import java.util.Comparator;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;

public class LastActionComparator implements Comparator<Entry<IMethodDescription, Score>> {

	private Comparator<Monument<Score, IMethodDescription, IUserFeedback>> nullComparator = Comparator.nullsFirst(Comparator.comparing(Monument::getChange));
	
	@Override
	public int compare(Entry<IMethodDescription, Score> o1, Entry<IMethodDescription, Score> o2) {
		return nullComparator.compare(o1.getValue().getLastAction(), o2.getValue().getLastAction());
	}
}
