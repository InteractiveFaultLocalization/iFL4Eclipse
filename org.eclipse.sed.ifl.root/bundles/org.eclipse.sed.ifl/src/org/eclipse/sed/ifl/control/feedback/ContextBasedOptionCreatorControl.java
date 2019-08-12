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

	ScoreSetterControl selected;
	
	ScoreSetterControl context;
	
	ScoreSetterControl other;
	
	@Override
	public void init() {
		ScoreSetterControl selected = new ScoreSetterControl("selected");
		selected.setModel(new ScoreSetterModel());
		ScoreSetterView selectedView = new ScoreSetterView();
		selected.setView(selectedView);
		getView().embed(selectedView);
		
		ScoreSetterControl context = new ScoreSetterControl("context");
		context.setModel(new ScoreSetterModel());
		ScoreSetterView contextView = new ScoreSetterView();
		context.setView(contextView);
		getView().embed(contextView);
		
		ScoreSetterControl other = new ScoreSetterControl("other");
		other.setModel(new ScoreSetterModel());
		ScoreSetterView otherView = new ScoreSetterView();
		other.setView(otherView);
		getView().embed(otherView);
		
		addSubControl(selected);
		addSubControl(context);
		addSubControl(other);
		super.init();
	}
	
	public void createNewOption(
			List<IMethodDescription> selected,
			List<IMethodDescription> context,
			List<IMethodDescription> others,
			Map<IMethodDescription, Defineable<Double>> all) {
		List<Defineable<Double>> scoresOfSelected = all.entrySet().stream()
		.filter(entry -> selected.contains(entry.getKey()))
		.map(entry -> entry.getValue())
		.collect(Collectors.toList());
		List<Defineable<Double>> scoresOfContext = all.entrySet().stream()
		.filter(entry -> context.contains(entry.getKey()))
		.map(entry -> entry.getValue())
		.collect(Collectors.toList());
		List<Defineable<Double>> scoresOfOthers = all.entrySet().stream()
		.filter(entry -> others.contains(entry.getKey()))
		.map(entry -> entry.getValue())
		.collect(Collectors.toList());
		getView().display(scoresOfSelected, scoresOfContext, scoresOfOthers);
	}
	
}
