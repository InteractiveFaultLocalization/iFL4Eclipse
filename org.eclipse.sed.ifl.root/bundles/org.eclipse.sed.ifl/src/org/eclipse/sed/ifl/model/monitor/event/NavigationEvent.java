package org.eclipse.sed.ifl.model.monitor.event;

import java.util.HashMap;


import org.eclipse.sed.ifl.model.monitor.resource.LineInfo;
import org.eclipse.sed.ifl.model.source.ICodeChunkLocation;

public class NavigationEvent extends Event {

	public NavigationEvent(ICodeChunkLocation target) {
		super(new HashMap<>());
		resources.put(new LineInfo(target.getAbsolutePath(), target.getBegining().getOffset()), "target");
	}
}
