package org.eclipse.sed.ifl.view;


import java.util.List;

import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.control.score.filter.BooleanRule;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.LastActionRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.FilterPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class FilterView extends View implements IEmbeddable, IEmbedee {
	
	private FilterPart filterPart;
	//private FilterParams filterParam;
	private Boolean setScoreNeeded = false;
	
	public FilterView() {
		this.filterPart = (FilterPart) getPart();
	}

	private IViewPart getPart() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(FilterPart.ID);
		
		if (view != null || page.isPartVisible(view)) {
			page.hideView(view);
		}
		EU.tryUnchecked(() -> page.showView(FilterPart.ID));
		view = page.findView(FilterPart.ID);
		if (view == null) {
			throw new RuntimeException();
		}
		else {
			return view;
		}
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
	}
	
	private void removeUIListeners() {
		filterPart.eventBooleanRuleAdded().remove(booleanRuleAddedListener);
		filterPart.eventDoubleRuleAdded().remove(doubleRuleAddedListener);
		filterPart.eventLastActionRuleAdded().remove(lastActionRuleAddedListener);
		filterPart.eventStringRuleAdded().remove(stringRuleAddedListener);
		filterPart.eventDeleteRules().remove(deleteRulesListener);
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
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			this.filterPart = (FilterPart) page.showView(FilterPart.ID);
		} catch (PartInitException e) {
			System.out.println("Could not open filters view.");
		}
		initUIListeners();
		if(setScoreNeeded) {
			//setScoreFilter(filterParam.getMin(), filterParam.getMax(), filterParam.getCurrent());
		}
	}
	
	public void close() {
		if(filterPart.getSite().getPart() != null) {
			filterPart.getSite().getPage().hideView(filterPart);
		}
	}
	
	/*
	public void setScoreFilter(double min, double max, double current) {
		try {
			filterPart.setScoreFilter(min, max, current);
		} catch (SWTException e) {
			filterParam = new FilterParams(min, max, current);
			setScoreNeeded = true;
		}
	}
	*/
	
	/*
	public void setScoreFilter(double min, double max) {
		try {	
			filterPart.setScoreFilter(min, max);
		} catch (SWTException e) {
			filterParam = new FilterParams(min, max);
		}
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

	public void enableFiltering() {
		filterPart.enableFiltering();
	}

	public void setResultNumber(Rule rule, int count) {
		filterPart.setResultNumber(rule, count);
		
	}
	
	//private IListener<SortingArg> sortListener = sortRequired::invoke;

}
