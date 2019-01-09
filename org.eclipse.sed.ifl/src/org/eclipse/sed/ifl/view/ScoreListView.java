package org.eclipse.sed.ifl.view;

import java.util.List;
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

	@Deprecated
	public void refreshScores(Map<IMethodDescription, Defineable<Double>> scores, ScoreStatus status) {
		ui.clearMethodScores();
		ui.setMethodScore(scores, status.getIconPath());
	}

	@Deprecated
	public void refreshScores(Map<ScoreStatus, Map<IMethodDescription, Defineable<Double>>> buckets) {
		ui.clearMethodScores();
		for (var bucket : buckets.entrySet()) {
			ui.setMethodScore(bucket.getValue(), bucket.getKey().getIconPath());
		}
	}

	public void createOptionsMenu(Iterable<Option> options) {
		ui.createMenuOptions(options);
		//TODO: move to init!
		ui.eventOptionSelected().add(optionSelectedListener);
	}

	private NonGenericListenerCollection<Map<String, List<IMethodDescription>>> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Map<String, List<IMethodDescription>>> eventOptionSelected() {
		return optionSelected;

	}

	private IListener<Map<String, List<IMethodDescription>>> optionSelectedListener = new IListener<>() {

		@Override
		public void invoke(Map<String, List<IMethodDescription>> event) {
			optionSelected.invoke(event);
		}

	};

}
