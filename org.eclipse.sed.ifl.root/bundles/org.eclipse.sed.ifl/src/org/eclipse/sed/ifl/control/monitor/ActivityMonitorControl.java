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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.Activator;

import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.swt.SWT;


public class ActivityMonitorControl extends ViewlessControl<ActivityMonitorModel> {

	private static boolean isUsed = false;

	public ActivityMonitorControl(ActivityMonitorModel model) {
		super(model);
		model.setMacAddress(determineMacAddress());
	}

	private static Boolean showError = true;
	private static boolean enabled = false;
	private static boolean showMissingInfo = true;
	
	public void log(Event event) {
		if (!isUsed) {
			isUsed = true;
			try {
				checkUserProvidedInformation();
				if (enabled) {
						getModel().insertEvent(event);
						System.out.printf("new %s are logged\n", event.toString());	
						showMissingInfo = true;
				} else {
					if(showMissingInfo)
					MessageDialog.open(MessageDialog.INFORMATION, null, "Unexpected error during logging",
							"You have not provided one or more necessary information for logging. If you whish to use logging, "
							+ "open the iFL preference page and provide the missing information. Logging disabled.", SWT.NONE);
					showMissingInfo = false;
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

	private void checkUserProvidedInformation() {
		enabled = checkUserId() && checkScenarioId() && checkHostAndPort();
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
		    return(sb.toString());
	    } else {
		return "Could not determine";
	    }
	}
	
	private boolean checkUserId() {
		String userId = Activator.getDefault().getPreferenceStore().getString("userId");
		if (userId.equals("")) {
			return false;
		 } else { 
			 getModel().setUserId(userId);
			 return true;
		 }
	}
	
	private boolean checkScenarioId() {
		StringBuilder fileContentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get("ScenarioIdFile"), StandardCharsets.UTF_8)) 
	    {
	        stream.forEach(s -> fileContentBuilder.append(s).append("\n"));
		    getModel().setScenarioId(fileContentBuilder.toString());
		    return true;
	    }
	    catch (IOException e) 
	    {
	        System.out.println("Scenario ID file not found.");
	        return false;
	    }
	}
	
	private boolean checkHostAndPort() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String host = store.getString("host");
		String port = store.getString("port");
		if (host.equals("") || port.equals("")) {
			return false;
		} else {
			getModel().setHostName(host);
			getModel().setPortNumber(port);
			getModel().setG();
			return true;
		}
	}
	
}
