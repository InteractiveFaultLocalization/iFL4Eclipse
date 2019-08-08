package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.ide.gui.element.ScoreSetter;
import org.eclipse.swt.widgets.Composite;

public class ScoreSetterView extends View implements IEmbeddable {

	private ScoreSetter ui = new ScoreSetter();
	
	@Override
	public void setParent(Composite parent) {
		ui.setParent(parent);
	}

}
