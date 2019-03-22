package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.model.IModel;
import org.eclipse.sed.ifl.view.IView;

public class Control<TModel extends IModel, TView extends IView> extends ViewlessControl<TModel> implements IControl<TModel, TView> {

	private TView view;

	protected TView getView() {
		return view;
	}

	public Control(TModel model, TView view) {
		super(model);
		this.view = view;
	}
	
	@Override
	public void init() {
		super.init();
		view.init();
	}
	
	@Override
	public void teardown() {
		super.teardown();
		view.teardown();
	}
}
