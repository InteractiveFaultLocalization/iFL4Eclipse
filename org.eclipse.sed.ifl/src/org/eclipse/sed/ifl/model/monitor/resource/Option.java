package org.eclipse.sed.ifl.model.monitor.resource;

import java.util.Map;

public class Option extends Resource {

	public Option(String id, String message) {
		super(id, Map.of("message", message));
	}

}
