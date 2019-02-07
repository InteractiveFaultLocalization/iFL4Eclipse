package org.eclipse.sed.ifl.control;

import java.util.ArrayList;

import org.eclipse.sed.ifl.model.IModel;

public class ViewlessControl<TModel extends IModel> implements IViewlessControl<TModel> {
	public ViewlessControl(TModel model) {
		this.model = model;
	}	
	
	private TModel model;
	
	protected TModel getModel() {
		return model;
	}

	private ArrayList<IViewlessControl<? extends IModel>> subControls = new ArrayList<>();
	
	protected Iterable<IViewlessControl<? extends IModel>> getSubControls() {
		return subControls;
	}
	
	protected void addSubControl(IViewlessControl<? extends IModel> control) {
		subControls.add(control);
	}
	
	@Override
	public void init() {
		for (var control : subControls) {
			control.init();
		}
		model.init();
	}

	@Override
	public void teardown() {
		for (var control : subControls) {
			control.teardown();
		}
		subControls.clear();
		model.teardown();
	}
}
