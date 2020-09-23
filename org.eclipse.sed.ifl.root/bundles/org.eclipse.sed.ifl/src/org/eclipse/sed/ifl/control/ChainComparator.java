package org.eclipse.sed.ifl.control;

import java.util.Comparator;
import java.util.List;

import org.eclipse.sed.ifl.control.score.filter.ScoreFilter;

public class ChainComparator implements Comparator<ScoreFilter> {
	
		   private List<Comparator<ScoreFilter>> comparatorList;
		   @Override
		public
		   int compare(ScoreFilter a, ScoreFilter b) {
		       int result;
		       for(Comparator<ScoreFilter> comparator : comparatorList) {
		         if ((result = comparator.compare(a, b)) != 0) {
		             return result;
		         }
		       }
		       return 0;
		   }
		}


