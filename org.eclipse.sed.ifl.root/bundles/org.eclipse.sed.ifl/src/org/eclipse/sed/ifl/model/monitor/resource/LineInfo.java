package org.eclipse.sed.ifl.model.monitor.resource;

import java.util.Map;

public class LineInfo extends Resource {

	public LineInfo(String absolutePath, int offset) {
		super(absolutePath + ":" + offset, Map.of("absolute_path", absolutePath, "position", offset));
	}
}
