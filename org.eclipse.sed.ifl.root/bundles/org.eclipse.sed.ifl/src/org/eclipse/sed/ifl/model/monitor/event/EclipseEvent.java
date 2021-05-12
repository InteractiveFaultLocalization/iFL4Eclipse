package org.eclipse.sed.ifl.model.monitor.event;

import org.eclipse.sed.ifl.util.Maps;

public class EclipseEvent extends Event {

	public EclipseEvent(String state) {
		super(Maps.<String, Object>builder().put("state", state).unmodifiable(true).build());
	}

	public static EclipseEvent start() {
		return new EclipseEvent("start");
	}
	
	public static EclipseEvent stop() {
		return new EclipseEvent("stop");
	}
	
}
