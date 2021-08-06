package org.eclipse.sed.ifl.control.monitor;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.accessor.gui.PartAccessor;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.PartMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.PartEvent;

public class PartMonitorControl extends ViewlessControl<PartMonitorModel> {

	private PartAccessor accessor;
			
	public PartMonitorControl(PartAccessor accessor) {
		this.accessor = accessor;
	}

	private ActivityMonitorControl activityMonitor;
	
	private IPartListener lifeCycleListener = new IPartListener() {

		@Override
		public void partActivated(MPart part) {
			activityMonitor.log(new PartEvent(part, PartState.ACTIVE));
			
		}

		@Override
		public void partBroughtToTop(MPart part) {
			activityMonitor.log(new PartEvent(part, PartState.BROUGHTTOTOP));
			
		}

		@Override
		public void partDeactivated(MPart part) {
			activityMonitor.log(new PartEvent(part, PartState.DEACTIVATE));
			
		}

		@Override
		public void partHidden(MPart part) {
			activityMonitor.log(new PartEvent(part, PartState.HIDDEN));
			
		}

		@Override
		public void partVisible(MPart part) {
			activityMonitor.log(new PartEvent(part, PartState.VISIBLE));
			
		}
	};
	
	public IPartListener getListener() {
		return lifeCycleListener;
	}
		
	
	@Override
	public void init() {
		activityMonitor = new ActivityMonitorControl(new ActivityMonitorModel());

		addSubControl(activityMonitor);
		accessor.addListenerToAllPages(lifeCycleListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		accessor.removeListenerToAllPages(lifeCycleListener);
		super.teardown();
	}

}
