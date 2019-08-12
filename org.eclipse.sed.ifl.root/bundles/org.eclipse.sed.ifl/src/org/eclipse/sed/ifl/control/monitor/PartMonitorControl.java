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
			
	public PartMonitorControl(PartAccessor accessor) {
		this.accessor = accessor;
	}

	private ActivityMonitorControl activityMonitor;
	
	private IPartListener2 lifeCycleListener = new IPartListener2() {

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {

			activityMonitor.log(new PartEvent(partRef, PartState.VISIBLE));

		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {

			activityMonitor.log(new PartEvent(partRef, PartState.OPEN));

		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {

			activityMonitor.log(new PartEvent(partRef, PartState.HIDDEN));

		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {


			activityMonitor.log(new PartEvent(partRef, PartState.DEACTIVATE));


		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {

			activityMonitor.log(new PartEvent(partRef, PartState.CLOSE));

		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {

			activityMonitor.log(new PartEvent(partRef, PartState.BROUGHTTOTOP));

		}

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {


			activityMonitor.log(new PartEvent(partRef, PartState.ACTIVE));


		}
	};
	
	public IPartListener2 getListener() {
		return lifeCycleListener;
	}
		
	
	@Override
	public void init() {
		activityMonitor = new ActivityMonitorControl();
		activityMonitor.setModel(new ActivityMonitorModel());

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
