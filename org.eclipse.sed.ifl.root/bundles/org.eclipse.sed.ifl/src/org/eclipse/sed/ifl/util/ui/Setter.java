package org.eclipse.sed.ifl.util.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class Setter {
	public static void RecursiveEnable(Control control, Boolean enabled) {
		control.setEnabled(enabled);
		if (control instanceof Composite) {
			for (Control child : ((Composite)control).getChildren()) {
				RecursiveEnable(child, enabled);
			}
		}
	}
}
