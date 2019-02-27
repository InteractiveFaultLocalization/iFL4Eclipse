package org.eclipse.sed.ifl.model.monitor.resource;

import java.util.HashMap;
import java.util.Map;

public class LineInfo extends Resource {

	public LineInfo(String absolutePath, int offset) {

		super(absolutePath + ":" + offset, new HashMap<String, Object>() {
			{
				put("absolute_path", absolutePath);
				put("position", offset);
			}
		});
	}
}
