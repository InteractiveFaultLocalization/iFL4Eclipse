package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.model.IModel;
import org.eclipse.sed.ifl.view.IView;
import org.eclipse.sed.ifl.view.ScoreRecalculatorView;

public class Control<TModel extends IModel, TView extends IView> extends ViewlessControl<TModel> implements IControl<TModel, TView> {

	private TView view;

	protected TView getView() {
		return view;
	}

	public void setView(TView view) {
		this.view = view;
	}
	
	public void setView(ScoreRecalculatorView scoreRecalculatorView) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void init() {
		//System.out.println("begin init of " + this.getClass().getName());
		super.init();
		if (view == null) {
			throw new ModelNotSetException();
		}
		view.init();
		//System.out.println("end init of " + this.getClass().getName());
	}
	
	@Override
	public void teardown() {
		super.teardown();
		view.teardown();
	}
}
