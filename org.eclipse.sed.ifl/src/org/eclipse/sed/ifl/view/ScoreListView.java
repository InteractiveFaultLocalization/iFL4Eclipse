package org.eclipse.sed.ifl.view;

import java.util.Map;

import org.eclipse.sed.ifl.control.score.ScoreListControl.ScoreStatus;
import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
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
		ui.clearMethodScores();
		ui.setMethodScore(scores, status.getIconPath());
	}

	public void refreshScores(Map<ScoreStatus, Map<IMethodDescription, Defineable<Double>>> buckets) {
		ui.clearMethodScores();
		for (var bucket : buckets.entrySet()) {
			ui.setMethodScore(bucket.getValue(), bucket.getKey().getIconPath());
		}
	}

	public void createOptionsMenu(Iterable<Option> options) {
		ui.createMenuOptions(options);
		ui.eventOptionSelected().add(optionSelectedListener);
	}

	private NonGenericListenerCollection<Map> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Map> eventOptionSelected() {
		return optionSelected;

	}

	private IListener<Map> optionSelectedListener = new IListener<Map>() {

		@Override
		public void invoke(Map event) {
			optionSelected.invoke(event);
		}

	};

}
