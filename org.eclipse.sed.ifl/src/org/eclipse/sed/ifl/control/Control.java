package org.eclipse.sed.ifl.control;

import java.util.ArrayList;

import org.eclipse.sed.ifl.model.IModel;
import org.eclipse.sed.ifl.view.IView;

public class Control<TModel extends IModel, TView extends IView> implements IControl<TModel, TView> {
	public Control(TModel model, TView view) {
		this.view = view;
		this.model = model;
	}	
	
	private TView view;
	private TModel model;
	
	protected TView getView() {
		return view;
	}

	protected TModel getModel() {
		return model;
	}

	ArrayList<IControl<IModel, IView>> subControls = new ArrayList<>();
	
	protected Iterable<IControl<IModel, IView>> getSubControls() {
		return subControls;
	}
	
	protected void addSubControl(IControl<IModel, IView> control) {
		subControls.add(control);
	}
	
	@Override
	public void Init() {
		for (var control : subControls) {
			control.Init();
		}
	}

	@Override
	public void Teardown() {
		for (var control : subControls) {
			control.Teardown();
			subControls.remove(control);
		}			
	}

}
