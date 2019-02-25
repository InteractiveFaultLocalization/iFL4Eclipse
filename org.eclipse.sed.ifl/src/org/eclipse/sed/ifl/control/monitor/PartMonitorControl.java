package org.eclipse.sed.ifl.control.monitor;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.accessor.gui.PartAccessor;
import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.monitor.PartMonitorModel;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

public class PartMonitorControl extends ViewlessControl<PartMonitorModel> {

	private PartAccessor accessor;
			
	public PartMonitorControl(PartMonitorModel model, PartAccessor accessor) {
		super(model);
		this.accessor = accessor;
	}

	private ActivityMonitorControl activityMonitor = new ActivityMonitorControl(new ActivityMonitorModel());
	
	private IPartListener2 lifeCycleListener = new IPartListener2() {
		
		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
		}
		
		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
			System.out.println();
		}
		
		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
		}
		
		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
		}
		
		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
		}
		
		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
		}
		
		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}
		
		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
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
