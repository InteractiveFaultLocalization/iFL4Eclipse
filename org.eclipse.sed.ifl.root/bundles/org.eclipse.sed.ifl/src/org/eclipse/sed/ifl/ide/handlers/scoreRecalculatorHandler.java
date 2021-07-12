package org.eclipse.sed.ifl.ide.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.ScoreRecalculatorControl;
import org.eclipse.sed.ifl.ide.accessor.source.WrongSelectionException;
import org.eclipse.swt.SWT;

public class scoreRecalculatorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		try {
			ScoreRecalculatorControl SRC = new ScoreRecalculatorControl();
			SRC.recalculate();
		} catch (WrongSelectionException e) {
			MessageDialog.open(MessageDialog.ERROR, null, "Unsupported project type", e.getMessage(), SWT.NONE);			
		} catch (Exception e) {
			System.out.println("Coverage generation failed...");
			e.printStackTrace();
		}
		
		return null;
	}
}
