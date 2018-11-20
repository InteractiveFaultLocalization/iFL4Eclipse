package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.general.IDisposable;
import org.eclipse.swt.widgets.Composite;

public interface IView extends IDisposable {
	Composite getUI();
}
