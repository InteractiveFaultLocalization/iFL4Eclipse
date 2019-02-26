package org.eclipse.sed.ifl.control.score;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.control.score.filter.HideUndefinedFilter;
import org.eclipse.sed.ifl.control.score.filter.ScoreFilter;
import org.eclipse.sed.ifl.core.BasicIflMethodScoreHandler;
import org.eclipse.sed.ifl.ide.accessor.source.EditorAccessor;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.NavigationEvent;
import org.eclipse.sed.ifl.model.monitor.event.UserFeedbackEvent;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.source.ICodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
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
		getView().eventSortRequired().add(sortListener);
		getView().eventNavigateToRequired().add(navigateToListener);
		getView().eventSelectionChanged().add(selectionChangedListener);
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
		super.teardown();
		activityMonitor = null;
	}

	//TODO: Yoda-mode :) split or move it to view
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
		handler.loadMethodsScoreMap(getModel().getRawScore());
		refreshView();
	}

	private List<ScoreFilter> filters = new ArrayList<>();
	private HideUndefinedFilter hideUndefinedFilter = new HideUndefinedFilter(false);
	
	private Map<IMethodDescription, Score> filterForView(Map<IMethodDescription, Score> allScores) {
		var filtered = allScores.entrySet().stream();
		for (var filter : filters) {
			filtered = filtered.filter(filter);
		}
		if (sorting != null) {
			switch (sorting) {
			case Score:
				return filtered
					.sorted((a, b) -> (sorting.isDescending() ? -1 : 1) * a.getValue().compareTo(b.getValue()))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
	
			default:
				return filtered
					.collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
			}
		} else {
			return filtered
				.collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
		}
	}

	public void setHideUndefinedScores(Boolean status) {
		System.out.println("hiding undefined scores are requested to set: " + status);
		hideUndefinedFilter.setEnabled(status);
		refreshView();
	}

	private void refreshView() {
		getView().refreshScores(filterForView(getModel().getScores()));
	}

	private IListener<List<IMethodDescription>> selectionChangedListener = event -> {
		List<MethodIdentity> context = new ArrayList<>();
		for (var item : event) {
			context.addAll(item.getContext());
		}
		getView().highlight(context);
	};

	private IListener<IUserFeedback> optionSelectedListener = event -> {
		var effect = event.getChoise().getSideEffect();
		if (effect == SideEffect.NOTHING) {
			handler.updateScore(event);
			activityMonitor.log(new UserFeedbackEvent(event));
		}
		else {
			boolean confirmed = false;
			for (var subject : event.getSubjects()) {
				String pass = subject.getId().getName();
				InputDialog dialog = new InputDialog(
					Display.getCurrent().getActiveShell(),
					"Terminal choice confirmation:" + event.getChoise().getTitle(), 
					"You choose an option which will end this iFL session with a "
					+ (effect.isSuccessFul()?"successful":"unsuccessful") + " result.\n"
					+ "Please confim that you intend to mark the selected code element '" + pass
					+ "', by typing its name bellow.", null, null);
				if (dialog.open() == InputDialog.OK && pass.equals(dialog.getValue())) {
					confirmed = true;
				}
				else {
					confirmed = false;
					break;
				}
			}
			if (confirmed) {
				//TODO
			}
		}
	};

	private IListener<EmptyEvent> scoreUpdatedListener = __ -> updateScore();
	
	private IListener<Map<IMethodDescription, Defineable<Double>>> scoreRecalculatedListener = getModel()::updateScore;
	
	private SortingArg sorting;
	
	private IListener<SortingArg> sortListener = event -> {
		sorting = event;
		refreshView();
	};
	
	EditorAccessor editor = new EditorAccessor();
	
	private IListener<ICodeChunkLocation> navigateToListener = event -> {
		editor.open(event.getAbsolutePath(), event.getBegining().getOffset());
		activityMonitor.log(new NavigationEvent(event));
	};
}
