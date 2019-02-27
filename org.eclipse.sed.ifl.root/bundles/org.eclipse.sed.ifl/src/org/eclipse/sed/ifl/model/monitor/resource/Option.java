package org.eclipse.sed.ifl.model.monitor.resource;

import org.eclipse.sed.ifl.util.Maps;

public class Option extends Resource {

	public Option(String id, String message) {
		super(id, Maps.<String, Object>builder().put("message", message).unmodifiable(true).build());
	}

}
