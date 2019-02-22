package org.eclipse.sed.ifl.model.monitor.event;

import java.util.Map;

import org.eclipse.sed.ifl.model.monitor.resource.LineInfo;
import org.eclipse.sed.ifl.model.source.ICodeChunkLocation;

public class NavigationEvent extends Event {

	public NavigationEvent(ICodeChunkLocation target) {
		super(Map.of());
		resources.put(new LineInfo(target.getAbsolutePath(), target.getBegining().getOffset()), "target");
	}
}
