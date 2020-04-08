package org.eclipse.sed.ifl.ide.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.PreferencePropertyChangedEvent;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private ActivityMonitorControl activityMonitor = new ActivityMonitorControl(new ActivityMonitorModel());
	private IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	
	public PreferenceInitializer() {
		store.addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				activityMonitor.log(new PreferencePropertyChangedEvent( event.getProperty(), event.getOldValue(), event.getNewValue()));
			}
			
		});
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
		store.setDefault("host", "localhost");
		store.setDefault("port", "8182");
	}

}
