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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.bi.faced.MethodScoreHandler;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.DualListControl;
import org.eclipse.sed.ifl.control.comparator.ChainComparator;
import org.eclipse.sed.ifl.control.comparator.ContextSizeComparator;
import org.eclipse.sed.ifl.control.comparator.InteractivityComparator;
import org.eclipse.sed.ifl.control.comparator.LastActionComparator;
import org.eclipse.sed.ifl.control.comparator.NameComparator;
import org.eclipse.sed.ifl.control.comparator.ParentTypeComparator;
import org.eclipse.sed.ifl.control.comparator.PathComparator;
import org.eclipse.sed.ifl.control.comparator.PositionComparator;
import org.eclipse.sed.ifl.control.comparator.ScoreComparator;
import org.eclipse.sed.ifl.control.comparator.SignatureComparator;
import org.eclipse.sed.ifl.control.feedback.ContextBasedOptionCreatorControl;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.control.score.filter.BooleanFilter;
import org.eclipse.sed.ifl.control.score.filter.BooleanRule;
import org.eclipse.sed.ifl.control.score.filter.DoubleFilter;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.FilterControl;
import org.eclipse.sed.ifl.control.score.filter.HideUndefinedFilter;
import org.eclipse.sed.ifl.control.score.filter.LastActionFilter;
import org.eclipse.sed.ifl.control.score.filter.LastActionRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.ScoreFilter;
import org.eclipse.sed.ifl.control.score.filter.SortRule;
import org.eclipse.sed.ifl.control.score.filter.StringFilter;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.sed.ifl.core.BasicIflMethodScoreHandler;
import org.eclipse.sed.ifl.ide.accessor.gui.FeatureAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.EditorAccessor;
import org.eclipse.sed.ifl.ide.gui.dialogs.CustomInputDialog;
import org.eclipse.sed.ifl.model.DualListModel;
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
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
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
import org.eclipse.sed.ifl.view.DualListView;
import org.eclipse.sed.ifl.view.FilterView;
import org.eclipse.sed.ifl.view.ScoreHistoryView;
import org.eclipse.sed.ifl.view.ScoreListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class ScoreListControl<TItem> extends Control<ScoreListModel, ScoreListView> {

	private BasicIflMethodScoreHandler handler = new BasicIflMethodScoreHandler(null);

	private ActivityMonitorControl activityMonitor;

	private ScoreHistoryControl scoreHistory;

	private ContextBasedOptionCreatorControl contextBasedOptionCreator;

	private FilterControl filterControl;

	private DualListControl<?> dualListControl;

	private ArrayList<Sortable> previousSorts = new ArrayList<Sortable>();

	private ArrayList<Sortable> dualListArray = createSortingAttributes();

	private List<Comparator<Entry<IMethodDescription, Score>>> comparators = new ArrayList<>();

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


		dualListControl = new DualListControl<>();
		dualListControl.setView(new DualListView<>());
		dualListControl.setModel(new DualListModel(dualListArray));

		contextBasedOptionCreator = new ContextBasedOptionCreatorControl();
		contextBasedOptionCreator.setModel(new ContextBasedOptionCreatorModel());
		contextBasedOptionCreator.setView(new ContextBasedOptionCreatorView());
		this.addSubControl(contextBasedOptionCreator);

		this.addSubControl(activityMonitor);
		this.addSubControl(scoreHistory);
		this.addSubControl(filterControl);
		this.addSubControl(dualListControl);
		contextBasedOptionCreator.eventContextBasedFeedbackOption().add(optionSelectedListener);
		contextBasedOptionCreator.eventContextBasedOptionNeeded().add(contextBasedOptionProviderListener);
		getView().createOptionsMenu(handler.getProvidedOptions());
		getView().refreshScores(getModel().getScores());
		getModel().eventScoreUpdated().add(scoreUpdatedListener);
		getView().eventOptionSelected().add(optionSelectedListener);
		getView().eventCustomOptionSelected().add(customOptionSelectedListener);
		handler.eventScoreUpdated().add(scoreRecalculatedListener);
		handler.loadMethodsScoreMap(getModel().getRawScore());
		handler.eventHighLightRequested().add(highlightRequestListener);
		filters.add(hideUndefinedFilter);
		getView().eventNavigateToRequired().add(navigateToListener);
		getView().eventNavigateToContext().add(navigateToContextListener);
		getView().eventSelectionChanged().add(selectionChangedListener);
		getView().eventOpenDetailsRequired().add(openDetailsRequiredListener);
		getModel().eventScoreLoaded().add(scoreLoadedListener);

		getView().eventOpenFiltersPart().add(openFiltersPage);
		getView().eventOpenDualListPart().add(openDualListPage);

		filterControl.eventBooleanRuleAdded().add(newBooleanFilterAddedListener);
		filterControl.eventDoubleRuleAdded().add(newDoubleFilterAddedListener);
		filterControl.eventLastActionRuleAdded().add(newLastActionFilterAddedListener);
		filterControl.eventStringRuleAdded().add(newStringFilterAddedListener);
		filterControl.eventDeleteRules().add(filtersRemovedListener);

		filterControl.eventGetTopTenLimit().add(getTopTenLimitListener);

		dualListControl.eventAttributeListRefreshRequested().add(attributeListRefreshRequested);
		dualListControl.eventSortingListRefreshRequested().add(sortingListRefreshRequested);
		dualListControl.eventUpdateSorting().add(updateSorting);
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
		getView().eventNavigateToRequired().remove(navigateToListener);
		getView().eventNavigateToContext().remove(navigateToContextListener);
		getView().eventSelectionChanged().remove(selectionChangedListener);
		getView().eventOpenDetailsRequired().remove(openDetailsRequiredListener);
		getModel().eventScoreLoaded().remove(scoreLoadedListener);

		getView().eventOpenFiltersPart().remove(openFiltersPage);
		getView().eventOpenDualListPart().remove(openDualListPage);

		filterControl.eventBooleanRuleAdded().remove(newBooleanFilterAddedListener);
		filterControl.eventDoubleRuleAdded().remove(newDoubleFilterAddedListener);
		filterControl.eventLastActionRuleAdded().remove(newLastActionFilterAddedListener);
		filterControl.eventStringRuleAdded().remove(newStringFilterAddedListener);
		filterControl.eventDeleteRules().remove(filtersRemovedListener);

		filterControl.eventGetTopTenLimit().remove(getTopTenLimitListener);

		dualListControl.eventAttributeListRefreshRequested().remove(attributeListRefreshRequested);
		dualListControl.eventSortingListRefreshRequested().remove(sortingListRefreshRequested);
		dualListControl.eventUpdateSorting().remove(updateSorting);

		super.teardown();
		activityMonitor = null;
		scoreHistory = null;
	}

	IListener<IMethodDescription> openDetailsRequiredListener = event -> {
		try {
			new FeatureAccessor().openLink(EU.tryUnchecked(() -> new URL(event.getDetailsLink())));
		} catch (RuntimeException e) {
			MessageDialog.open(MessageDialog.ERROR, Display.getCurrent().getActiveShell(), "Error opening details",
					"The details link can not be opened. Please check if the CSV file provides a working details link.",
					SWT.NONE);
		}

	};

	private ArrayList<Sortable> createSortingAttributes(){
		ArrayList<Sortable> list = new ArrayList<>();
		List<String> domainList = Stream.of(SortingArg.values()).map(SortingArg::name).collect(Collectors.toList());
		for (String name : domainList) {
			list.add(new MethodSortingArg(name, Sortable.SortingDirection.Ascending));
		}
		return list;
	}
	
	private List<ScoreFilter> filters = new ArrayList<>();

	private HideUndefinedFilter hideUndefinedFilter = new HideUndefinedFilter(true);

	private Map<IMethodDescription, Score> filterForView(Map<IMethodDescription, Score> allScores) {
		Stream<Entry<IMethodDescription, Score>> filtered = allScores.entrySet().stream();
		Map<IMethodDescription, Score> toDisplay = new HashMap<>();
		for (ScoreFilter filter : filters) {
			filtered = filtered.filter(filter);
			Set<Entry<IMethodDescription, Score>> result = filtered.collect(Collectors.toSet());
			filterControl.setResultNumber(filter.getRule(), result.size());
			filtered = result.stream();
		}

		if (!previousSorts.isEmpty()) {

			for (Sortable sorting : previousSorts) {

				switch (sorting.getName()) {
				case "Score":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new ScoreComparator());
					} else {
						comparators.add(new ScoreComparator().reversed());
					}
					break;
				case "Name":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new NameComparator());
					} else {
						comparators.add(new NameComparator().reversed());
					}
					break;
				case "Signature":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new SignatureComparator());
					} else {
						comparators.add(new SignatureComparator().reversed());
					}
					break;
				case "ParentType":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new ParentTypeComparator());
					} else {
						comparators.add(new ParentTypeComparator().reversed());
					}
					break;
				case "Path":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new PathComparator());
					} else {
						comparators.add(new PathComparator().reversed());
					}
					break;
				case "ContextSize":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new ContextSizeComparator());
					} else {
						comparators.add(new ContextSizeComparator().reversed());
					}
					break;
				case "Position":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new PositionComparator());
					} else {
						comparators.add(new PositionComparator().reversed());
					}
					break;
				case "Interactivity":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new InteractivityComparator());
					} else {
						comparators.add(new InteractivityComparator().reversed());
					}
					break;
				case "LastAction":
					if (sorting.getSortingDirection().equals(Sortable.SortingDirection.Ascending)) {
						comparators.add(new LastActionComparator());
					} else {
						comparators.add(new LastActionComparator().reversed());
					}
					break;
				}
			}

			toDisplay = filtered.sorted((a, b) -> new ChainComparator(comparators).compare(a, b))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));

		} else {
			toDisplay = filtered.collect(Collectors.collectingAndThen(Collectors.toMap(Entry::getKey, Entry::getValue),
					Collections::unmodifiableMap));
		}

		return toDisplay;
	}

	private Sortable sorting;

	private IListener<List<Sortable>> updateSorting = event -> {
		if (event.isEmpty() == false) {
			comparators.clear();
			previousSorts.clear();
			for (Sortable argument : event) {
				sorting = argument;
				if (!sorting.getSortingDirection().equals(argument.getSortingDirection())) {
					sorting.setSortingDirection(argument.getSortingDirection());
				}
				previousSorts.add(sorting);
			}
		}

		refreshView();
	};
	
	private IListener<List<Sortable>> sortingListRefreshRequested = event -> {
		dualListControl.eventSortingListRefreshRequested();
	};

	private IListener<List<Sortable>> attributeListRefreshRequested = event -> {
		dualListControl.eventAttributeListRefreshRequested();
	};

	private IListener<EmptyEvent> openFiltersPage = event -> {
		filterControl.showFilterPart();
	};

	private IListener<EmptyEvent> openDualListPage = event -> {
		dualListControl.showDualListPart();
	};

	public void setHideUndefinedScores(Boolean status) {
		System.out.println("hiding undefined scores are requested to set: " + status);
		hideUndefinedFilter.setEnabled(status);
		refreshView();
	}

	private IListener<List<IMethodDescription>> highlightRequestListener = list -> {
		getView().highlightRequest(list);
	};

	private IListener<DoubleRule> newDoubleFilterAddedListener = rule -> {
		filters.add(new DoubleFilter(true, rule));
		refreshView();
	};

	private IListener<StringRule> newStringFilterAddedListener = rule -> {
		filters.add(new StringFilter(true, rule));
		refreshView();
	};

	private IListener<BooleanRule> newBooleanFilterAddedListener = rule -> {
		filters.add(new BooleanFilter(true, rule));
		refreshView();
	};

	private IListener<List<Rule>> filtersRemovedListener = rules -> {
		List<ScoreFilter> toBeRemoved = new ArrayList<>();
		for (ScoreFilter filter : filters) {
			for (Rule rule : rules) {
				if (rule == filter.getRule()) {
					if (rule instanceof SortRule) {
						sorting = null;
					}
					toBeRemoved.add(filter);
				}
			}
		}
		for (ScoreFilter filter : toBeRemoved) {
			filters.remove(filter);
		}
		refreshView();
	};

	private IListener<LastActionRule> newLastActionFilterAddedListener = rule -> {
		filters.add(new LastActionFilter(true, rule, scoreHistory));
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
		for (Option option : handler.getProvidedOptions()) {
			if (option.getId().equals("CONTEXT_BASED_OPTION")) {
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
			CustomInputDialog dialog = new CustomInputDialog(Display.getCurrent().getActiveShell(),
					"Terminal choice confirmation: " + event.getChoice().getTitle(),
					"You choose an option which will end this iFL session with a "
							+ (effect.isSuccessFul() ? "successful" : "unsuccessful") + " result.\n"
							+ "Please confim that you intend to mark the selected code elements by typing their name next to them in the text areas. Element names are case-sensitive.",
					getElementNames(event));
			if (dialog.open() == InputDialog.OK) {
				confirmed = true;
			} else {
				confirmed = false;
				activityMonitor
						.log(new AbortEvent(new UserFeedback(event.getChoice(), event.getSubjects(), event.getUser())));
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
		Optional<Defineable<Double>> min = rawScores.values().stream().filter(score -> score.isDefinit())
				.min(Comparator.comparing(score -> score.getValue()));
		Optional<Defineable<Double>> max = rawScores.values().stream().filter(score -> score.isDefinit())
				.max(Comparator.comparing(score -> score.getValue()));
		if (min.isPresent() && max.isPresent()) {
			// getView().setScoreFilter(min.get().getValue(), max.get().getValue());
			// filterControl.setScoreFilter(min.get().getValue(), max.get().getValue());
		}
		handler.loadMethodsScoreMap(rawScores);
		// TODO: history-saving-bug history should be saved here but we do not have the
		// cause, since this event come from the model,
		// which do not need to know about it.
		refreshView();
	};

	private IListener<MethodScoreHandler.ScoreUpdateArgs> scoreRecalculatedListener = event -> {
		Map<IMethodDescription, ScoreChange> changes = getModel().updateScore(event.getNewScores().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, i -> new Score(i.getValue()))));
		for (Entry<IMethodDescription, ScoreListModel.ScoreChange> entry : changes.entrySet()) {
			scoreHistory.store(entry.getValue().getNewScore(), entry.getValue().getOldScore(), entry.getKey(),
					event.getCause());
		}
		// TODO: this is a redundant refresh since the event listener of
		// scoreUpdatedListener already refreshed it,
		// but the history do not contains the monuments requested to display. Search
		// history-saving-bug for more.
		refreshView();
	};

	EditorAccessor editor = new EditorAccessor();

	private IListener<IMethodDescription> navigateToListener = event -> {
		editor.open(event.getLocation().getAbsolutePath(), event.getLocation().getBegining().getOffset());
		activityMonitor.log(new NavigationEvent(event));
	};

	private IListener<Entry<IMethodDescription, Score>> navigateToContextListener = entry -> {
		Map<IMethodDescription,Score> scores = getModel().getScores();
		for(MethodIdentity methodId : entry.getKey().getContext()) {
			for(IMethodDescription method : scores.keySet()) {
				if (methodId.equals(method.getId())) {
					editor.open(method.getLocation().getAbsolutePath(), method.getLocation().getBegining().getOffset());
					activityMonitor.log(new NavigationEvent(method));
				}
			}
		}
		/*for (MethodIdentity methodId : entry) {
			editor.open(method.getLocation().getAbsolutePath(), method.getLocation().getBegining().getOffset());
			activityMonitor.log(new NavigationEvent(method));
		}*/
	};

	private static final int TOP_SCORE_LIMIT = 9;

	private IListener<EmptyEvent> getTopTenLimitListener = event -> {
		Map<IMethodDescription, Defineable<Double>> rawScores = getModel().getRawScore().entrySet().stream()
				.sorted((a, b) -> -1 * a.getValue().compareTo(b.getValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		Optional<Defineable<Double>> min = rawScores.values().stream().filter(score -> score.isDefinit())
				.min(Comparator.comparing(score -> score.getValue()));
		Optional<Defineable<Double>> max = rawScores.values().stream().filter(score -> score.isDefinit())
				.max(Comparator.comparing(score -> score.getValue()));
		if (rawScores.size() > TOP_SCORE_LIMIT && min.isPresent() && max.isPresent()) {
			Defineable<Double> limit = rawScores.entrySet().stream().skip(TOP_SCORE_LIMIT).collect(Collectors.toList())
					.get(0).getValue();
			filterControl.applyTopScorePreset(limit.getValue());
		}
	};

	private IListener<EmptyEvent> scoreLoadedListener = __ -> {

		filterControl.enableFiltering();
		dualListControl.enableOrdering();
		/*
		MessageDialog.open(MessageDialog.INFORMATION, null, "iFL Score List",
				"Only the top 10 source code items are displayed.\n"
						+ "You can set the filters to show more or less items.",
				SWT.NONE);
		*/
	};

	private List<String> getElementNames(IUserFeedback event) {
		List<String> rvList = new ArrayList<String>(event.getSubjects().size());
		for (IMethodDescription method : event.getSubjects().keySet()) {
			rvList.add(method.getId().getName());
		}
		return rvList;
	}

	public void closeFilterPart() {
		filterControl.terminate();
		filterControl.close();
	}
	
	public void closeDualListPart() {
		dualListControl.terminate();
		dualListControl.close();
	}
}