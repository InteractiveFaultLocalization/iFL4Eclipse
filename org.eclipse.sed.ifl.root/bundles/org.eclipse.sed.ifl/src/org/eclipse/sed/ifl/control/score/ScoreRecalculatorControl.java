package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.event.IListener;

public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> { 

		
	
	public ScoreRecalculatorControl(boolean interactivity) {
		this.interactivity = interactivity;
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

	private IListener<String> eventRecalculationSelected = new IListener<String>() { 

		public void invoke(String event) throws UnsupportedOperationException {
			System.out.println("Recalculating scores are requested...");
			throw new UnsupportedOperationException("Function is not yet implemented");

		}
	};
	
	public void start(String event)throws UnsupportedOperationException {
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
