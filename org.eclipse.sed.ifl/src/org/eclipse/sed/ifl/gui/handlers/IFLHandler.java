package org.eclipse.sed.ifl.gui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.sed.ifl.gui.views.IFLMainView;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class IFLHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		try {
			window.getActivePage().showView(IFLMainView.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}
}
