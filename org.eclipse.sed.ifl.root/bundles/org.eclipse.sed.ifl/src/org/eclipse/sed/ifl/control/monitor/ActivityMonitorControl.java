package org.eclipse.sed.ifl.control.monitor;

import org.apache.tinkerpop.gremlin.process.remote.RemoteConnectionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.swt.SWT;

public class ActivityMonitorControl extends ViewlessControl<ActivityMonitorModel> {

	private static boolean isUsed = false;

	public ActivityMonitorControl(ActivityMonitorModel model) {
		super(model);
	}

	private static Boolean showError = true;
	private static boolean enabled = false;

	public void log(Event event) {
		if (!isUsed) {
			isUsed = true;
			System.out.println("event: " + event.toString());
			try {
				if (enabled) {
					getModel().insertEvent(event);
					System.out.printf("new %s are logged\n", event.toString());
				}
			} catch (IllegalStateException e) {
				if (showError && e.getCause() instanceof RemoteConnectionException) {
					boolean answer = MessageDialog.open(MessageDialog.QUESTION, null, "Unexpected error during logging",
							"We are unable to log an event.\n" + "Please notify iFL for Eclipse's developers.\n"
									+ "Details: " + e.getCause().getMessage() + "\n"
									+ "Do you whish to display this message again?",
							SWT.NONE);
					if (!answer) {
						MessageDialog.open(MessageDialog.INFORMATION, null, "Unexpected error during logging",
								"You will not get any more notice until you restart Eclipse.", SWT.NONE);
						showError = false;
					}
				}
			}
			isUsed = false;
		}
	}

	public static void enable() {
		ActivityMonitorControl.enabled  = true;
	}

}
