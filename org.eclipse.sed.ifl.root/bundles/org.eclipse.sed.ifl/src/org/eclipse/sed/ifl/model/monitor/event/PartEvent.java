package org.eclipse.sed.ifl.model.monitor.event;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.sed.ifl.control.monitor.PartState;
import org.eclipse.sed.ifl.model.monitor.resource.Part;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.sed.ifl.util.exception.EU;

public class PartEvent extends Event {

	public PartEvent(MPart part, PartState state) {
		super(Maps.<String, Object>builder()
				.put("new-state", state.name())
				.unmodifiable(true).build());
		resources.put(new Part(pseudoIdOf(part), part.getElementId(), part.getDescription(), part.getTooltip()), "subject");
	}

	private String hash(String message) {
		MessageDigest digest = EU.tryUnchecked(() -> MessageDigest.getInstance("SHA-256"));
		byte[] encodedhash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(encodedhash);
	}

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private String pseudoIdOf(MPart part) {
		String data = part.getElementId() + part.getDescription() + part.getTooltip();
		return hash(data);
	}

}
