package org.eclipse.sed.ifl.ide.accessor.gui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class PartAccessor {
	private ExecutionEvent event;
	
	public PartAccessor(ExecutionEvent event) {
		this.event = event;
	}
	
	public IViewPart getPart(String id){
		IWorkbenchWindow window = EU.tryUnchecked(() -> HandlerUtil.getActiveWorkbenchWindowChecked(event));
		EU.tryUnchecked(() -> window.getActivePage().showView(id));
		for (IViewReference viewRef : window.getActivePage().getViewReferences()) {
			if (id.equals(viewRef.getId())) {
				return viewRef.getView(false);
			}
		}
		throw new RuntimeException();
	}
}
