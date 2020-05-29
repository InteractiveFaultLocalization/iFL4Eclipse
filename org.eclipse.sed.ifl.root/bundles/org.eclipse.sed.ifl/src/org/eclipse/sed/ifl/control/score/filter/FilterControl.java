package org.eclipse.sed.ifl.control.score.filter;


import java.util.List;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.model.FilterModel;
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
		getView().eventBooleanRuleAdded().add(booleanRuleAddedListener);
		getView().eventDoubleRuleAdded().add(doubleRuleAddedListener);
		getView().eventLastActionRuleAdded().add(lastActionRuleAddedListener);
		getView().eventStringRuleAdded().add(stringRuleAddedListener);
		getView().eventSortRequired().add(sortListener);
		getView().eventDeleteRules().add(deleteRulesListener);
	}
	
	public void teardown() {
		getView().eventBooleanRuleAdded().remove(booleanRuleAddedListener);
		getView().eventDoubleRuleAdded().remove(doubleRuleAddedListener);
		getView().eventLastActionRuleAdded().remove(lastActionRuleAddedListener);
		getView().eventStringRuleAdded().remove(stringRuleAddedListener);
		getView().eventSortRequired().remove(sortListener);
		getView().eventDeleteRules().remove(deleteRulesListener);
		super.teardown();
	}
	
	
	public void close() {
		getView().close();
	}
	
	/*
	public void setScoreFilter(double min, double max, double current) {
		getView().setScoreFilter(min, max, current);
	}
	*/
	/*
	public void setScoreFilter(double min, double max) {
		getView().setScoreFilter(min, max);
	}
	*/
	
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
	
	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	private IListener<SortingArg> sortListener = sortRequired::invoke;

	public void resetFilterState() {
		//getView().resetFilterState();
		
	}

	public void enableFiltering() {
		getView().enableFiltering();
	}
	
}
