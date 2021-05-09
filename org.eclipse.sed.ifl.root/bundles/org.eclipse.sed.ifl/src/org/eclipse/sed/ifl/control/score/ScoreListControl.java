package org.eclipse.sed.ifl.control.score;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.bi.faced.MethodScoreHandler;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.feedback.ContextBasedOptionCreatorControl;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.control.score.filter.ContextSizeFilter;
import org.eclipse.sed.ifl.control.score.filter.FilterControl;
import org.eclipse.sed.ifl.control.score.filter.HideUndefinedFilter;
import org.eclipse.sed.ifl.control.score.filter.LessOrEqualFilter;
import org.eclipse.sed.ifl.control.score.filter.NameFilter;
import org.eclipse.sed.ifl.control.score.filter.ScoreFilter;
import org.eclipse.sed.ifl.core.BasicIflMethodScoreHandler;
import org.eclipse.sed.ifl.ide.accessor.gui.FeatureAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.EditorAccessor;
import org.eclipse.sed.ifl.ide.gui.dialogs.CustomInputDialog;
import org.eclipse.sed.ifl.model.FilterModel;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.AbortEvent;
import org.eclipse.sed.ifl.model.monitor.event.ConfirmEvent;
import org.eclipse.sed.ifl.model.monitor.event.NavigationEvent;
import org.eclipse.sed.ifl.model.monitor.event.SelectionChangedEvent;
import org.eclipse.sed.ifl.model.monitor.event.UserFeedbackEvent;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.score.ScoreListModel.ScoreChange;
import org.eclipse.sed.ifl.model.score.history.Monument;
import org.eclipse.sed.ifl.model.score.history.ScoreHistoryModel;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionCreatorModel;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.sed.ifl.util.items.IMethodDescriptionCollectionUtil;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.ContextBasedOptionCreatorView;
import org.eclipse.sed.ifl.view.FilterView;
import org.eclipse.sed.ifl.view.ScoreHistoryView;
import org.eclipse.sed.ifl.view.ScoreListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.MethodIdentity;
import org.eclipse.sed.ifl.commons.model.source.Score;

public class ScoreListControl extends Control<ScoreListModel, ScoreListView> {

	private BasicIflMethodScoreHandler handler = new BasicIflMethodScoreHandler(null);

	private ActivityMonitorControl activityMonitor;
	
	private ScoreHistoryControl scoreHistory;

	private ContextBasedOptionCreatorControl contextBasedOptionCreator; 
	
	private FilterControl filterControl;

	@Override
	public void init() {
		activityMonitor = new ActivityMonitorControl(new ActivityMonitorModel());
		
		scoreHistory = new ScoreHistoryControl();
		scoreHistory.setModel(new ScoreHistoryModel());
		ScoreHistoryView scoreHistoryView = new ScoreHistoryView();
		getView().embed(scoreHistoryView);
		scoreHistory.setView(scoreHistoryView);
		
		filterControl = new FilterControl();
		filterControl.setView(new FilterView());
		filterControl.setModel(new FilterModel());
		
		contextBasedOptionCreator = new ContextBasedOptionCreatorControl();
		contextBasedOptionCreator.setModel(new ContextBasedOptionCreatorModel());
		contextBasedOptionCreator.setView(new ContextBasedOptionCreatorView());
		this.addSubControl(contextBasedOptionCreator);

		this.addSubControl(activityMonitor);
		this.addSubControl(scoreHistory);
		this.addSubControl(filterControl);
		contextBasedOptionCreator.eventContextBasedFeedbackOption().add(optionSelectedListener);
		contextBasedOptionCreator.eventContextBasedOptionNeeded().add(contextBasedOptionProviderListener);
		getView().createOptionsMenu(handler.getProvidedOptions());
		getView().refreshScores(getModel().getScores());
		getModel().eventScoreUpdated().add(scoreUpdatedListener);
		getView().eventOptionSelected().add(optionSelectedListener);
		getView().eventCustomOptionSelected().add(customOptionSelectedListener);
		handler.eventScoreUpdated().add(scoreRecalculatedListener);
		handler.loadMethodsScoreMap(getModel().getRawScore());
		handler.eventHighLightRequested().add(highlightRequestListener);;
		filters.add(hideUndefinedFilter);
		filters.add(lessOrEqualFilter);
		filters.add(contextSizeFilter);
		filters.add(nameFilter);
		
		getView().eventSortRequired().add(sortListener);
		getView().eventNavigateToRequired().add(navigateToListener);
		getView().eventNavigateToContext().add(navigateToContextListener);
		getView().eventSelectionChanged().add(selectionChangedListener);
		getView().eventOpenDetailsRequired().add(openDetailsRequiredListener);
		getModel().eventScoreLoaded().add(scoreLoadedListener);
		
		getView().eventOpenFiltersPart().add(openFiltersPage);
		filterControl.eventLimitFilterRelationChanged().add(limitFilterRelationChangedListener);
		filterControl.eventlowerScoreLimitChanged().add(lowerScoreLimitChangedListener);
		filterControl.eventlowerScoreLimitEnabled().add(lowerScoreLimitEnabledListener);
		filterControl.eventContextSizeLimitEnabled().add(contextSizeLimitEnabledListener);
		filterControl.eventContextSizeLimitChanged().add(contextSizeLimitChangedListener);
		filterControl.eventContextSizeRelationChanged().add(contextSizeRelationChangedListener);
		filterControl.eventNameFilterChanged().add(nameFilterChangedListener);
		filterControl.eventSortRequired().add(sortListener);
		
		super.init();
	}

	@Override
	public void teardown() {
		contextBasedOptionCreator.eventContextBasedFeedbackOption().remove(optionSelectedListener);
		contextBasedOptionCreator.eventContextBasedOptionNeeded().remove(contextBasedOptionProviderListener);
		getModel().eventScoreUpdated().remove(scoreUpdatedListener);
		getView().eventOptionSelected().remove(optionSelectedListener);
		getView().eventCustomOptionSelected().remove(customOptionSelectedListener);
		handler.eventScoreUpdated().remove(scoreRecalculatedListener);
		handler.eventHighLightRequested().remove(highlightRequestListener);
		getView().eventSortRequired().remove(sortListener);
		getView().eventNavigateToRequired().remove(navigateToListener);
		getView().eventNavigateToContext().remove(navigateToContextListener);
		getView().eventSelectionChanged().remove(selectionChangedListener);
		getView().eventOpenDetailsRequired().remove(openDetailsRequiredListener);
		getModel().eventScoreLoaded().remove(scoreLoadedListener);
		
		getView().eventOpenFiltersPart().remove(openFiltersPage);
		filterControl.eventLimitFilterRelationChanged().remove(limitFilterRelationChangedListener);
		filterControl.eventlowerScoreLimitChanged().remove(lowerScoreLimitChangedListener);
		filterControl.eventlowerScoreLimitEnabled().remove(lowerScoreLimitEnabledListener);
		filterControl.eventContextSizeLimitEnabled().remove(contextSizeLimitEnabledListener);
		filterControl.eventContextSizeLimitChanged().remove(contextSizeLimitChangedListener);
		filterControl.eventContextSizeRelationChanged().remove(contextSizeRelationChangedListener);
		filterControl.eventNameFilterChanged().remove(nameFilterChangedListener);
		filterControl.eventSortRequired().add(sortListener);
		
		super.teardown();
		activityMonitor = null;
		scoreHistory = null;
	}

	IListener<IMethodDescription> openDetailsRequiredListener = event -> {
		try {
			new FeatureAccessor().openLink(EU.tryUnchecked(() -> new URL(event.getDetailsLink())));
		} catch (RuntimeException e) {
			MessageDialog.open(MessageDialog.ERROR, Display.getCurrent().getActiveShell(), "Error opening details", "The details link can not be opened. Please check if the CSV file provides a working details link.", SWT.NONE);
		}
		
	};

	private List<ScoreFilter> filters = new ArrayList<>();
	private HideUndefinedFilter hideUndefinedFilter = new HideUndefinedFilter(true);

	private LessOrEqualFilter lessOrEqualFilter = new LessOrEqualFilter(true);
	
	private ContextSizeFilter contextSizeFilter = new ContextSizeFilter(false);
	
	private NameFilter nameFilter = new NameFilter(true);

	private Map<IMethodDescription, Score> filterForView(Map<IMethodDescription, Score> allScores) {
		Stream<Entry<IMethodDescription, Score>> filtered = allScores.entrySet().stream();
		Map<IMethodDescription, Score> toDisplay = new HashMap<>();
		for (ScoreFilter filter : filters) {
			filtered = filtered.filter(filter);
		}
		if (sorting != null) {
			switch (sorting) {
			case Score:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getValue().compareTo(b.getValue()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case Name:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getName().compareToIgnoreCase(b.getKey().getId().getName()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case Signature:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getSignature().compareToIgnoreCase(b.getKey().getId().getSignature()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case ParentType:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getParentType().compareToIgnoreCase(b.getKey().getId().getParentType()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case Path:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getLocation().getAbsolutePath().compareToIgnoreCase(b.getKey().getLocation().getAbsolutePath()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case ContextSize:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * new Integer(a.getKey().getContext().size()).compareTo(b.getKey().getContext().size()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case Position:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * new Integer(a.getKey().getLocation().getBegining().getOffset()).compareTo(b.getKey().getLocation().getBegining().getOffset()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case Interactivity:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * new Boolean(a.getKey().isInteractive()).compareTo(b.getKey().isInteractive()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case LastAction:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * (a.getValue().getLastAction() == null || b.getValue().getLastAction() == null ? 0 : a.getValue().getLastAction().getChange().compareTo(b.getValue().getLastAction().getChange())))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			default:
				toDisplay = filtered.collect(Collectors.collectingAndThen(Collectors.toMap(Entry::getKey, Entry::getValue), Collections::unmodifiableMap));
				break;
			}
		} else {
			toDisplay = filtered.collect(Collectors.collectingAndThen(Collectors.toMap(Entry::getKey, Entry::getValue), Collections::unmodifiableMap));
		}
		return toDisplay;
	}

	private IListener<EmptyEvent> openFiltersPage = event -> {
		filterControl.showFilterPart();
	};
	
	public void setHideUndefinedScores(Boolean status) {
		System.out.println("hiding undefined scores are requested to set: " + status);
		hideUndefinedFilter.setEnabled(status);
		refreshView();
	}

	private IListener<Double> lowerScoreLimitChangedListener = limit -> {
		lessOrEqualFilter.setLimit(limit);
		refreshView();
	};

	private IListener<Boolean> lowerScoreLimitEnabledListener = enabled -> {
		lessOrEqualFilter.setEnabled(enabled);
		refreshView();
	};
	
	private IListener<Boolean> contextSizeLimitEnabledListener = enabled -> {
		contextSizeFilter.setEnabled(enabled);
		refreshView();
	};
	
	private IListener<Integer> contextSizeLimitChangedListener = limit -> {
		contextSizeFilter.setLimit(limit);
		refreshView();
	};
	
	private IListener<String> contextSizeRelationChangedListener = relation -> {
		contextSizeFilter.setRelation(relation);
		refreshView();
	};
	
	private IListener<List<IMethodDescription>> highlightRequestListener = list -> {
		getView().highlightRequest(list);
	};
	
	private IListener<String> nameFilterChangedListener = name -> {
		nameFilter.setName(name);
		refreshView();
	};

	private IListener<String> limitFilterRelationChangedListener = relation -> {
		lessOrEqualFilter.setRelation(relation);
		refreshView();
	};
	
	private void refreshView() {
		for (Entry<IMethodDescription, Score> entry : getModel().getScores().entrySet()) {
			Monument<Score, IMethodDescription, IUserFeedback> last = scoreHistory.getLastOf(entry.getKey());
			entry.getValue().setLastAction(last);
		}
		Map<IMethodDescription, Score> toDisplay = filterForView(getModel().getScores());
		scoreHistory.hideView();
		getView().refreshScores(toDisplay);
		getView().showNoItemsLabel(toDisplay.isEmpty());
	}

	private IListener<List<IMethodDescription>> selectionChangedListener = event -> {
		List<MethodIdentity> context = new ArrayList<>();
		for (IMethodDescription item : event) {
			context.addAll(item.getContext());
		}
		getView().highlight(context);
		if (event.size() == 1) {
			scoreHistory.display(event.get(0));
		} else {
			scoreHistory.hideView();
		}
		activityMonitor.log(new SelectionChangedEvent(event));
	};

	private NonGenericListenerCollection<SideEffect> terminationRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<SideEffect> eventTerminationRequested() {
		return terminationRequested;
	}
	
	private IListener<Boolean> contextBasedOptionProviderListener = event -> {
		for(Option option : handler.getProvidedOptions()) {
			if(option.getId().equals("CONTEXT_BASED_OPTION")) {
				contextBasedOptionCreator.createContextBasedUserFeedback(option);
				break;
			}
		}
	};
	
	private IListener<IUserFeedback> optionSelectedListener = event -> {
		SideEffect effect = event.getChoice().getSideEffect();		
		if (effect == SideEffect.NOTHING) {
			handler.updateScore(event);
			activityMonitor.log(new UserFeedbackEvent(event));
		} else {
			boolean confirmed = false;
			CustomInputDialog dialog = new CustomInputDialog(Display.getCurrent().getActiveShell(), "Terminal choice confirmation:" + event.getChoice().getTitle(),
					"You choose an option which will end this iFL session with a " + (effect.isSuccessFul() ? "successful" : "unsuccessful") + " result.\n"
					+ "Please confim that you intend to mark the selected code elements by typing their name next to them in the text areas. Element names are case-sensitive.",
					getElementNames(event));
			if (dialog.open() == InputDialog.OK) {
				confirmed = true;
			} else {
				confirmed = false;
				activityMonitor.log(new AbortEvent(new UserFeedback(event.getChoice(), event.getSubjects(), event.getUser())));
			}
			if (confirmed) {
				activityMonitor.log(new ConfirmEvent(event));
				terminationRequested.invoke(effect);
			}
		}
	};
	
	private IListener<List<IMethodDescription>> customOptionSelectedListener = event -> {
		Map<IMethodDescription, Defineable<Double>> all = getModel().getRawScore();
		List<IMethodDescription> selected = event;
		List<IMethodDescription> context = IMethodDescriptionCollectionUtil.collectContext(selected, all);
		List<IMethodDescription> others = IMethodDescriptionCollectionUtil.collectOther(all, selected, context);
		contextBasedOptionCreator.createNewOption(selected, context, others, all);
	};

	private IListener<Map<IMethodDescription, ScoreChange>> scoreUpdatedListener = event -> {
		Map<IMethodDescription, Defineable<Double>> rawScores = getModel().getRawScore();
		Optional<Defineable<Double>> min = rawScores.values().stream().filter(score -> score.isDefinit()).min(Comparator.comparing(score -> score.getValue()));
		Optional<Defineable<Double>> max = rawScores.values().stream().filter(score -> score.isDefinit()).max(Comparator.comparing(score -> score.getValue()));
		if (min.isPresent() && max.isPresent()) {
			//getView().setScoreFilter(min.get().getValue(), max.get().getValue());
			filterControl.setScoreFilter(min.get().getValue(), max.get().getValue());
		}
		handler.loadMethodsScoreMap(rawScores);
		//TODO: history-saving-bug history should be saved here but we do not have the cause, since this event come from the model,
		// which do not need to know about it.
		refreshView();
	};

	private IListener<MethodScoreHandler.ScoreUpdateArgs> scoreRecalculatedListener = event -> {
		Map<IMethodDescription, ScoreChange> changes = getModel().updateScore(
			event.getNewScores().entrySet().stream()
			.collect(
				Collectors.toMap(
					Map.Entry::getKey,
					i -> new Score(i.getValue()))));
		for (Entry<IMethodDescription, ScoreListModel.ScoreChange> entry : changes.entrySet()) {
			scoreHistory.store(entry.getValue().getNewScore(), entry.getValue().getOldScore(), entry.getKey(), event.getCause());
		}
		//TODO: this is a redundant refresh since the event listener of scoreUpdatedListener already refreshed it,
		// but the history do not contains the monuments requested to display. Search history-saving-bug for more.
		refreshView();
	};

	private SortingArg sorting;

	private IListener<SortingArg> sortListener = event -> {
		sorting = event;
		refreshView();
	};

	EditorAccessor editor = new EditorAccessor();

	private IListener<IMethodDescription> navigateToListener = event -> {
		editor.open(event.getLocation().getAbsolutePath(), event.getLocation().getBegining().getOffset());
		activityMonitor.log(new NavigationEvent(event));
	};
	
	private IListener<List<IMethodDescription>> navigateToContextListener = event -> {
		for(IMethodDescription method : event) {
			editor.open(method.getLocation().getAbsolutePath(), method.getLocation().getBegining().getOffset());
			activityMonitor.log(new NavigationEvent(method));
		}
	};
	
	private static final int TOP_SCORE_LIMIT = 9;
	
	private IListener<EmptyEvent> scoreLoadedListener = __ -> {
		Map<IMethodDescription, Defineable<Double>> rawScores = getModel().getRawScore().entrySet().stream()
			.sorted((a, b) -> -1 * a.getValue().compareTo(b.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		Optional<Defineable<Double>> min = rawScores.values().stream().filter(score -> score.isDefinit()).min(Comparator.comparing(score -> score.getValue()));
		Optional<Defineable<Double>> max = rawScores.values().stream().filter(score -> score.isDefinit()).max(Comparator.comparing(score -> score.getValue()));
		if (rawScores.size() > TOP_SCORE_LIMIT && min.isPresent() && max.isPresent()) {
			Defineable<Double> limit = rawScores.entrySet().stream().skip(TOP_SCORE_LIMIT).collect(Collectors.toList()).get(0).getValue();
			if (limit.isDefinit()) {
				//getView().setScoreFilter(min.get().getValue(), max.get().getValue(), limit.getValue());
				filterControl.setScoreFilter(min.get().getValue(), max.get().getValue(), limit.getValue());
			}
			MessageDialog.open(
					MessageDialog.INFORMATION, null,
					"iFL Score List",
					"Only the top 10 source code items are displayed.\n"
					+ "You can set the filters to show more or less items.", SWT.NONE);
		}
	};

	private List<String> getElementNames(IUserFeedback event) {
		List<String> rvList = new ArrayList<String>(event.getSubjects().size());
		for(IMethodDescription method : event.getSubjects().keySet()) {
			rvList.add(method.getId().getName());
		}
		return rvList;
	}

	public void resetFilterState() {
		filterControl.resetFilterState();
		
	}

	public void closeFilterPart() {
		filterControl.close();
		
	}
}
