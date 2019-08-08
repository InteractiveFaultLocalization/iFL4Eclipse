package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.model.IModel;
import org.eclipse.sed.ifl.view.IView;

public class Control<TModel extends IModel, TView extends IView> extends ViewlessControl<TModel> implements IControl<TModel, TView> {

	private TView view;

	protected TView getView() {
		return view;
	}

	public void setView(TView view) {
		this.view = view;
	}
	
	@Override
	public void init() {
		super.init();
		if (view == null) {
			throw new ModelNotSetException();
		}
		view.init();
	}
	
	@Override
	public void teardown() {
		super.teardown();
		view.teardown();
	}
}
