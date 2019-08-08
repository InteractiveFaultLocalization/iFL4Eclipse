package org.eclipse.sed.ifl.view;

import java.util.List;

import org.eclipse.sed.ifl.ide.gui.dialogs.ContextBasedOptionCreatorDialog;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.widgets.Display;

public class ContextBasedOptionCreatorView extends View {

	private ContextBasedOptionCreatorDialog dialog;
	
	public ContextBasedOptionCreatorView(ScoreSetterView selected, ScoreSetterView context, ScoreSetterView other) {
		dialog = new ContextBasedOptionCreatorDialog(
			Display.getCurrent().getActiveShell(),
			selected, context, other);
	}
	
	public void display(
			List<Defineable<Double>> scoresOfSelected,
			List<Defineable<Double>> scoresOfContext,
			List<Defineable<Double>> scoresOfOthers) {
		dialog.setSelected(scoresOfSelected);
		dialog.setContext(scoresOfContext);
		dialog.setOthers(scoresOfOthers);
		dialog.open();
	}
}
