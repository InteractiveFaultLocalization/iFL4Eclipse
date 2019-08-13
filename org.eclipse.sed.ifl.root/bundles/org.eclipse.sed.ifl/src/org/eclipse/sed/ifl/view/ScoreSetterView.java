package org.eclipse.sed.ifl.view;

import java.util.List;

import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.ide.gui.element.ScoreSetter;
import org.eclipse.swt.widgets.Composite;

public class ScoreSetterView extends View implements IEmbeddable, IDataPointsDisplayer {

	private ScoreSetter ui = new ScoreSetter();
	
	@Override
	public void setParent(Composite parent) {
		ui.setParent(parent);
	}

	public void setTitle(String name) {
		ui.setTitle(name);
	}

	@Override
	public void setDataPoints(List<Double> points) {
		ui.setDataPoints(points);
	}

}
