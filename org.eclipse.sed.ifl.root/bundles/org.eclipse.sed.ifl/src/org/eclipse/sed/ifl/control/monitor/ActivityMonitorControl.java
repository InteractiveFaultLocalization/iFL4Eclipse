package org.eclipse.sed.ifl.control.monitor;

import org.apache.tinkerpop.gremlin.process.remote.RemoteConnectionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.swt.SWT;

public class ActivityMonitorControl extends ViewlessControl<ActivityMonitorModel> {

	public ActivityMonitorControl(ActivityMonitorModel model) {
		super(model);
	}
	
	private static Boolean showError = true;
	
	public void log(Event event) {
		synchronized (showError) {
/*		try {
			getModel().insertEvent(event);
			System.out.printf("new %s are logged\n", event.toString());
		}
		catch (IllegalStateException e) {
*/			if (showError) {// && e.getCause() instanceof RemoteConnectionException) {
				boolean answer = false;/*MessageDialog.open(
					MessageDialog.QUESTION, null,
					"Unexpected error during logging",
					"We are unable to log an event.\n"
					+ "Please notify iFL for Eclipse's developers.\n"
					+ "Details: " //+ e.getCause().getMessage() + "\n"
					+ "Do you whish to display this message again?",
					SWT.NONE);*/
				if (!answer) {
					MessageDialog.open(
						MessageDialog.INFORMATION, null,
						"Unexpected error during logging",
						"You will not get any more notice until you restart Eclipse.", SWT.NONE);
					showError = false;
				}
			}
	//	}
		}
	}
}
