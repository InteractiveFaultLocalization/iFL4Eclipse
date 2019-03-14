package org.eclipse.sed.ifl.ide.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.monitor.LogOnlyModeControl;
import org.eclipse.sed.ifl.control.monitor.PartMonitorControl;
import org.eclipse.sed.ifl.control.session.SessionControl;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.ide.accessor.gui.PartAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.WrongSelectionException;
import org.eclipse.sed.ifl.ide.gui.MainPart;
import org.eclipse.sed.ifl.model.monitor.LogOnlyModeModel;
import org.eclipse.sed.ifl.model.monitor.PartMonitorModel;
import org.eclipse.sed.ifl.model.session.SessionModel;
import org.eclipse.sed.ifl.view.SessionView;
import org.eclipse.swt.SWT;

public class startHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PartAccessor partAccessor = new PartAccessor(event);
			CodeEntityAccessor sourceAccessor = new CodeEntityAccessor(); 
			if (Activator.getDefault().isSessionActive()) {
				MessageDialog.open(MessageDialog.WARNING, null, "iFL session already active", "You already started an Interactive fault localization session. Consult with the iFL panel for further details.", SWT.NONE);			
			}
			else {
				if (isKeyFilePresent()) {
					try {
						IJavaProject selected = sourceAccessor.getSelectedProject();
						SessionControl session = new SessionControl(new SessionModel(), new SessionView((MainPart) partAccessor.getPart(MainPart.ID)), selected, new PartMonitorControl(new PartMonitorModel(), partAccessor));
						session.init(); 
						Activator.getDefault().setSession(session);
					} catch (WrongSelectionException e) {
						MessageDialog.open(MessageDialog.ERROR, null, "iFL", e.getMessage(), SWT.NONE);			
					}
				}
				else {
					if (!Activator.getDefault().isLogOnlyModeActive()) {
						MessageDialog.open(MessageDialog.INFORMATION, null, "iFL", "Log-only mode activated.", SWT.NONE);
						LogOnlyModeControl mode = new LogOnlyModeControl(new LogOnlyModeModel(), partAccessor);
						mode.init();
						Activator.getDefault().setLogOnlyMode(mode);
					}
					else {
						MessageDialog.open(MessageDialog.WARNING, null, "iFL", "You already activated log-only mode. You could continue your work as you usually do.", SWT.NONE);
					}
				}
			}
		} catch (Exception e) {
			MessageDialog.open(MessageDialog.ERROR, null, "Unexpected error during iFL initalization", e.getMessage(), SWT.NONE);
			e.printStackTrace();
			Activator.getDefault().setSession(null);
		}
		return null;
	}

	private boolean isKeyFilePresent() {
		// TODO Auto-generated method stub
		return false;
	}

}
