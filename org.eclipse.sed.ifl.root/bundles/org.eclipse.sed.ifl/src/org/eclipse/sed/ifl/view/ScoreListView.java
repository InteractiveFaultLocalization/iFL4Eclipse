package org.eclipse.sed.ifl.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class ScoreListView extends View implements IEmbeddable {
	ScoreListUI ui = new ScoreListUI();

	@Override
	public void setParent(Composite parent) {
		ui.setParent(parent);
	}

	public void refreshScores(Map<IMethodDescription, Score> scores) {
		ui.clearMethodScores();
		ui.setMethodScore(scores);
	}

	public void createOptionsMenu(Iterable<Option> options) {
		ui.createContexMenu(options);
	}

	@Override
	public void init() {
		ui.eventOptionSelected().add(optionSelectedListener);
		ui.eventCustomOptionSelected().add(customOptionSelectedListener);
		ui.eventSortRequired().add(sortListener);
		ui.eventNavigateToRequired().add(navigateToListener);
		ui.eventNavigateToContext().add(navigateToContextListener);
		ui.eventSelectionChanged().add(selectionChangedListener);
		ui.eventlowerScoreLimitChanged().add(lowerScoreLimitChangedListener);
		ui.eventlowerScoreLimitEnabled().add(lowerScoreLimitEnabledListener);
		ui.eventContextSizeLimitEnabled().add(contextSizeLimitEnabledListener);
		ui.eventContextSizeLimitChanged().add(contextSizeLimitChangedListener);
		ui.eventContextSizeRelationChanged().add(contextSizeRelationChangedListener);
		ui.eventOpenDetailsRequired().add(openDetailsRequiredListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		ui.eventOptionSelected().remove(optionSelectedListener);
		ui.eventCustomOptionSelected().remove(customOptionSelectedListener);
		ui.eventSortRequired().remove(sortListener);
		ui.eventNavigateToRequired().remove(navigateToListener);
		ui.eventNavigateToContext().remove(navigateToContextListener);
		ui.eventSelectionChanged().remove(selectionChangedListener);
		ui.eventlowerScoreLimitChanged().remove(lowerScoreLimitChangedListener);
		ui.eventlowerScoreLimitEnabled().remove(lowerScoreLimitEnabledListener);
		ui.eventContextSizeLimitEnabled().remove(contextSizeLimitEnabledListener);
		ui.eventContextSizeLimitChanged().remove(contextSizeLimitChangedListener);
		ui.eventContextSizeRelationChanged().remove(contextSizeRelationChangedListener);
		ui.eventOpenDetailsRequired().remove(openDetailsRequiredListener);
		super.teardown();
	}
	
	private NonGenericListenerCollection<List<IMethodDescription>> selectionChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<List<IMethodDescription>> eventSelectionChanged() {
		return selectionChanged;
	}
	
	private IListener<Table> selectionChangedListener = event -> {
		List<IMethodDescription> selection = new ArrayList<>();
		for (TableItem item : event.getSelection()) {
			selection.add((IMethodDescription)item.getData());
		}
		selectionChanged.invoke(selection);
	};

	private NonGenericListenerCollection<IUserFeedback> optionSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventOptionSelected() {
		return optionSelected;

	}

	private IListener<IUserFeedback> optionSelectedListener = optionSelected::invoke;

	private NonGenericListenerCollection<List<IMethodDescription>> customOptionSelected = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<List<IMethodDescription>> eventCustomOptionSelected() {
		return customOptionSelected;
	}

	private IListener<List<IMethodDescription>> customOptionSelectedListener = customOptionSelected::invoke;

	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	private IListener<SortingArg> sortListener = sortRequired::invoke;
	
	private NonGenericListenerCollection<IMethodDescription> navigateToRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<IMethodDescription> eventNavigateToRequired() {
		return navigateToRequired;
	}
	
	private IListener<IMethodDescription> navigateToListener = navigateToRequired::invoke;
	
	private NonGenericListenerCollection<List<IMethodDescription>> navigateToContext = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<List<IMethodDescription>> eventNavigateToContext() {
		return navigateToContext;
	}
	
	private IListener<List<IMethodDescription>> navigateToContextListener = navigateToContext::invoke;
	
	public void highlight(List<MethodIdentity> context) {
		ui.highlight(context);
	}
	
	public void setScoreFilter(double min, double max, double current) {
		ui.setScoreFilter(min, max, current);
	}
	
	public void setScoreFilter(double min, double max) {
		ui.setScoreFilter(min, max);
	}
	
	public void showNoItemsLabel(boolean show) {
		ui.showNoItemsLabel(show);
	}
	
	private NonGenericListenerCollection<Double> lowerScoreLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Double> eventlowerScoreLimitChanged() {
		return lowerScoreLimitChanged;
	}
	
	private IListener<Double> lowerScoreLimitChangedListener = lowerScoreLimitChanged::invoke;

	private NonGenericListenerCollection<Boolean> lowerScoreLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventlowerScoreLimitEnabled() {
		return lowerScoreLimitEnabled;
	}
	
	private IListener<Boolean> lowerScoreLimitEnabledListener = lowerScoreLimitEnabled::invoke;
	
	private NonGenericListenerCollection<Boolean> contextSizeLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventcontextSizeLimitEnabled() {
		return contextSizeLimitEnabled;
	}
	
	private IListener<Boolean> contextSizeLimitEnabledListener = contextSizeLimitEnabled::invoke;
	
	private NonGenericListenerCollection<Integer> contextSizeLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Integer> eventContextSizeLimitChanged() {
		return contextSizeLimitChanged;
	}
	
	private IListener<Integer> contextSizeLimitChangedListener = contextSizeLimitChanged::invoke;
	
	private NonGenericListenerCollection<String> contextSizeRelationChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventContextSizeRelationChanged() {
		return contextSizeRelationChanged;
	}
	
	private IListener<String> contextSizeRelationChangedListener = contextSizeRelationChanged::invoke;
	
	private NonGenericListenerCollection<IMethodDescription> openDetailsRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<IMethodDescription> eventOpenDetailsRequired() {
		return openDetailsRequired;
	}
	
	IListener<IMethodDescription> openDetailsRequiredListener = openDetailsRequired::invoke;
}
