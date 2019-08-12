package org.eclipse.sed.ifl.view;

import java.util.List;

import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.dialogs.ContextBasedOptionCreatorDialog;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.widgets.Display;

public class ContextBasedOptionCreatorView extends View implements IEmbedee {

	private ContextBasedOptionCreatorDialog dialog = new ContextBasedOptionCreatorDialog(Display.getCurrent().getActiveShell());
	
	public void display(
			List<Defineable<Double>> scoresOfSelected,
			List<Defineable<Double>> scoresOfContext,
			List<Defineable<Double>> scoresOfOthers) {
		dialog.setSelected(scoresOfSelected);
		dialog.setContext(scoresOfContext);
		dialog.setOthers(scoresOfOthers);
		dialog.open();
	}

	@Override
	public void embed(IEmbeddable embedded) {
		dialog.embed(embedded);
	}
}
