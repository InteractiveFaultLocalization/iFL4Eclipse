package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ScoreRecalculatorView extends View {


	public ScoreRecalculatorView() {
		
	}

	private NonGenericListenerCollection<String> recalculationSelected = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<String> eventrecalculationSelected() {
		return recalculationSelected;
	}

	public void select() {	
			recalculationSelected.invoke("Recalculation");
	}

}
