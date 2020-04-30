package org.eclipse.sed.ifl.ide.accessor.gui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class PartAccessor {
	private ExecutionEvent event;
	
	public PartAccessor(ExecutionEvent event) {
		this.event = event;
	}
	
	public IViewPart getPart(String id){
		IWorkbenchWindow window = EU.tryUnchecked(() -> HandlerUtil.getActiveWorkbenchWindowChecked(event));
		//TODO: figure out why is it required to close the part and reopen it for a proper initialisation.
		IViewPart view = window.getActivePage().findView(id);
		if (view != null || window.getActivePage().isPartVisible(view)) {
			window.getActivePage().hideView(view);
		}
		EU.tryUnchecked(() -> window.getActivePage().showView(id));
		view = window.getActivePage().findView(id);
		if (view == null) {
			throw new RuntimeException();
		}
		else {
			return view;
		}
	}
	
	public void addListenerToAllPages(IPartListener2 eclipseListener) {
		IWorkbenchWindow window = EU.tryUnchecked(() -> HandlerUtil.getActiveWorkbenchWindowChecked(event));
        for (IWorkbenchPage page : window.getPages()) {
            page.addPartListener(eclipseListener);
        }
	}

	public void removeListenerToAllPages(IPartListener2 eclipseListener) {
		IWorkbenchWindow window;
		try {
			window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
	        for (IWorkbenchPage page : window.getPages()) {
	            page.removePartListener(eclipseListener);
	        }
		} catch (ExecutionException e) {
			//TODO: check if this assumption is correct: no need to unsubscribe of there is no active window.
			System.out.println("it seems Eclipse is closed... Unable to unsubscribe");
		}
	}
}
