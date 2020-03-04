package org.eclipse.sed.ifl.control.monitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.process.remote.RemoteConnectionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class ActivityMonitorControl extends ViewlessControl<ActivityMonitorModel> {

	private static boolean isUsed = false;

	public ActivityMonitorControl(ActivityMonitorModel model) {
		super(model);
		model.setMacAddress(determineMacAddress());
		model.setUserId(determineUserId());
		model.setScenarioId(determineScenarioId());
		System.out.println("mac: " + model.getMacAddress() + ", user id: " + model.getUserId() + ", scenario id: " + model.getScenarioId());
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

	private String determineMacAddress() {
		byte[] macAddressByte = null;
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
			macAddressByte = ni.getHardwareAddress();
		} catch (UnknownHostException e) {
			System.out.println("Could not determine ip address\n");
		} catch (SocketException e) {
			System.out.println("Could not access Socket\n");
		}
		if(macAddressByte != null) {
			StringBuilder sb = new StringBuilder(18);
		    for (byte b : macAddressByte) {
		        if (sb.length() > 0)
		            sb.append('-');
		        sb.append(String.format("%02x", b));
		    }
		    return sb.toString();
	    }
		return null;
	}
	
	private String determineUserId() {
		String userId = Activator.getDefault().getPreferenceStore().getString("userId");
		if (userId.equals("")) {
			InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
		            "Enter user ID", "Please enter your user ID.", "User ID", null);
		        if (dlg.open() == Window.OK) {
		        	userId = dlg.getValue();
		        	Activator.getDefault().getPreferenceStore().setValue("userId", userId);
		        }
		}
		return userId;
	}
	
	private String determineScenarioId() {
		StringBuilder fileContentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get("IdFile"), StandardCharsets.UTF_8)) 
	    {
	        stream.forEach(s -> fileContentBuilder.append(s).append("\n"));
	    }
	    catch (IOException e) 
	    {
	        System.out.println("Scenario ID file not found.");
	    }
	    return fileContentBuilder.toString();
	}
	
}
