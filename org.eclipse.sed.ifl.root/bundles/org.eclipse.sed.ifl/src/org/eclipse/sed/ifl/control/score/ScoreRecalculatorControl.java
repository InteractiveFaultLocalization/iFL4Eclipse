package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.view.ScoreLoaderView;

public class ScoreRecalculatorControl extends Control<ScoreListModel, ScoreLoaderView> {
	
	private boolean interactivity;
	
	public ScoreRecalculatorControl(boolean interactivity) {
		this.interactivity = interactivity;
	}
	
	public void load() {
		getView().select();
	}
	
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
	
	private IListener<String> fileSelectedListener = new IListener<String>() {
		public void invoke(String event) {
			
		}
	};
	
	@Override
	public void init() {
		getView().eventFileSelected().add(fileSelectedListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		getView().eventFileSelected().remove(fileSelectedListener);
		super.teardown();
	}

}
