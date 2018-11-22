package org.eclipse.sed.ifl.view;

import java.util.Map;

import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.widgets.Composite;

public class ScoreListView extends View {
	ScoreListUI ui;
	
	public ScoreListView(ScoreListUI ui) {
		super();
		this.ui = ui;
	}

	@Override
	public Composite getUI() {
		return ui;
	}
	
	public void refreshScores(Map<IMethodDescription, Defineable<Double>> scores, ScoreStatus status) {
		ui.setMethodScore(scores, status.getIconPath());
	}
	
	public enum ScoreStatus {
		NONE (null),
		INCREASED ("icons/up32.png"),
		DECREASED ("icons/down32.png"),
		UNDEFINED ("icons/undefined32.png");
		
		private final String iconPath;
		
		ScoreStatus(String iconPath) {
			this.iconPath = iconPath;
		}
		
		public String getIconPath() {
			return iconPath;
		}
	}
}
