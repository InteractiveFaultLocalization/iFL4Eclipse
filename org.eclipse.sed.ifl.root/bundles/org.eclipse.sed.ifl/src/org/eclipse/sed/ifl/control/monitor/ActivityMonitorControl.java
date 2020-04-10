package org.eclipse.sed.ifl.control.monitor;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

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
				
				if(Activator.getDefault().getPreferenceStore().getBoolean("logKey")) {
				checkUserProvidedInformation();
				} else {
					enabled = false;
				}
				if (enabled) {
						getModel().insertEvent(event);
						System.out.printf("new %s are logged\n", event.toString());	
						showMissingInfo = true;
				} else {
					if(showMissingInfo)
					MessageDialog.open(MessageDialog.INFORMATION, null, "Unexpected error during logging",
							"You have disabled or have not provided one or more necessary information for logging. If you wish to use logging, "
							+ "open the iFL preference page, activate logging and provide the missing information.", SWT.NONE);
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
		enabled = checkUserId() && checkScenarioId() && checkHostAndPort() && checkGeneratedId();
	}

	public static void enable() {
		ActivityMonitorControl.enabled  = true;
	}

	//alapból mac
	//ha nincs mac, akkor ip
	//ha az se, akkor unknown
	private String determineMacAddress() {
		byte[] macAddressByte = null;
		try {
			InetAddress ip = InetAddress.getLocalHost();
			Activator.getDefault().getPreferenceStore().setValue("ipAddress", ip.toString());
			NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
			System.out.println("ip address: " + ip.toString());
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
		    Activator.getDefault().getPreferenceStore().setValue("macAddress", sb.toString());
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
		
		String scenarioId = Activator.getDefault().getPreferenceStore().getString("scenarioId");
		if (scenarioId.equals("")) {
			return false;
		 } else { 
			 getModel().setScenarioId(scenarioId);
			 return true;
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
	
	private boolean checkGeneratedId() {
		
		String generatedId = Activator.getDefault().getPreferenceStore().getString("generatedId");
		if (generatedId.equals("")) {
			return false;
		 } else { 
			 getModel().setGeneratedId(generatedId);
			 return true;
		 }
	}
}
