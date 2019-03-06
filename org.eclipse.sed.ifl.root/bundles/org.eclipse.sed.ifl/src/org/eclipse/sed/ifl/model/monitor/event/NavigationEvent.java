package org.eclipse.sed.ifl.model.monitor.event;

import java.util.HashMap;

import org.eclipse.sed.ifl.model.monitor.resource.CodeElement;
import org.eclipse.sed.ifl.model.monitor.resource.File;
import org.eclipse.sed.ifl.model.monitor.resource.LineInfo;
import org.eclipse.sed.ifl.model.source.IMethodDescription;

public class NavigationEvent extends Event {

	public NavigationEvent(IMethodDescription event) {
		super(new HashMap<>());
		resources.put(new LineInfo(event.getLocation().getAbsolutePath(), event.getLocation().getBegining().getOffset()), "target");
		resources.put(new CodeElement(event.getId().getKey()), "target");
		resources.put(new File(event.getLocation().getAbsolutePath()), "target");
	}
}
