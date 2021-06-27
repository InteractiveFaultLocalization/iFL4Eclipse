package org.eclipse.sed.ifl.control.comparator;

import java.util.Comparator;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.Score;
import org.eclipse.sed.ifl.control.score.ScoreHistoryControl;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;

public class LastActionComparator implements Comparator<Entry<IMethodDescription, Score>> {

	private ScoreHistoryControl scoreHistory;
	private Comparator<Monument<Score, IMethodDescription, IUserFeedback>> nullComparator = Comparator.nullsFirst(Comparator.comparing(Monument::getChange));
	
	public LastActionComparator(ScoreHistoryControl scoreHistory) {
		super();
		this.scoreHistory = scoreHistory;
	}

	@Override
	public int compare(Entry<IMethodDescription, Score> o1, Entry<IMethodDescription, Score> o2) {
		return nullComparator.compare(scoreHistory.getLastOf(o1.getKey()), scoreHistory.getLastOf(o2.getKey()));
	}
}
