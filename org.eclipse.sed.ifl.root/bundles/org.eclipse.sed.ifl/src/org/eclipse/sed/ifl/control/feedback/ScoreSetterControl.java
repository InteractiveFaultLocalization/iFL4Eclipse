package org.eclipse.sed.ifl.control.feedback;

import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.ScoreSetterModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.Projection;
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
		getView().eventDeltaPercentChanged().add(deltaPercentChangedListener);
		
		getModel().eventRelatedChanged().add(relatedChangeListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		getModel().eventRelatedChanged().remove(relatedChangeListener);
		super.teardown();
	}

	public void setCurrentRelatedScores(Map<IMethodDescription, Defineable<Double>> scores) {
		getModel().setSubjects(scores);
	}

	private IListener<EmptyEvent> relatedChangeListener = event -> {
		getView().displayCurrentScoreDistribution(getModel().getProjection());
	};

	private IListener<Integer> deltaPercentChangedListener = event -> {
		double ratio = event / 100.0;
		getModel().updateSubjects(getModel().getSubjects().entrySet().stream()
			.collect(Collectors.toMap(
				entry -> entry.getKey(),
				entry -> {
					Projection<Double> projected = new Projection<>(entry.getValue().getOriginal());
					double newValue = entry.getValue().getOriginal() + entry.getValue().getOriginal() * ratio;
					projected.setProjected(new Defineable<>(Math.max(0.0, Math.min(newValue, 1.0))));
					return projected;
				})));
	};
}
