package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ScoreLoaderView extends View {

	private FileDialog dialog;

	public ScoreLoaderView() {
		dialog = new FileDialog(new Shell(), SWT.OPEN);
	}
	
	private NonGenericListenerCollection<String> fileSelected = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventFileSelected() {
		return fileSelected;
	}
	
	private NonGenericListenerCollection<String> jsonFileSelected = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventJsonFileSelected() {
		return jsonFileSelected;
	}
	
	public void select() {
		dialog.setFilterExtensions(new String[] {"*.csv"});
		String path = dialog.open();
		if (path != null) {
			fileSelected.invoke(path);
		}
	}

	public void selectJson() {
		dialog.setFilterExtensions(new String[] {"*.json"});
		String path = dialog.open();
		if (path != null) {
			jsonFileSelected.invoke(path);
		}
	}
	
}
