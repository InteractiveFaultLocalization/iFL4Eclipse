package org.eclipse.sed.ifl.ide.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.sed.ifl.ide.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	
	public PreferenceInitializer() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		store.setDefault("logKey", false);
		store.setDefault("scenarioId", "");
		store.setDefault("userId", "");
		store.setDefault("host", "");
		store.setDefault("port", "");
		store.setDefault("generatedId", "");
		store.setDefault("macAddress", "");
		store.setDefault("ipAddress", "");
		store.setDefault("interactivity", "random");
	}

}
