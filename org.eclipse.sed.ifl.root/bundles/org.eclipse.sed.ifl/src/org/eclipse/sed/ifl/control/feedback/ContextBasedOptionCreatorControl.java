package org.eclipse.sed.ifl.control.feedback;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionCreatorModel;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.ContextBasedOptionCreatorView;

public class ContextBasedOptionCreatorControl extends Control<ContextBasedOptionCreatorModel, ContextBasedOptionCreatorView> {

	public ContextBasedOptionCreatorControl(ContextBasedOptionCreatorModel model, ContextBasedOptionCreatorView view) {
		super(model, view);
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
