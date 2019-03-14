package org.eclipse.sed.ifl.model.monitor.event;

import org.eclipse.sed.ifl.util.Maps;

public class ModeEvent extends Event {

	public enum State {
		ACTIVATED, DEACTIVATED, DENIED
	}
	
	public ModeEvent(String name, State state) {
		super(Maps.<String,Object>builder()
			.put("name", name)
			.put("state", state.name())
			.build());
	}

}
