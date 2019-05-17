package org.eclipse.sed.ifl.control.monitor;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.accessor.gui.PartAccessor;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.PartMonitorModel;
import org.eclipse.sed.ifl.model.monitor.event.PartEvent;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

public class PartMonitorControl extends ViewlessControl<PartMonitorModel> {

	private PartAccessor accessor;
			
	public PartMonitorControl(PartMonitorModel model, PartAccessor accessor) {
		super(model);
		this.accessor = accessor;
		addSubControl(activityMonitor);
	}

	private ActivityMonitorControl activityMonitor = new ActivityMonitorControl(new ActivityMonitorModel());
	
	
		private IPartListener2 lifeCycleListener = new IPartListener2() {
				
				@Override
				public void partVisible(IWorkbenchPartReference partRef) {
					synchronized(activityMonitor) {
						activityMonitor.log(new PartEvent(partRef, PartState.VISIBLE));
					}
				}
				
				@Override
				public void partOpened(IWorkbenchPartReference partRef) {
					synchronized(activityMonitor) {
						activityMonitor.log(new PartEvent(partRef, PartState.OPEN));
					}
				}
				
				@Override
				public void partInputChanged(IWorkbenchPartReference partRef) {
				}
				
				@Override
				public void partHidden(IWorkbenchPartReference partRef) {
					synchronized(activityMonitor) {
						activityMonitor.log(new PartEvent(partRef, PartState.HIDDEN));
					}
				}
				
				@Override
				public void partDeactivated(IWorkbenchPartReference partRef) {
					synchronized(activityMonitor) {
						activityMonitor.log(new PartEvent(partRef, PartState.DEACTIVATE));
					}
				}
				
				@Override
				public void partClosed(IWorkbenchPartReference partRef) {
					synchronized(activityMonitor) {
						activityMonitor.log(new PartEvent(partRef, PartState.CLOSE));
					}
				}
				
				@Override
				public void partBroughtToTop(IWorkbenchPartReference partRef) {
					synchronized(activityMonitor) {
						activityMonitor.log(new PartEvent(partRef, PartState.BROUGHTTOTOP));
					}
				}
				
				@Override
				public void partActivated(IWorkbenchPartReference partRef) {
					synchronized(activityMonitor) {
						activityMonitor.log(new PartEvent(partRef, PartState.ACTIVE));
					}
				}
		};
	
	
	@Override
	public void init() {
		accessor.addListenerToAllPages(lifeCycleListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		accessor.removeListenerToAllPages(lifeCycleListener);
		super.teardown();
	}

}
