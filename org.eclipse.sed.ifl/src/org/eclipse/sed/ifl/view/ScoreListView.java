package org.eclipse.sed.ifl.view;

import java.util.Map;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.source.ICodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
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
	}

	@Override
	public void init() {
		ui.eventOptionSelected().add(optionSelectedListener);
		ui.eventSortRequired().add(sortListener);
		ui.eventNavigateToRequired().add(navigateToListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		ui.eventOptionSelected().remove(optionSelectedListener);
		ui.eventSortRequired().remove(sortListener);
		ui.eventNavigateToRequired().remove(navigateToListener);
		super.teardown();
	}
	
	private NonGenericListenerCollection<IUserFeedback> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventOptionSelected() {
		return optionSelected;

	}

	private IListener<IUserFeedback> optionSelectedListener = new IListener<>() {

		@Override
		public void invoke(IUserFeedback event) {
			optionSelected.invoke(event);
		}

	};

	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	private IListener<SortingArg> sortListener = new IListener<>() {
		
		@Override
		public void invoke(org.eclipse.sed.ifl.control.score.SortingArg event) {
			sortRequired.invoke(event);
		}
	};
	
	private NonGenericListenerCollection<ICodeChunkLocation> navigateToRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<ICodeChunkLocation> eventNavigateToRequired() {
		return navigateToRequired;
	}
	
	private IListener<ICodeChunkLocation> navigateToListener = navigateToRequired::invoke;
}
