package org.eclipse.sed.ifl.model.monitor.resource;

import java.util.HashMap;

public class LineInfo extends Resource {

	public LineInfo(String absolutePath, int offset) {
		super(absolutePath + ":" + offset, new HashMap<String, Object>() {
			private static final long serialVersionUID = -758865882281387353L;
				{
					put("absolute_path", absolutePath); put("position", offset);
				}
			});
	}
}
