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
import org.eclipse.sed.ifl.model.monitor.event.ContextBasedUserFeedbackEvent;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionCreatorModel;
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
	
	//option példányt kap paraméterként -> visszaadja a beállított custom optiont
	public IUserFeedback createContextBasedUserFeedback(Option contextBasedOption) {
		
		Relativeable<Defineable<Double>> selectedValue = selectedSetter.relativeableValueProvider();
		Relativeable<Defineable<Double>> contextValue = contextSetter.relativeableValueProvider();
		Relativeable<Defineable<Double>> otherValue = otherSetter.relativeableValueProvider();
		
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> selectedFunction =
			 selectedValue.getValue().isDefinit() ? null : item -> contextBasedFeedbackValueSetter(selectedValue, item.getValue());
			
		
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> contextFunction =
			 contextValue.getValue().isDefinit() ? null : item -> contextBasedFeedbackValueSetter(contextValue, item.getValue());
		
					 
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> otherFunction =
			 otherValue.getValue().isDefinit() ? null : item -> contextBasedFeedbackValueSetter(otherValue, item.getValue());
			
			 
		ContextBasedOption customOption = new ContextBasedOption("CUSTOM_FEEDBACK",
				"Custom feedback",
				"Individually change the scores of selected, context and other items.",
				OptionKind.CUSTOM,
				selectedFunction,
				contextFunction,
				otherFunction);
		
		IUserFeedback feedback = new UserFeedback(customOption, selectedSetter.getOriginalSubjects());
		
		//TODO ScoreModificationEvent legyen
		//if-ek nem fognak kelleni, csak az új map-et kell összerakni
		//két userfeedback event között csak 1 scoremodificationevent lehet (de nem szükségszerû)
		if(!(selectedValue == null)) {
			activityMonitor.log(new ContextBasedUserFeedbackEvent(selectedSetter.getOriginalSubjects(), selectedValue, "selected"));
		}
		if(!(contextValue == null)) {
			activityMonitor.log(new ContextBasedUserFeedbackEvent(contextSetter.getOriginalSubjects(), contextValue, "context"));
		}
		if(!(otherValue == null)) {
			activityMonitor.log(new ContextBasedUserFeedbackEvent(otherSetter.getOriginalSubjects(), otherValue, "other"));
		}
		
		
		return feedback;
	}
	
	private Defineable<Double> contextBasedFeedbackValueSetter(Relativeable<Defineable<Double>> relativeableValue, Defineable<Double> previousValue) {
		Defineable<Double> newValue = new Defineable<Double>();

		if(relativeableValue.getValue().isDefinit()) {
			if(!relativeableValue.isRelative()) {
				newValue = relativeableValue.getValue();
			} else {
				double newDoubleValue = previousValue.getValue() + (previousValue.getValue() * (relativeableValue.getValue().getValue() * 0.01));
				if(newDoubleValue > 1.0) {
					newDoubleValue = 1.0;
				} else if(newDoubleValue < 0.0) {
					newDoubleValue = 0.0;
				}
				newValue = new Defineable<Double>(newDoubleValue);
			}
		}
		return newValue;
	}
		
	//TODO boolean helyett itt adja át a user választását
	private NonGenericListenerCollection<IUserFeedback> customFeedbackOption = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<IUserFeedback> eventCustomFeedbackOption() {
		return customFeedbackOption;
	}
	//TODO az eredeti listener kezelje le a ScoreListControlban
	private IListener<Boolean> customFeedbackOptionListener = event ->{
		//customFeedbackOption.invoke(createContextBasedUserFeedback());
	};
	
	private IListener<Boolean> refreshUiListener = event -> {
		selectedSetter.refreshUi();
		contextSetter.refreshUi();
		otherSetter.refreshUi();
	};
}
