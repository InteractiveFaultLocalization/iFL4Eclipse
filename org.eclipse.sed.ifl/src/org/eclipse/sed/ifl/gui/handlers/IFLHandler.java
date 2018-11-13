package org.eclipse.sed.ifl.gui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.sed.ifl.gui.views.IFLMainView;
import org.eclipse.sed.ifl.view.IView;

public class IFLHandler extends AbstractHandler {

	public IFLHandler() {
		System.out.println("handler");
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("handler.execute()");
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		//Activator.getDefault()
		//marker annotation spec
		try {
			window.getActivePage().showView(IFLMainView.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		for (IViewReference viewRef : window.getActivePage().getViewReferences()) {
			if (IFLMainView.ID.equals(viewRef.getId())) {
				IFLMainView view = (IFLMainView) viewRef.getView(false);
				//view.setMethodList(listOfMethods);
			}
		}
		return null;
	}
}
