package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;

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
