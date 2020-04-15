package org.eclipse.sed.ifl.model.user.interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.Projection;
import org.eclipse.sed.ifl.util.wrapper.Relativeable;

public class ScoreSetterModel extends EmptyModel {
	private Map<IMethodDescription, Projection<Double>> subjects;
	private Map<IMethodDescription, Defineable<Double>> originalSubjects;
	
	private Relativeable<Defineable<Double>> relativeableValue;
	
	public void setRelativeableValue(Relativeable<Defineable<Double>> relativeableValue) {
		this.relativeableValue = relativeableValue;
	}
	
	public Relativeable<Defineable<Double>> getRelativeableValue() {
		return this.relativeableValue;
	}
	
	public Map<IMethodDescription, Projection<Double>> getSubjects() {
		return subjects;
	}

	public Map<IMethodDescription, Defineable<Double>> getOriginalSubjects(){
		return this.originalSubjects;
	}
	
	public void setSubjects(Map<IMethodDescription, Defineable<Double>> scores) {
		this.originalSubjects = scores;
		this.subjects = scores.entrySet().stream()
			.filter(entry -> entry.getValue().isDefinit())
			.collect(Collectors.toMap(
				entry -> (IMethodDescription)entry.getKey(),
				entry -> new Projection<Double>(entry.getValue().getValue())));
		relatedChanged.invoke(new EmptyEvent());
	}

	public void updateSubjects(Map<IMethodDescription, Projection<Double>> subjects) {
		this.subjects = subjects;
		relatedChanged.invoke(new EmptyEvent());
	}
	
	public List<Projection<Double>> getProjection() {
		return new ArrayList<Projection<Double>>(subjects.values());
	}
	
	private NonGenericListenerCollection<EmptyEvent> relatedChanged = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<EmptyEvent> eventRelatedChanged() {
		return relatedChanged;
	}
}
