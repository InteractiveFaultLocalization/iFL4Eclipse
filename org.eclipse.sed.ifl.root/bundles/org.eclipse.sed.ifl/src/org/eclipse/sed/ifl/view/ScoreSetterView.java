package org.eclipse.sed.ifl.view;

import java.util.List;

import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.ide.gui.element.ScoreSetter;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Composite;

public class ScoreSetterView extends View implements IEmbeddable, IDataPointsDisplayer {

	private ScoreSetter ui = new ScoreSetter();
	
	@Override
	public void init() {
		ui.eventDeltaPercentChanged().add(deltaPercentChangedListener);
		ui.eventAbsoluteScoreSetted().add(absoluteScoreSettedListener);
		ui.eventAbsoluteScoreSettingDisabled().add(absoluteScoreSettingDisabledListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		ui.eventDeltaPercentChanged().remove(deltaPercentChangedListener);
		ui.eventAbsoluteScoreSetted().remove(absoluteScoreSettedListener);
		ui.eventAbsoluteScoreSettingDisabled().remove(absoluteScoreSettingDisabledListener);
		super.teardown();
	}
	
	@Override
	public void setParent(Composite parent) {
		ui.setParent(parent);
	}

	public void setTitle(String name) {
		ui.setTitle(name);
	}

	@Override
	public void setDataPoints(List<Double> points) {
		ui.setDataPoints(points);
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
