package org.eclipse.sed.ifl.control.monitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.tinkerpop.gremlin.process.remote.RemoteConnectionException;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.swt.SWT;
import org.osgi.service.prefs.Preferences;

public class ActivityMonitorControl extends ViewlessControl<ActivityMonitorModel> {

	private static boolean isUsed = false;
	private Preferences preferences;
	
	@Preference(value="logKey")
	boolean logKey;
	
	@Preference(value="userId")
	String userId;
	
	@Preference(value="scenarioId")
	String scenarioId;
	
	@Preference(value="host")
	String host;
	
	@Preference(value="port")
	String port;
	
	@Preference(value="generatedId")
	String generatedId;
	
	public ActivityMonitorControl(ActivityMonitorModel model) {
		super.setModel(model);
		this.preferences = InstanceScope.INSTANCE.getNode("org.sed.ifl");
	}

	private static Boolean showError = true;
	private static boolean enabled = false;
	private static boolean showMissingInfo = true;
	
	public void log(Event event) {
		if (!isUsed) {
			isUsed = true;
			try {				
				if(logKey) {
					System.out.println(determineMacAddress());
					checkUserProvidedInformation();
				} else {
					enabled = false;
				}
				if (enabled) {
					getModel().setMacAddress(determineMacAddress());
						getModel().insertEvent(event);
						System.out.printf("new %s are logged\n", event.toString());	
						showMissingInfo = true;
				} else {
					if(showMissingInfo) {
						MessageDialog.open(MessageDialog.INFORMATION, null, "Unexpected error during logging",
								"Some necessary information may not have been provided for logging, or the host may be unreachable. If you wish to use logging, "
								+ "open the iFL preference page, activate logging, provide the missing parameters, and make sure the provided host is reachable.", SWT.NONE);
						showMissingInfo = false;
					}
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
		enabled = checkUserId() && checkScenarioId() && checkHostAndPort() && checkGeneratedId();
	}

	public static void enable() {
		ActivityMonitorControl.enabled  = true;
	}

	private String determineMacAddress() {
		byte[] macAddressByte = null;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress("google.com", 80));
			preferences.put("ipAddress", socket.getLocalAddress().getHostAddress());
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
			macAddressByte = ni.getHardwareAddress();
			socket.close();
		} catch (UnknownHostException | NullPointerException e) {
			System.out.println("Could not determine ip address\n");
		} catch (SocketException e) {
			System.out.println("Could not access Socket\n");
		} catch (IOException e) {
			System.out.println("I/O exception occured while trying to get IP address.");
		}
		if(macAddressByte != null) {
			StringBuilder sb = new StringBuilder(18);
		    for (byte b : macAddressByte) {
		        if (sb.length() > 0)
		            sb.append('-');
		        sb.append(String.format("%02x", b));
		    }
		    preferences.put("macAddress", sb.toString());
		    return(sb.toString());
	    } else {
		return "Could not determine";
	    }
	}
	
	private boolean checkUserId() {
		if (userId.equals("")) {
			return false;
		 } else { 
			 getModel().setUserId(userId);
			 return true;
		 }
	}
	
	private boolean checkScenarioId() {
		if (scenarioId.equals("")) {
			return false;
		 } else { 
			 getModel().setScenarioId(scenarioId);
			 return true;
		 }
	}
	
	private boolean checkHostAndPort() {
		try {
			Integer.parseInt(port);
		} catch (NumberFormatException e){
			return false;
		}
		if (host.equals("") || port.equals("")) {
			return false;
		} else {
			getModel().setHostName(host);
			getModel().setPortNumber(port);
			try {
				getModel().setG();
			} catch (IllegalArgumentException e) {
				return false;
			}
			return true;
		}
	}
	
	private boolean checkGeneratedId() {
		if (generatedId.equals("")) {
			return false;
		 } else { 
			 getModel().setGeneratedId(generatedId);
			 return true;
		 }
	}
}
