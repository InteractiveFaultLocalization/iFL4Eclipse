package org.eclipse.sed.ifl.model.monitor.resource;

import org.eclipse.sed.ifl.util.Maps;

public class Part extends Resource {

	public Part(String id, String partId, String name, String title, String tooltip) {
		super(id, Maps.<String, Object>builder()
			.put("part-id", partId)
			.put("title", title)
			.put("tooltip", tooltip)
			.put("name", name)
			.unmodifiable(true).build());
	}

}
