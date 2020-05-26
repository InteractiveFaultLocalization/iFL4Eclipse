package org.eclipse.sed.ifl.view;


import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.FilterPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.sed.ifl.util.wrapper.FilterParams;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class FilterView extends View implements IEmbeddable, IEmbedee {
	
	private FilterPart filterPart;
	private FilterParams filterParam;
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

		filterPart.eventlowerScoreLimitChanged().add(lowerScoreLimitChangedListener);
		filterPart.eventlowerScoreLimitEnabled().add(lowerScoreLimitEnabledListener);
		filterPart.eventContextSizeLimitEnabled().add(contextSizeLimitEnabledListener);
		filterPart.eventContextSizeLimitChanged().add(contextSizeLimitChangedListener);
		filterPart.eventContextSizeRelationChanged().add(contextSizeRelationChangedListener);
		filterPart.eventNameFilterChanged().add(nameFilterChangedListener);
		filterPart.eventLimitRelationChanged().add(limitFilterRelationChangedListener);
		filterPart.eventSortRequired().add(sortListener);
		
		super.init();
	}
	
	@Override
	public void teardown() {
		
		filterPart.eventlowerScoreLimitChanged().remove(lowerScoreLimitChangedListener);
		filterPart.eventlowerScoreLimitEnabled().remove(lowerScoreLimitEnabledListener);
		filterPart.eventContextSizeLimitEnabled().remove(contextSizeLimitEnabledListener);
		filterPart.eventContextSizeLimitChanged().remove(contextSizeLimitChangedListener);
		filterPart.eventContextSizeRelationChanged().remove(contextSizeRelationChangedListener);
		filterPart.eventNameFilterChanged().remove(nameFilterChangedListener);
		filterPart.eventLimitRelationChanged().remove(limitFilterRelationChangedListener);
		filterPart.eventSortRequired().remove(sortListener);
		
		super.teardown();
	}
	
	private void initUIListeners() {
		filterPart.eventlowerScoreLimitChanged().add(lowerScoreLimitChangedListener);
		filterPart.eventlowerScoreLimitEnabled().add(lowerScoreLimitEnabledListener);
		filterPart.eventContextSizeLimitEnabled().add(contextSizeLimitEnabledListener);
		filterPart.eventContextSizeLimitChanged().add(contextSizeLimitChangedListener);
		filterPart.eventContextSizeRelationChanged().add(contextSizeRelationChangedListener);
		filterPart.eventNameFilterChanged().add(nameFilterChangedListener);
		filterPart.eventLimitRelationChanged().add(limitFilterRelationChangedListener);
		filterPart.eventSortRequired().add(sortListener);
	}
	
	private void removeUIListeners() {
		filterPart.eventlowerScoreLimitChanged().remove(lowerScoreLimitChangedListener);
		filterPart.eventlowerScoreLimitEnabled().remove(lowerScoreLimitEnabledListener);
		filterPart.eventContextSizeLimitEnabled().remove(contextSizeLimitEnabledListener);
		filterPart.eventContextSizeLimitChanged().remove(contextSizeLimitChangedListener);
		filterPart.eventContextSizeRelationChanged().remove(contextSizeRelationChangedListener);
		filterPart.eventNameFilterChanged().remove(nameFilterChangedListener);
		filterPart.eventLimitRelationChanged().remove(limitFilterRelationChangedListener);
		filterPart.eventSortRequired().remove(sortListener);
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
			setScoreFilter(filterParam.getMin(), filterParam.getMax(), filterParam.getCurrent());
		}
	}
	
	public void close() {
		if(filterPart.getSite().getPart() != null) {
			filterPart.getSite().getPage().hideView(filterPart);
		}
	}
	
	public void setScoreFilter(double min, double max, double current) {
		try {
			filterPart.setScoreFilter(min, max, current);
		} catch (SWTException e) {
			filterParam = new FilterParams(min, max, current);
			setScoreNeeded = true;
		}
	}
	
	public void setScoreFilter(double min, double max) {
		try {	
			filterPart.setScoreFilter(min, max);
		} catch (SWTException e) {
			filterParam = new FilterParams(min, max);
		}
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

	public void resetFilterState() {

	}
}
