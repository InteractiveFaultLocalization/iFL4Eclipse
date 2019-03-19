package org.eclipse.sed.ifl.control.score;

import java.net.URL;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;

import java.util.Comparator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.sed.ifl.view.ScoreListView;
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
		super.teardown();
		activityMonitor = null;
	}

	// TODO: Yoda-mode :) split or move it to view
	public enum ScoreStatus {
		NONE(null), INCREASED("icons/up32.png"), DECREASED("icons/down32.png"), UNDEFINED("icons/undefined32.png");

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
		for (ScoreFilter filter : filters) {
			filtered = filtered.filter(filter);
		}
		if (sorting != null) {
			switch (sorting) {
			case Score:
				return filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getValue().compareTo(b.getValue()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
			case Name:
				return filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getName().compareTo(b.getKey().getId().getName()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
			case Signature:
				return filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getSignature().compareTo(b.getKey().getId().getSignature()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
			case ParentType:
				return filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getId().getParentType().compareTo(b.getKey().getId().getParentType()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
			case Path:
				return filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getKey().getLocation().getAbsolutePath().compareTo(b.getKey().getLocation().getAbsolutePath()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
			case ContextSize:
				return filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * new Integer(a.getKey().getContext().size()).compareTo(b.getKey().getContext().size()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
			case Position:
				return filtered.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * new Integer(a.getKey().getLocation().getBegining().getOffset()).compareTo(b.getKey().getLocation().getBegining().getOffset()))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));

			default:
				return filtered.collect(Collectors.collectingAndThen(Collectors.toMap(Entry::getKey, Entry::getValue), Collections::unmodifiableMap));
			}
		} else {

			return filtered.collect(Collectors.collectingAndThen(Collectors.toMap(Entry::getKey, Entry::getValue), Collections::unmodifiableMap));
		}

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
		getView().refreshScores(filterForView(getModel().getScores()));
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
			for (IMethodDescription subject : event.getSubjects()) {
				String pass = subject.getId().getName();
				InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), "Terminal choice confirmation:" + event.getChoise().getTitle(),
						"You choose an option which will end this iFL session with a " + (effect.isSuccessFul() ? "successful" : "unsuccessful") + " result.\n"
								+ "Please confim that you intend to mark the selected code element '" + pass + "', by typing its name bellow.",
						"name of item", input -> pass.equals(input) ? null : "Type the name of the item or select cancel to abort.");
				if (dialog.open() == InputDialog.OK && pass.equals(dialog.getValue())) {
					confirmed = true;
				} else {
					confirmed = false;
					activityMonitor.log(new AbortEvent(new UserFeedback(event.getChoise(), Arrays.asList(subject), event.getUser())));
					break;
				}
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
}
