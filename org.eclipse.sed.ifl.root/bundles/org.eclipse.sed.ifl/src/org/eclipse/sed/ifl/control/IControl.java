package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.general.IDisposable;
import org.eclipse.sed.ifl.model.IModel;
import org.eclipse.sed.ifl.view.IView;

public interface IControl<TModel extends IModel, TView extends IView> extends IDisposable, IViewlessControl<TModel>{
	void setView(TView view);
}
