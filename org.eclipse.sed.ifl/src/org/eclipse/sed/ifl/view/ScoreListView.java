package org.eclipse.sed.ifl.view;

import java.util.List;
import java.util.Map;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
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

	public void refreshScores(Map<IMethodDescription, Score> scores) {
		ui.clearMethodScores();
		ui.setMethodScore(scores);
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
