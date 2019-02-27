package org.eclipse.sed.ifl.model.monitor.event;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.eclipse.sed.ifl.control.monitor.PartState;
import org.eclipse.sed.ifl.model.monitor.resource.Part;
import org.eclipse.sed.ifl.util.Maps;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.ui.IWorkbenchPartReference;

public class PartEvent extends Event {

	public PartEvent(IWorkbenchPartReference part, PartState state) {
		super(Maps.<String, Object>builder()
				.put("new-state", state.name())
				.put("part-id", part.getId())
				.put("title", part.getTitle())
				.put("tooltip", part.getTitleToolTip())
				.put("name", part.getPartName())
				.unmodifiable(true).build());
		resources.put(new Part(pseudoIdOf(part)), "subject");

		

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

	private String pseudoIdOf(IWorkbenchPartReference part) {
		String data = part.getId() + part.getPartName() + part.getTitle() + part.getTitleToolTip();
		return hash(data);
	}

}
