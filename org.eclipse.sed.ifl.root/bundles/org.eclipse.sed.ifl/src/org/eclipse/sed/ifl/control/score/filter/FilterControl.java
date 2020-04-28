package org.eclipse.sed.ifl.control.score.filter;

import java.util.Map;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.model.FilterModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.view.FilterView;

public class FilterControl extends Control<FilterModel, FilterView> {
	
	public void showFilterPart() {
		getView().showFilterPart();
	}
	
	public void init() {
		initUIListeners();
		super.init();
	}

	private void initUIListeners() {
		getView().eventlowerScoreLimitChanged().add(lowerScoreLimitChangedListener);
		getView().eventlowerScoreLimitEnabled().add(lowerScoreLimitEnabledListener);
		getView().eventContextSizeLimitEnabled().add(contextSizeLimitEnabledListener);
		getView().eventContextSizeLimitChanged().add(contextSizeLimitChangedListener);
		getView().eventContextSizeRelationChanged().add(contextSizeRelationChangedListener);
		getView().eventNameFilterChanged().add(nameFilterChangedListener);
		getView().eventLimitFilterRelationChanged().add(limitFilterRelationChangedListener);
		getView().eventSortRequired().add(sortListener);
	}
	
	public void teardown() {
		getView().eventlowerScoreLimitChanged().remove(lowerScoreLimitChangedListener);
		getView().eventlowerScoreLimitEnabled().remove(lowerScoreLimitEnabledListener);
		getView().eventContextSizeLimitEnabled().remove(contextSizeLimitEnabledListener);
		getView().eventContextSizeLimitChanged().remove(contextSizeLimitChangedListener);
		getView().eventContextSizeRelationChanged().remove(contextSizeRelationChangedListener);
		getView().eventNameFilterChanged().remove(nameFilterChangedListener);
		getView().eventLimitFilterRelationChanged().remove(limitFilterRelationChangedListener);
		getView().eventSortRequired().remove(sortListener);
		super.teardown();
	}
	
	
	public void close() {
		getView().close();
	}
	
	public void setScoreFilter(double min, double max, double current) {
		getView().setScoreFilter(min, max, current);
	}
	
	public void setScoreFilter(double min, double max) {
		getView().setScoreFilter(min, max);
	}
	
	private NonGenericListenerCollection<Double> lowerScoreLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Double> eventlowerScoreLimitChanged() {
		return lowerScoreLimitChanged;
	}
	
	private IListener<Double> lowerScoreLimitChangedListener = lowerScoreLimitChanged::invoke;

	private NonGenericListenerCollection<Boolean> lowerScoreLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventlowerScoreLimitEnabled() {
		return lowerScoreLimitEnabled;
	}
	
	private IListener<Boolean> lowerScoreLimitEnabledListener = lowerScoreLimitEnabled::invoke;
	
	private NonGenericListenerCollection<Boolean> contextSizeLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventContextSizeLimitEnabled() {
		return contextSizeLimitEnabled;
	}
	
	private IListener<Boolean> contextSizeLimitEnabledListener = contextSizeLimitEnabled::invoke;
	
	private NonGenericListenerCollection<Integer> contextSizeLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Integer> eventContextSizeLimitChanged() {
		return contextSizeLimitChanged;
	}
	
	private IListener<Integer> contextSizeLimitChangedListener = contextSizeLimitChanged::invoke;
	
	private NonGenericListenerCollection<String> contextSizeRelationChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventContextSizeRelationChanged() {
		return contextSizeRelationChanged;
	}
	
	private IListener<String> contextSizeRelationChangedListener = contextSizeRelationChanged::invoke;
	
	private NonGenericListenerCollection<String> limitFilterRelationChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventLimitFilterRelationChanged() {
		return limitFilterRelationChanged;
	}
	
	private IListener<String> limitFilterRelationChangedListener = limitFilterRelationChanged::invoke;
	
	private NonGenericListenerCollection<String> nameFilterChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventNameFilterChanged() {
		return nameFilterChanged;
	}
	
	private IListener<String> nameFilterChangedListener = nameFilterChanged::invoke;
	
	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	private IListener<SortingArg> sortListener = sortRequired::invoke;
	
}
