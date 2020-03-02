package org.eclipse.sed.ifl.model.monitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.swt.widgets.Display;

public class IdNode extends Node {
	
	private static String macAddress;
	private static String userId;
	private static String fileId = null;
	
	public IdNode(Map<String, Object> properties) {
		super(Maps.<String, Object>builder()
				.put("mac", determineMacAddress())
				.put("userID", determineUserId())
				.put("fileID", determineFileId())
				.build());
	}
	
	@Override
	protected String getLabel() {
		// TODO Auto-generated method stub
		return "id";
	}
	
	public String getMacAddress() {
		return macAddress;
	}
		
	public String getUserId() {
		return userId;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	private static String determineMacAddress() {
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
		macAddress = new String(macAddressByte);
		return macAddress;
	}
	
	private static String determineUserId() {
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
	            "Enter user ID", "Please enter your user ID.", "User ID", null);
	        if (dlg.open() == Window.OK) {
	          userId = dlg.getValue();
	        }
	     return userId;
	}
	
	private static String determineFileId() {
		
		StringBuilder fileContentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get("IdFile"), StandardCharsets.UTF_8)) 
	    {
	        stream.forEach(s -> fileContentBuilder.append(s).append("\n"));
	    }
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	    fileId = fileContentBuilder.toString();
		
		return fileId;
	}
	
	@Override
	public String toString() {
		return String.format("Event [getType()=%s, getCreation()=%s, macAddress=%s, userId=%s]"
				,getType(), getCreation(), macAddress, userId);
	}

	
	
}
