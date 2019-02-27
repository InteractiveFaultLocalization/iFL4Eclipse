package org.eclipse.sed.ifl.model.monitor.resource;

import java.util.HashMap;
import java.util.Map;

public class Option extends Resource {

	public Option(String id, String message) {
		super(id, new HashMap<String, Object>() {
			{
				put("absolute_path", message);
			}
		});
	}

}
