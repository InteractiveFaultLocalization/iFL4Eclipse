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
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.control.score.filter.HideUndefinedFilter;
import org.eclipse.sed.ifl.control.score.filter.LessOrEqualFilter;
import org.eclipse.sed.ifl.control.score.filter.ScoreFilter;
import org.eclipse.sed.ifl.core.BasicIflMethodScoreHandler;
import org.eclipse.sed.ifl.ide.accessor.gui.FeatureAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.EditorAccessor;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.AbortEvent;
import org.eclipse.sed.ifl.model.monitor.event.ConfirmEvent;
import org.eclipse.sed.ifl.model.monitor.event.NavigationEvent;
import org.eclipse.sed.ifl.model.monitor.event.SelectionChangedEvent;
import org.eclipse.sed.ifl.model.monitor.event.UserFeedbackEvent;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.CustomInputDialog;
import org.eclipse.sed.ifl.view.ScoreListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class ScoreListControl extends Control<ScoreListModel, ScoreListView> {

	private BasicIflMethodScoreHandler handler = new BasicIflMethodScoreHandler(null);

	private ActivityMonitorControl activityMonitor = new ActivityMonitorControl(new ActivityMonitorModel());

	public ScoreListControl(ScoreListModel model, ScoreListView view) {
		super(model, view);
	}

	@Override
	public void init() {
		this.addSubControl(activityMonitor);

		getView().refreshScores(getModel().getScores());
		getModel().eventScoreUpdated().add(scoreUpdatedListener);
		getView().createOptionsMenu(handler.getProvidedOptions());
		getView().eventOptionSelected().add(optionSelectedListener);
		handler.eventScoreUpdated().add(scoreRecalculatedListener);
		handler.loadMethodsScoreMap(getModel().getRawScore());
		filters.add(hideUndefinedFilter);
		filters.add(lessOrEqualFilter);
		getView().eventlowerScoreLimitChanged().add(lowerScoreLimitChangedListener);
		getView().eventlowerScoreLimitEnabled().add(lowerScoreLimitEnabledListener);
		getView().eventSortRequired().add(sortListener);
		getView().eventNavigateToRequired().add(navigateToListener);
		getView().eventSelectionChanged().add(selectionChangedListener);
		getView().eventOpenDetailsRequired().add(openDetailsRequiredListener);
		getModel().eventScoreLoaded().add(scoreLoadedListener);
		super.init();
	}

	@Override
	public void teardown() {
		getModel().eventScoreUpdated().remove(scoreUpdatedListener);
		getView().eventOptionSelected().remove(optionSelectedListener);
		handler.eventScoreUpdated().remove(scoreRecalculatedListener);
		getView().eventSortRequired().remove(sortListener);
		getView().eventNavigateToRequired().remove(navigateToListener);
		getView().eventSelectionChanged().remove(selectionChangedListener);
		getView().eventlowerScoreLimitChanged().remove(lowerScoreLimitChangedListener);
		getView().eventlowerScoreLimitEnabled().remove(lowerScoreLimitEnabledListener);
		getView().eventOpenDetailsRequired().remove(openDetailsRequiredListener);
		getModel().eventScoreLoaded().remove(scoreLoadedListener);
		super.teardown();
		activityMonitor = null;
	}

	// TODO: Yoda-mode :) split or move it to view
	public enum ScoreStatus {
		NONE(null), INCREASED("icons/up_arrow16.png"), DECREASED("icons/down_arrow16.png"), UNDEFINED("icons/undef16.png");

		private final String iconPath;

		ScoreStatus(String iconPath) {
			this.iconPath = iconPath;
		}

		public String getIconPath() {
			return iconPath;
		}
	}

	private void updateScore() {
		Map<IMethodDescription, Defineable<Double>> rawScores = getModel().getRawScore();
		Optional<Defineable<Double>> min = rawScores.values().stream().filter(score -> score.isDefinit()).min(Comparator.comparing(score -> score.getValue()));
		Optional<Defineable<Double>> max = rawScores.values().stream().filter(score -> score.isDefinit()).max(Comparator.comparing(score -> score.getValue()));
		if (min.isPresent() && max.isPresent()) {
			getView().setScoreFilter(min.get().getValue(), max.get().getValue());
		}
		handler.loadMethodsScoreMap(rawScores);
		refreshView();
	}
	
	IListener<IMethodDescription> openDetailsRequiredListener = event -> {
		new FeatureAccessor().openLink(EU.tryUnchecked(() -> new URL(event.getDetailsLink())));
	};

	private List<ScoreFilter> filters = new ArrayList<>();
	private HideUndefinedFilter hideUndefinedFilter = new HideUndefinedFilter(false);

	private LessOrEqualFilter lessOrEqualFilter = new LessOrEqualFilter(true);

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
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getName().compareTo(b.getKey().getId().getName()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case Signature:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getSignature().compareTo(b.getKey().getId().getSignature()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case ParentType:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getParentType().compareTo(b.getKey().getId().getParentType()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				break;
			case Path:
				toDisplay = filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getLocation().getAbsolutePath().compareTo(b.getKey().getLocation().getAbsolutePath()))
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

			default:
				toDisplay = filtered.collect(Collectors.collectingAndThen(Collectors.toMap(Entry::getKey, Entry::getValue), Collections::unmodifiableMap));
				break;
			}
		} else {
			toDisplay = filtered.collect(Collectors.collectingAndThen(Collectors.toMap(Entry::getKey, Entry::getValue), Collections::unmodifiableMap));
		}
		return toDisplay;
	}

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

	private void refreshView() {
		Map<IMethodDescription, Score> toDisplay = filterForView(getModel().getScores());
		getView().refreshScores(toDisplay);
		if (toDisplay.isEmpty()) {
			MessageDialog.open(
				MessageDialog.WARNING, null,
				"iFL Score List",
				"There are no source code items to display.\n"
				+ "Please check that you do not set the filters to hide all items.", SWT.NONE);
		}
	}

	private IListener<List<IMethodDescription>> selectionChangedListener = event -> {
		List<MethodIdentity> context = new ArrayList<>();
		for (IMethodDescription item : event) {
			context.addAll(item.getContext());
		}
		getView().highlight(context);
		activityMonitor.log(new SelectionChangedEvent(event));
	};

	private NonGenericListenerCollection<SideEffect> terminationRequested = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<SideEffect> eventTerminationRequested() {
		return terminationRequested;
	}

	private IListener<IUserFeedback> optionSelectedListener = event -> {
		SideEffect effect = event.getChoise().getSideEffect();
		if (effect == SideEffect.NOTHING) {
			handler.updateScore(event);
			activityMonitor.log(new UserFeedbackEvent(event));
		} else {
			boolean confirmed = false;
			String pass = "Finish session";
			CustomInputDialog dialog = new CustomInputDialog(Display.getCurrent().getActiveShell(), "Terminal choice confirmation:" + event.getChoise().getTitle(),
					"You choose an option which will end this iFL session with a " + (effect.isSuccessFul() ? "successful" : "unsuccessful") + " result.\n"
					+ "Please confim that you intend to mark the selected code elements by checking the boxes next to them and then typing \"Finish session\" in the text area.",
					"type here", input -> pass.equals(input) ? null : "Type \"Finish session\" and check all boxes or select cancel to abort.", event.getSubjects());
			if (dialog.open() == InputDialog.OK && pass.equals(dialog.getValue())) {
				confirmed = true;
			} else {
				confirmed = false;
				activityMonitor.log(new AbortEvent(new UserFeedback(event.getChoise(), event.getSubjects(), event.getUser())));
			}
			if (confirmed) {
				activityMonitor.log(new ConfirmEvent(event));
				terminationRequested.invoke(effect);
			}
		}
	};

	private IListener<EmptyEvent> scoreUpdatedListener = __ -> updateScore();

	private IListener<Map<IMethodDescription, Defineable<Double>>> scoreRecalculatedListener = event -> {
		getModel().updateScore(
			event.entrySet().stream()
			.collect(
				Collectors.toMap(
					Map.Entry::getKey,
					i -> new Score(i.getValue(), true))));
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
				getView().setScoreFilter(min.get().getValue(), max.get().getValue(), limit.getValue());
			}
			MessageDialog.open(
					MessageDialog.INFORMATION, null,
					"iFL Score List",
					"Only the top 10 source code items are displayed.\n"
					+ "You can set the filters to show more or less items.", SWT.NONE);
		}
	};
}
