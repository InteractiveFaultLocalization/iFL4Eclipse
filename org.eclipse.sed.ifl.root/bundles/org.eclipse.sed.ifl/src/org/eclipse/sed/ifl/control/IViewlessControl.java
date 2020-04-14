package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.general.IDisposable;
import org.eclipse.sed.ifl.model.IModel;

public interface IViewlessControl<TModel extends IModel> extends IDisposable {
	void setModel(TModel model);
}
