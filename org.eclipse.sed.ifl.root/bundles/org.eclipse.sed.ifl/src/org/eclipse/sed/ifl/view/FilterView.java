package org.eclipse.sed.ifl.view;

import javax.inject.Inject;

import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.accessor.gui.PartAccessor;
import org.eclipse.sed.ifl.ide.gui.FilterPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartService;

public class FilterView extends View implements IEmbeddable, IEmbedee {
	
	private FilterPart filterPart;
	private IPartService service;
	
	public FilterView(PartAccessor partAccessor) {
		this.filterPart = (FilterPart) partAccessor.getPart(FilterPart.ID);
	}

	@Override
	public void init() {
		service = filterPart.getSite().getService(IPartService.class);
		
		filterPart.eventlowerScoreLimitChanged().add(lowerScoreLimitChangedListener);
		filterPart.eventlowerScoreLimitEnabled().add(lowerScoreLimitEnabledListener);
		filterPart.eventContextSizeLimitEnabled().add(contextSizeLimitEnabledListener);
		filterPart.eventContextSizeLimitChanged().add(contextSizeLimitChangedListener);
		filterPart.eventContextSizeRelationChanged().add(contextSizeRelationChangedListener);
		filterPart.eventNameFilterChanged().add(nameFilterChangedListener);
		filterPart.eventLimitRelationChanged().add(limitFilterRelationChangedListener);
		
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
		
		super.teardown();
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
		//TODO
	}
	
	public void close() {
		filterPart.getSite().getPage().hideView(filterPart);
	}
	
	public void setScoreFilter(double min, double max, double current) {
		filterPart.setScoreFilter(min, max, current);
	}
	
	public void setScoreFilter(double min, double max) {
		filterPart.setScoreFilter(min, max);
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
	
	public INonGenericListenerCollection<Boolean> eventcontextSizeLimitEnabled() {
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
	
	
}
