package org.eclipse.sed.ifl.view;

import java.util.Map;

import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.ide.gui.element.ScoreSetter;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.Projection;
import org.eclipse.sed.ifl.util.wrapper.Relativeable;
import org.eclipse.swt.widgets.Composite;

public class ScoreSetterView extends View implements IEmbeddable {

	private ScoreSetter ui = new ScoreSetter();
	
	public ScoreSetter getUi() {
		return ui;
	}

	@Override
	public void init() {
		ui.eventCollectRelativeableValue().add(relativeableValueSetListener);
		ui.eventDeltaPercentChanged().add(deltaPercentChangedListener);
		ui.eventAbsoluteScoreSetted().add(absoluteScoreSettedListener);
		ui.eventAbsoluteScoreSettingDisabled().add(absoluteScoreSettingDisabledListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		ui.eventCollectRelativeableValue().remove(relativeableValueSetListener);
		ui.eventDeltaPercentChanged().remove(deltaPercentChangedListener);
		ui.eventAbsoluteScoreSetted().remove(absoluteScoreSettedListener);
		ui.eventAbsoluteScoreSettingDisabled().remove(absoluteScoreSettingDisabledListener);
		super.teardown();
	}
	
	@Override
	public void setParent(Composite parent) {
		ui.setParent(parent);
	}

	public void refreshUi() {
		ui.refreshView();
	}
	
	public void setColumnTitle(String name) {
		ui.setColumnTitle(name);
	}
	
	public void setTitle(String name) {
		ui.setTitle(name);
	}

	public void displayCurrentScoreDistribution(Map<IMethodDescription ,Projection<Double>> points) {
		ui.displayCurrentScoreDistribution(points);
	}
	
	public void setTableContents(Map<IMethodDescription, Projection<Double>> subjects) {
		ui.setTableContents(subjects);
	}
	
	public void setDeltaPercent(int delta) {
		ui.displayDeltaPercent(delta);
	}
	
	private NonGenericListenerCollection<Integer> deltaPercentChanged = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventDeltaPercentChanged() {
		return deltaPercentChanged;
	}

	private IListener<Integer> deltaPercentChangedListener = event -> {
		ui.activatePreset(event);
		ui.displayDeltaPercent(event);
		deltaPercentChanged.invoke(event);
	};
	
	private NonGenericListenerCollection<Relativeable<Defineable<Double>>> relativeableValueSet = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Relativeable<Defineable<Double>>> eventRelativeableValueSet() {
		return relativeableValueSet;
	}
	
	private IListener<Relativeable<Defineable<Double>>> relativeableValueSetListener = relativeableValue -> {
		relativeableValueSet.invoke(relativeableValue);
	};
	
	private NonGenericListenerCollection<Double> absoluteScoreSetted = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Double> eventAbsoluteScoreSetted() {
		return absoluteScoreSetted;
	}
	
	private IListener<Double> absoluteScoreSettedListener = event -> {
		ui.disableRelativeScoreSetting();
		absoluteScoreSetted.invoke(event);
	};
	
	private IListener<EmptyEvent> absoluteScoreSettingDisabledListener = event -> ui.enableRelativeScoreSetting();
}
