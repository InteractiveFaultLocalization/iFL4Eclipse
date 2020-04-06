package org.eclipse.sed.ifl.control.feedback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.ScoreModifiedEvent;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionCreatorModel;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionLambdaSetter;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOption;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.model.user.interaction.ScoreSetterModel;
import org.eclipse.sed.ifl.model.user.interaction.UserFeedback;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.Relativeable;
import org.eclipse.sed.ifl.view.ContextBasedOptionCreatorView;
import org.eclipse.sed.ifl.view.ScoreSetterView;

public class ContextBasedOptionCreatorControl extends Control<ContextBasedOptionCreatorModel, ContextBasedOptionCreatorView> {

	private ScoreSetterControl selectedSetter;
	
	private ScoreSetterControl contextSetter;
	
	private ScoreSetterControl otherSetter;
	
	private ActivityMonitorControl activityMonitor;
	
	private ContextBasedOptionLambdaSetter lambdaSetter;
	
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
		getView().eventCustomOptionDialog().add(contextBasedFeedbackOptionListener);
		getView().eventRefreshUi().add(refreshUiListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		getView().eventCustomOptionDialog().remove(contextBasedFeedbackOptionListener);
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
		selectedSetter.invokeRelativeableCollection();
		
		Map<IMethodDescription, Defineable<Double>> scoresOfContext = all.entrySet().stream()
		.filter(entry -> context.contains(entry.getKey()))
		.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		contextSetter.setCurrentRelatedScores(scoresOfContext);
		contextSetter.setTableContents();
		contextSetter.invokeRelativeableCollection();
		
		Map<IMethodDescription, Defineable<Double>> scoresOfOther = all.entrySet().stream()
		.filter(entry -> other.contains(entry.getKey()))
		.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		otherSetter.setCurrentRelatedScores(scoresOfOther);
		otherSetter.setTableContents();
		otherSetter.invokeRelativeableCollection();
		
		getView().display();
	}
	
	//option példányt kap paraméterként -> visszaadja a beállított custom optiont
	public void createContextBasedUserFeedback(Option option) {
		
		Relativeable<Defineable<Double>> selectedValue = selectedSetter.relativeableValueProvider();
		Relativeable<Defineable<Double>> contextValue = contextSetter.relativeableValueProvider();
		Relativeable<Defineable<Double>> otherValue = otherSetter.relativeableValueProvider();
		
		lambdaSetter = new ContextBasedOptionLambdaSetter(selectedValue);
		Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> selectedFunction =
		lambdaSetter.setLambda();
			
		lambdaSetter = new ContextBasedOptionLambdaSetter(contextValue);
		Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> contextFunction =
		lambdaSetter.setLambda();
		
		lambdaSetter = new ContextBasedOptionLambdaSetter(otherValue);
		Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> otherFunction =
		lambdaSetter.setLambda();
			 
		ContextBasedOption contextBasedOption = new ContextBasedOption(option.getId(),
			option.getTitle(),
			option.getDescription(),
			option.getKind(),
			selectedFunction,
			contextFunction,
			otherFunction);
		
		IUserFeedback feedback = new UserFeedback(contextBasedOption, selectedSetter.getOriginalSubjects());
		
		Map<Relativeable<Defineable<Double>>, Map<IMethodDescription, Defineable<Double>>> loggingMap = new HashMap<>();
		loggingMap.put(selectedValue, selectedSetter.getOriginalSubjects());
		loggingMap.put(contextValue, contextSetter.getOriginalSubjects());
		loggingMap.put(otherValue, otherSetter.getOriginalSubjects());
		
		activityMonitor.log(new ScoreModifiedEvent(loggingMap));
		contextBasedFeedbackOption.invoke(feedback);
		//return feedback;
	}
		
	private NonGenericListenerCollection<IUserFeedback> contextBasedFeedbackOption = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventContextBasedFeedbackOption() {
		return contextBasedFeedbackOption;
	}
	
	private NonGenericListenerCollection<Boolean> contextBasedOptionNeeded = new NonGenericListenerCollection<>();
	public INonGenericListenerCollection<Boolean> eventContextBasedOptionNeeded() {
		return contextBasedOptionNeeded;
	}
	private IListener<Boolean> contextBasedFeedbackOptionListener = contextBasedOptionNeeded::invoke;
	
	private NonGenericListenerCollection<Option> contextBasedOptionProvided = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Option> eventContextBasedOptionProvided() {
		return contextBasedOptionProvided;
	}
	
	private IListener<Boolean> refreshUiListener = event -> {
		selectedSetter.refreshUi();
		contextSetter.refreshUi();
		otherSetter.refreshUi();
	};
}
