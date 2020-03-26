package org.eclipse.sed.ifl.control.feedback;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionCreatorModel;
import org.eclipse.sed.ifl.model.user.interaction.CustomOption;
import org.eclipse.sed.ifl.model.user.interaction.CustomUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
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
	//TODO legyen event
	public IUserFeedback createCustomOption() {
		
		CustomValue selectedValue = selectedSetter.customValueProvider();
		CustomValue contextValue = contextSetter.customValueProvider();
		CustomValue otherValue = otherSetter.customValueProvider();
		
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> selectedFunction =
			 selectedValue == null ? null : item -> new Defineable<Double>(customFeedbackValueSetter(selectedValue, item.getValue().getValue()));
			//activityMonitor.log(new CustomUserFeedbackEvent(selected, selectedFeedback, "selected"));
		
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> contextFunction =
			 contextValue == null ? null : item -> new Defineable<Double>(customFeedbackValueSetter(contextValue, item.getValue().getValue()));
			//activityMonitor.log(new CustomUserFeedbackEvent(selected, selectedFeedback, "selected"));
					 
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> otherFunction =
			 otherValue == null ? null : item -> new Defineable<Double>(customFeedbackValueSetter(otherValue, item.getValue().getValue()));
			//activityMonitor.log(new CustomUserFeedbackEvent(selected, selectedFeedback, "selected"));
			 
		CustomOption customOption = new CustomOption("CUSTOM_FEEDBACK",
				"Custom feedback",
				"Individually change the scores of selected, context and other items.",
				OptionKind.CUSTOM,
				selectedFunction,
				contextFunction,
				otherFunction);
		
		IUserFeedback feedback = new CustomUserFeedback(customOption, selectedSetter.getOriginalSubjects());
		
		return feedback;
	}
	
	private double customFeedbackValueSetter(CustomValue customValue, double previousValue) {
		if(customValue.isAbsolute()) {
			return customValue.getValue();
		} else {
			return previousValue + (previousValue * (customValue.getValue() * 0.01));
		}
	}
		
	//TODO boolean helyett itt adja át a user választását
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
