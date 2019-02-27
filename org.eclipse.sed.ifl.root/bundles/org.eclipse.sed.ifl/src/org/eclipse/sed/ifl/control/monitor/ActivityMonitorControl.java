package org.eclipse.sed.ifl.control.monitor;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.Event;

public class ActivityMonitorControl extends ViewlessControl<ActivityMonitorModel> {

	public ActivityMonitorControl(ActivityMonitorModel model) {
		super(model);
	}
	
	public void log(Event event) {
		getModel().insertEvent(event);
		System.out.printf("new %s are logged\n", event.toString());
	}
}
