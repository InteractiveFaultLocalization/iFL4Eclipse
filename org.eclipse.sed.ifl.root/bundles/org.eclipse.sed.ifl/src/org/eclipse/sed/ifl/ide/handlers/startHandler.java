package org.eclipse.sed.ifl.ide.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.control.monitor.PartMonitorControl;
import org.eclipse.sed.ifl.control.session.SessionControl;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.ide.accessor.gui.PartAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.WrongSelectionException;
import org.eclipse.sed.ifl.ide.gui.dialogs.CustomWarningDialog;
import org.eclipse.sed.ifl.model.monitor.PartMonitorModel;
import org.eclipse.sed.ifl.model.session.SessionModel;
import org.eclipse.sed.ifl.view.SessionView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class startHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PartAccessor partAccessor = new PartAccessor(event);
			CodeEntityAccessor sourceAccessor = new CodeEntityAccessor(); 
			if (Activator.getDefault().isSessionActive()) {
				CustomWarningDialog dialog = new CustomWarningDialog(Display.getCurrent().getActiveShell(), "iFL session is already active", "You have already started an Interactive Fault Localization session. Check the iFL panel for further details.");
				dialog.open();
			}
			else {
				//enable or disable logging
				if (isLogKeyEnabled()) {
					ActivityMonitorControl.enable();
					MessageDialog.open(MessageDialog.INFORMATION, null, "iFL", "Activity log enabled.", SWT.NONE);
				} else {
					MessageDialog.open(MessageDialog.INFORMATION, null, "iFL", "Activity log disabled.", SWT.NONE);
				}
				try {
					IJavaProject selected = sourceAccessor.getSelectedProject();
					PartMonitorControl partMonitor = new PartMonitorControl(partAccessor);
					partMonitor.setModel(new PartMonitorModel());
					SessionControl session = new SessionControl(selected, partMonitor);
					session.setModel(new SessionModel());
					session.setView(new SessionView(partAccessor));
					session.init();
					Activator.getDefault().setSession(session);
				} catch (WrongSelectionException e) {
					MessageDialog.open(MessageDialog.ERROR, null, "Unsupported project type", e.getMessage(), SWT.NONE);			
				}
				//TODO: logging
				/*
				if (!Activator.getDefault().isLogOnlyModeActive()) {
					MessageDialog.open(MessageDialog.INFORMATION, null, "iFL", "Log-only mode activated.", SWT.NONE);
					LogOnlyModeControl mode = new LogOnlyModeControl(new LogOnlyModeModel(), partAccessor);
					mode.init();
					Activator.getDefault().setLogOnlyMode(mode);
				}
				else {	
					Activator.getDefault().getLogOnlyMode().logDenied();
					MessageDialog.open(MessageDialog.WARNING, null, "iFL", "You already activated log-only mode. You could continue your work as you usually do.", SWT.NONE);
				}*/
			}
		} catch (Exception e) {
			MessageDialog.open(MessageDialog.ERROR, null, "Unexpected error during iFL initalization", e.getMessage(), SWT.NONE);
			e.printStackTrace();
			Activator.getDefault().setSession(null);
		}
		return null;
	}
	
	private boolean isLogKeyEnabled() {
		return Activator.getDefault().getPreferenceStore().getBoolean("logKey");
	}
	
	//TODO: logging
	public boolean isKeyFilePresent(String fileName, String content) {
		File key = new File(fileName);
		System.out.println("looking for key file at: " + key.getAbsolutePath());
		if (key.canRead()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(key))) {
				List<String> lines = reader.lines().collect(Collectors.toList());
					if (!lines.isEmpty()) {
						return lines.get(0).equals(content);
					}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
