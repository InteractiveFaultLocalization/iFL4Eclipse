package org.eclipse.sed.ifl.ide.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.ScoreRecalculatorControl;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.WrongSelectionException;
import org.eclipse.swt.SWT;

public class scoreRecalculatorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		try {
			IJavaProject selectedProject = new CodeEntityAccessor().getSelectedProject();
			ScoreRecalculatorControl SRC = new ScoreRecalculatorControl(selectedProject);
			SRC.recalculate();
		} catch (WrongSelectionException e) {
			MessageDialog.open(MessageDialog.ERROR, null, "Unsupported project type", e.getMessage(), SWT.NONE);			
		} catch (Exception e) {
			MessageDialog.open(MessageDialog.ERROR, null, "Unexpected error during coverage generation", e.getMessage(), SWT.NONE);
			System.out.println("Unexpected error during coverage generation");
			e.printStackTrace();
		}
		
		return null;
	}
}
