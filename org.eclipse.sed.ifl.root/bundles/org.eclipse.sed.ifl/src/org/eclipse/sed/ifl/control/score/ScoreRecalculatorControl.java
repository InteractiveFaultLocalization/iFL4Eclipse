package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.view.ScoreRecalculatorView;

public class ScoreRecalculatorControl extends Control<ScoreListModel, ScoreRecalculatorView> {

	

	
	
	public ScoreRecalculatorControl(boolean interactivity) {
		this.interactivity = interactivity;
	}

	public void load() {
		getView().select();
	}
	
	private boolean interactivity;
	
	
	


	public class Entry {
		private String name;
		private String detailsLink;
		private boolean interactivity;

		public Entry(String name, String detailsLink, boolean interactivity) {
			super();
			this.name = name;
			this.detailsLink = detailsLink;
			this.interactivity = interactivity;
		}

		public String getName() {
			return name;
		}

		public String getDetailsLink() {
			return detailsLink;
		}

		public boolean isInteractive() {
			return interactivity;
		}
	}

	private IListener<String> eventrecalculationSelected = new IListener<String>() {

		public void invoke(String event) throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Function is not yet implemented");

		}
	};

	@Override
	public void init() {
		getView().eventrecalculationSelected().add(eventrecalculationSelected);
		super.init();
	}

	@Override
	public void teardown() {
		getView().eventrecalculationSelected().remove(eventrecalculationSelected);
		super.teardown();
	}

	

}
