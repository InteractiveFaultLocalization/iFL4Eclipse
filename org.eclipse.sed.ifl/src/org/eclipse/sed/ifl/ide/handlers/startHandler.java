package org.eclipse.sed.ifl.ide.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.sed.ifl.control.session.SessionControl;
import org.eclipse.sed.ifl.ide.gui.PartAccessor;
import org.eclipse.sed.ifl.model.session.SessionModel;
import org.eclipse.sed.ifl.view.MainPart;
import org.eclipse.sed.ifl.view.SessionView;

public class startHandler extends AbstractHandler {
	private SessionControl session; 
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PartAccessor accessor = new PartAccessor(event);
		session = new SessionControl(new SessionModel(), new SessionView((MainPart) accessor.getPart(MainPart.ID)));
		return null;
	}

}
