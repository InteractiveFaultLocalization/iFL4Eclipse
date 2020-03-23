package org.eclipse.sed.ifl.control.feedback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.CustomUserFeedbackEvent;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionCreatorModel;
import org.eclipse.sed.ifl.model.user.interaction.ScoreSetterModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.CustomValue;
import org.eclipse.sed.ifl.view.ContextBasedOptionCreatorView;
import org.eclipse.sed.ifl.view.ScoreSetterView;

public class ContextBasedOptionCreatorControl extends Control<ContextBasedOptionCreatorModel, ContextBasedOptionCreatorView> {

	private ScoreSetterControl selectedSetter;
	
	private ScoreSetterControl contextSetter;
	
	private ScoreSetterControl otherSetter;
	
	private List<IMethodDescription> selected;
	private List<IMethodDescription> context;
	private List<IMethodDescription> other;
	
	
	private ActivityMonitorControl activityMonitor;
	
	@Override
	public void init() {
		activityMonitor = new ActivityMonitorControl();
		activityMonitor.setModel(new ActivityMonitorModel());
		this.addSubControl(activityMonitor);
		selectedSetter = new ScoreSetterControl("selected");
		selectedSetter.setModel(new ScoreSetterModel());
		ScoreSetterView selectedView = new ScoreSetterView();
		selectedSetter.setView(selectedView);
		getView().embed(selectedView);
		
		contextSetter = new ScoreSetterControl("context");
		contextSetter.setModel(new ScoreSetterModel());
		ScoreSetterView contextView = new ScoreSetterView();
		contextSetter.setView(contextView);
		getView().embed(contextView);
		
		otherSetter = new ScoreSetterControl("other");
		otherSetter.setModel(new ScoreSetterModel());
		ScoreSetterView otherView = new ScoreSetterView();
		otherSetter.setView(otherView);
		getView().embed(otherView);
		
		addSubControl(selectedSetter);
		addSubControl(contextSetter);
		addSubControl(otherSetter);
		getView().eventCustomOptionDialog().add(customFeedbackOptionListener);
		getView().eventRefreshUi().add(refreshUiListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		getView().eventCustomOptionDialog().remove(customFeedbackOptionListener);
		getView().eventRefreshUi().remove(refreshUiListener);
		activityMonitor = null;
		super.teardown();
	}
	public void createNewOption(
			List<IMethodDescription> selected,
			List<IMethodDescription> context,
			List<IMethodDescription> other,
			Map<IMethodDescription, Defineable<Double>> all) {
		this.selected = selected;
		this.context = context;
		this.other = other;
		Map<IMethodDescription, Defineable<Double>> scoresOfSelected = all.entrySet().stream()
		.filter(entry -> selected.contains(entry.getKey()))
		.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		selectedSetter.setCurrentRelatedScores(scoresOfSelected);
		selectedSetter.setTableContents();
		
		Map<IMethodDescription, Defineable<Double>> scoresOfContext = all.entrySet().stream()
		.filter(entry -> context.contains(entry.getKey()))
		.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		contextSetter.setCurrentRelatedScores(scoresOfContext);
		contextSetter.setTableContents();
		
		Map<IMethodDescription, Defineable<Double>> scoresOfOther = all.entrySet().stream()
		.filter(entry -> other.contains(entry.getKey()))
		.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		otherSetter.setCurrentRelatedScores(scoresOfOther);
		otherSetter.setTableContents();
		
		getView().display();
	}
	
	public Map<IMethodDescription, Defineable<Double>> collectCustomUserFeedback(Map<IMethodDescription, Defineable<Double>> all) {
		Map<IMethodDescription, Defineable<Double>> changes = new HashMap<IMethodDescription, Defineable<Double>>();
		
		CustomValue selectedFeedback = selectedSetter.customValueProvider();
		CustomValue contextFeedback = contextSetter.customValueProvider();
		CustomValue otherFeedback = otherSetter.customValueProvider();
		
		if (selectedFeedback.isActive()) {
			Map<IMethodDescription, Defineable<Double>> selectedMap = all.entrySet().stream()
					.filter(entry -> selected.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> 
					new Defineable<Double>(customFeedbackValueSetter(entry.getValue().getValue(), selectedFeedback))));
		
			changes.putAll(selectedMap);
			activityMonitor.log(new CustomUserFeedbackEvent(selected, selectedFeedback, "selected"));
		}
		
		if (contextFeedback.isActive()) {
			Map<IMethodDescription, Defineable<Double>> contextMap = all.entrySet().stream()
					.filter(entry -> context.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> 
					new Defineable<Double>(customFeedbackValueSetter(entry.getValue().getValue(), contextFeedback))));
			changes.putAll(contextMap);
			activityMonitor.log(new CustomUserFeedbackEvent(context, contextFeedback, "context"));
		}
		
		if (otherFeedback.isActive()) {
			Map<IMethodDescription, Defineable<Double>> otherMap = all.entrySet().stream()
					.filter(entry -> other.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> 
					new Defineable<Double>(customFeedbackValueSetter(entry.getValue().getValue(), otherFeedback))));
			changes.putAll(otherMap);
			activityMonitor.log(new CustomUserFeedbackEvent(other, otherFeedback, "other"));
		}
		
		return changes;
	}
	
	private double customFeedbackValueSetter(double entry, CustomValue feedback) {
		double rValue = entry + (entry * (feedback.getScaleValue() * 0.01));
		if(feedback.isAbsoluteValue()) {
			return feedback.getAbsoluteValue();
		}
		if (rValue > 1) {
			return 1.0;
		} else if (rValue < 0) {
			return 0.0;
		}
		return rValue;
	}
	
	private NonGenericListenerCollection<Boolean> customFeedbackOption = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Boolean> eventCustomFeedbackOption() {
		return customFeedbackOption;
	}
	
	private IListener<Boolean> customFeedbackOptionListener = customFeedbackOption::invoke;
	
	private IListener<Boolean> refreshUiListener = event -> {
		selectedSetter.refreshUi();
		contextSetter.refreshUi();
		otherSetter.refreshUi();
	};
}
