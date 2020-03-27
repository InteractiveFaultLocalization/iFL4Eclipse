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
import org.eclipse.sed.ifl.model.monitor.event.CustomUserFeedbackEvent;
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
	public IUserFeedback createCustomUserFeedback() {
		
		CustomValue selectedValue = selectedSetter.customValueProvider();
		CustomValue contextValue = contextSetter.customValueProvider();
		CustomValue otherValue = otherSetter.customValueProvider();
		
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> selectedFunction =
			 selectedValue == null ? null : item -> new Defineable<Double>(customFeedbackValueSetter(selectedValue, item.getValue().getValue()));
			
		
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> contextFunction =
			 contextValue == null ? null : item -> new Defineable<Double>(customFeedbackValueSetter(contextValue, item.getValue().getValue()));
		
					 
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> otherFunction =
			 otherValue == null ? null : item -> new Defineable<Double>(customFeedbackValueSetter(otherValue, item.getValue().getValue()));
			
			 
		CustomOption customOption = new CustomOption("CUSTOM_FEEDBACK",
				"Custom feedback",
				"Individually change the scores of selected, context and other items.",
				OptionKind.CUSTOM,
				selectedFunction,
				contextFunction,
				otherFunction);
		
		IUserFeedback feedback = new CustomUserFeedback(customOption, selectedSetter.getOriginalSubjects());
		
		if(!(selectedValue == null)) {
			activityMonitor.log(new CustomUserFeedbackEvent(selectedSetter.getOriginalSubjects(), selectedValue, "selected"));
		}
		if(!(contextValue == null)) {
			activityMonitor.log(new CustomUserFeedbackEvent(contextSetter.getOriginalSubjects(), contextValue, "context"));
		}
		if(!(otherValue == null)) {
			activityMonitor.log(new CustomUserFeedbackEvent(otherSetter.getOriginalSubjects(), otherValue, "other"));
		}
		
		
		return feedback;
	}
	
	private double customFeedbackValueSetter(CustomValue customValue, double previousValue) {
		System.out.println("custom value" + customValue.getValue());
		if(customValue.isAbsolute()) {
			System.out.println("absolute value: " + customValue.getValue());
			return customValue.getValue();
		} else {
			double newValue = previousValue + (previousValue * (customValue.getValue() * 0.01));
			if(newValue >= 1) {
				newValue = 1.0;
			}
			if(newValue <= 0) {
				newValue = 0.0;
			}
			System.out.println("scale value: " + newValue);
			return newValue;
		}
	}
		
	//TODO boolean helyett itt adja át a user választását
	private NonGenericListenerCollection<IUserFeedback> customFeedbackOption = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventCustomFeedbackOption() {
		return customFeedbackOption;
	}
	//TODO az eredeti listener kezelje le a ScoreListControlban
	private IListener<Boolean> customFeedbackOptionListener = event ->{
		customFeedbackOption.invoke(createCustomUserFeedback());
	};
	
	private IListener<Boolean> refreshUiListener = event -> {
		selectedSetter.refreshUi();
		contextSetter.refreshUi();
		otherSetter.refreshUi();
	};
}
