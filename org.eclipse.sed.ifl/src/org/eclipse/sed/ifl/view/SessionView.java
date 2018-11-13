package org.eclipse.sed.ifl.view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

public class SessionView extends View {
	public static final String START_COMMAND_ID = "org.eclipse.sed.ifl.commands.startCommand";

	@Override
	public void init() {
		super.init();
		IHandlerService handlerService =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
		handlerService.activateHandler(START_COMMAND_ID, new AbstractHandler() {
			
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
}
