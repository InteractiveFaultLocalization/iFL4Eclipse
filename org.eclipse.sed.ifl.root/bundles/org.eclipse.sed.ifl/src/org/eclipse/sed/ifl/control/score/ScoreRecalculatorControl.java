package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.event.IListener;

public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {

	public ScoreRecalculatorControl() {

	}

	private IListener<String> eventRecalculationSelected = new IListener<String>() {

		public void invoke(String event) throws UnsupportedOperationException {
			System.out.println("Recalculating scores are requested...");
			throw new UnsupportedOperationException("Function is not yet implemented");

		}
	};

	public void recalculate(String event) throws UnsupportedOperationException {
		this.eventRecalculationSelected.invoke(event);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void teardown() {
		super.teardown();
	}

}
