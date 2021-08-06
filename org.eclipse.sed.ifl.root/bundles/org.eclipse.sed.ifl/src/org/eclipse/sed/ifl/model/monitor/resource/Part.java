package org.eclipse.sed.ifl.model.monitor.resource;

import org.eclipse.sed.ifl.util.Maps;

public class Part extends Resource {

	public Part(String id, String partId, String description, String tooltip) {
		super(id, Maps.<String, Object>builder()
			.put("part-id", partId)
			.put("description", description)
			.put("tooltip", tooltip)
			.unmodifiable(true).build());
	}

}
