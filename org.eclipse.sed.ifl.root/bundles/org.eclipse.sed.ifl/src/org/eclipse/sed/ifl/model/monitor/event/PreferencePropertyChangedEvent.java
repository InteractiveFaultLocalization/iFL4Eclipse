package org.eclipse.sed.ifl.model.monitor.event;

import org.eclipse.sed.ifl.util.Maps;

public class PreferencePropertyChangedEvent extends Event {
	public PreferencePropertyChangedEvent(String propertyName, Object oldValue, Object newValue) {
		super(Maps.<String, Object>builder()
				.put("propertyName", propertyName)
				.put("oldValue", oldValue)
				.put("newValue", newValue).
				unmodifiable(true).build());
		
	}
}
