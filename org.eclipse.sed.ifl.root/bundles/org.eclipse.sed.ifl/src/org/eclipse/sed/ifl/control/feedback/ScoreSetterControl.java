package org.eclipse.sed.ifl.control.feedback;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.user.interaction.ScoreSetterModel;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.ScoreSetterView;

public class ScoreSetterControl extends Control<ScoreSetterModel, ScoreSetterView> {

	private String name = "noname";

	public ScoreSetterControl(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public void init() {
		
		getView().setTitle(getName());
		getView().setDeltaPercent(0);
		
		super.init();
	}

	public void setCurrentRelatedScores(List<Defineable<Double>> scores) {
		getView().setDataPoints(
			scores.stream()
			.filter(entry -> entry.isDefinit())
			.map(entry -> entry.getValue())
			.collect(Collectors.toList()));
	}
}
