package org.eclipse.sed.ifl.control.feedback;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionCreatorModel;
import org.eclipse.sed.ifl.model.user.interaction.ScoreSetterModel;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.ContextBasedOptionCreatorView;
import org.eclipse.sed.ifl.view.ScoreSetterView;

public class ContextBasedOptionCreatorControl extends Control<ContextBasedOptionCreatorModel, ContextBasedOptionCreatorView> {

	private ScoreSetterControl selectedSetter;
	
	private ScoreSetterControl contextSetter;
	
	private ScoreSetterControl otherSetter;
	
	@Override
	public void init() {
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
		super.init();
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
		
		Map<IMethodDescription, Defineable<Double>> scoresOfContext = all.entrySet().stream()
		.filter(entry -> context.contains(entry.getKey()))
		.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		contextSetter.setCurrentRelatedScores(scoresOfContext);
		
		Map<IMethodDescription, Defineable<Double>> scoresOfOther = all.entrySet().stream()
		.filter(entry -> other.contains(entry.getKey()))
		.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		otherSetter.setCurrentRelatedScores(scoresOfOther);
		
		getView().display();
	}
	
}
