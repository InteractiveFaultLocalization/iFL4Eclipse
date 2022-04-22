package org.eclipse.sed.ifl.control.score.filter;


import java.util.List;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.FilterModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
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
		getView().eventBooleanRuleAdded().add(booleanRuleAddedListener);
		getView().eventDoubleRuleAdded().add(doubleRuleAddedListener);
		getView().eventLastActionRuleAdded().add(lastActionRuleAddedListener);
		getView().eventStringRuleAdded().add(stringRuleAddedListener);
		getView().eventSortRequired().add(sortListener);
		getView().eventDeleteRules().add(deleteRulesListener);
		getView().eventGetTopTenLimit().add(getTopTenLimitListener);
	}
	
	public void teardown() {
		getView().eventBooleanRuleAdded().remove(booleanRuleAddedListener);
		getView().eventDoubleRuleAdded().remove(doubleRuleAddedListener);
		getView().eventLastActionRuleAdded().remove(lastActionRuleAddedListener);
		getView().eventStringRuleAdded().remove(stringRuleAddedListener);
		getView().eventSortRequired().remove(sortListener);
		getView().eventDeleteRules().remove(deleteRulesListener);
		getView().eventGetTopTenLimit().remove(getTopTenLimitListener);
		super.teardown();
	}
	
	
	public void close() {
		getView().close();
	}
	
	public void terminate() {
		getView().terminatePart();
	}
	
	public void applyTopScorePreset(Double limit) {
		getView().applyTopScorePreset(limit);
	}
	
	private NonGenericListenerCollection<List<Rule>> deleteRules = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<List<Rule>> eventDeleteRules() {
		return deleteRules;
	}
	
	private IListener<List<Rule>> deleteRulesListener = deleteRules::invoke;
	
	private NonGenericListenerCollection<DoubleRule> doubleRuleAdded = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<DoubleRule> eventDoubleRuleAdded() {
		return doubleRuleAdded;
	}
	
	private IListener<DoubleRule> doubleRuleAddedListener = doubleRuleAdded::invoke;
	
	private NonGenericListenerCollection<StringRule> stringRuleAdded = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<StringRule> eventStringRuleAdded() {
		return stringRuleAdded;
	}
	
	private IListener<StringRule> stringRuleAddedListener = stringRuleAdded::invoke;
	
	private NonGenericListenerCollection<BooleanRule> booleanRuleAdded = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<BooleanRule> eventBooleanRuleAdded() {
		return booleanRuleAdded;
	}
	
	private IListener<BooleanRule> booleanRuleAddedListener = booleanRuleAdded::invoke;
	
	private NonGenericListenerCollection<LastActionRule> lastActionRuleAdded = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<LastActionRule> eventLastActionRuleAdded() {
		return lastActionRuleAdded;
	}
	
	private IListener<LastActionRule> lastActionRuleAddedListener = lastActionRuleAdded::invoke;
	
	private NonGenericListenerCollection<SortRule> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortRule> eventSortRequired() {
		return sortRequired;
	}
	
	private IListener<SortRule> sortListener = sortRequired::invoke;
	
	private NonGenericListenerCollection<EmptyEvent> getTopTenLimit = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<EmptyEvent> eventGetTopTenLimit() {
		return getTopTenLimit;
	}
	
	private IListener<EmptyEvent> getTopTenLimitListener = getTopTenLimit::invoke;

	public void enableFiltering() {
		getView().enableFiltering();
	}

	public void setResultNumber(Rule rule, int count) {
		getView().setResultNumber(rule, count);
		
	}
	
}
