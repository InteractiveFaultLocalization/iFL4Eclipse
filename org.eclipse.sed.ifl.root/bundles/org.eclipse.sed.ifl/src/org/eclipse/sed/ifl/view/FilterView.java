package org.eclipse.sed.ifl.view;


import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.sed.ifl.control.score.filter.BooleanRule;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.LastActionRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.SortRule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.FilterPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Composite;

public class FilterView extends View implements IEmbeddable, IEmbedee {
	
	private FilterPart filterPart;
	
	@Inject
	EPartService partService;
	
	public FilterView() {
		this.filterPart = (FilterPart) partService.findPart("org.eclipse.sed.ifl.views.IFLFilterView");
	}
	
	@Override
	public void init() {

		initUIListeners();
		
		super.init();
	}
	
	@Override
	public void teardown() {
		
		removeUIListeners();
		
		super.teardown();
	}
	
	private void initUIListeners() {
		filterPart.eventBooleanRuleAdded().add(booleanRuleAddedListener);
		filterPart.eventDoubleRuleAdded().add(doubleRuleAddedListener);
		filterPart.eventLastActionRuleAdded().add(lastActionRuleAddedListener);
		filterPart.eventStringRuleAdded().add(stringRuleAddedListener);
		filterPart.eventDeleteRules().add(deleteRulesListener);
		filterPart.eventSortRuleAdded().add(sortListener);
		filterPart.eventGetTopTenLimit().add(getTopTenLimitListener);
	}
	
	private void removeUIListeners() {
		filterPart.eventBooleanRuleAdded().remove(booleanRuleAddedListener);
		filterPart.eventDoubleRuleAdded().remove(doubleRuleAddedListener);
		filterPart.eventLastActionRuleAdded().remove(lastActionRuleAddedListener);
		filterPart.eventStringRuleAdded().remove(stringRuleAddedListener);
		filterPart.eventDeleteRules().remove(deleteRulesListener);
		filterPart.eventSortRuleAdded().remove(sortListener);
		filterPart.eventGetTopTenLimit().remove(getTopTenLimitListener);
	}
	
	@Override
	public void embed(IEmbeddable embedded) {
		filterPart.embed(embedded);

	}

	@Override
	public void setParent(Composite parent) {
		filterPart.setParent(parent);

	}

	public void showFilterPart() {
		removeUIListeners();
		partService.showPart(partService.findPart("org.eclipse.sed.ifl.views.IFLFilterView"), PartState.ACTIVATE);
		initUIListeners();
	}
	
	public void close() {
		partService.hidePart(partService.findPart("org.eclipse.sed.ifl.views.IFLDualListView"));
	}
	
	public void applyTopScorePreset(Double limit) {
		filterPart.applyTopScorePreset(limit);
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
		filterPart.enableFiltering();
	}

	public void setResultNumber(Rule rule, int count) {
		filterPart.setResultNumber(rule, count);
		
	}

	public void terminatePart() {
		filterPart.terminate();
	}
}
