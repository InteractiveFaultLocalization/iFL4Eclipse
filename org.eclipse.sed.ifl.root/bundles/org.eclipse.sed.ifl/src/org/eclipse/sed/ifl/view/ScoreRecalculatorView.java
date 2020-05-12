package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ScoreRecalculatorView extends View {

	private FileDialog dialog;

	public ScoreRecalculatorView() {
		dialog = new FileDialog(new Shell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.csv" });
	}

	private NonGenericListenerCollection<String> fileSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<String> eventFileSelected() {
		return fileSelected;
	}

	public void select() {
		String path = dialog.open();
		if (path != null) {
			fileSelected.invoke(path);
		}
	}

}
