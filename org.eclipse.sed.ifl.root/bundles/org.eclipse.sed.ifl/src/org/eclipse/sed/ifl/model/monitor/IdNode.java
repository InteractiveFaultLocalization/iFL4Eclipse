package org.eclipse.sed.ifl.model.monitor;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.swt.widgets.Display;

public class IdNode extends Node {
	
	private String macAdress;
	private String userId;
	
	public IdNode(Map<String, Object> properties) {
		super(Maps.<String, Object>builder()
				.put("mac", determineMacAddress())
				.put("userID", getUserId())
				.build());
		this.macAdress = determineMacAddress();
		this.userId = getUserId();
	}
	
	@Override
	protected String getLabel() {
		// TODO Auto-generated method stub
		return "id";
	}
		
	private static String determineMacAddress() {
		byte[] macAddress = null;
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
			macAddress = ni.getHardwareAddress();
		} catch (UnknownHostException e) {
			System.out.println("Could not determine ip address\n");
		} catch (SocketException e) {
			System.out.println("Could not access Socket\n");
		}
		return new String(macAddress);
	}
	
	private static String getUserId() {
		String userId = null;
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
	            "Enter user ID", "Please enter your user ID.", "User ID", null);
	        if (dlg.open() == Window.OK) {
	          userId = dlg.getValue();
	        }
	     return userId;
	}
	
	@Override
	public String toString() {
		return String.format("Event [getType()=%s, getCreation()=%s, macAddress=%s, userId=%s]"
				,getType(), getCreation(), this.macAdress, this.userId);
	}
	
}
