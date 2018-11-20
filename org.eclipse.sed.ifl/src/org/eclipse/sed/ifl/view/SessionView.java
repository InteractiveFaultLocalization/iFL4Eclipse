package org.eclipse.sed.ifl.view;

import org.eclipse.sed.ifl.ide.gui.MainPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

public class SessionView extends View {
	private MainPart part;
	
	public SessionView(MainPart part) {
		this.part = part;
	}

	@Override
	public Composite getUI() {
		return part.getUI();
	}
	
	@Override
	public void init() {
		service = part.getSite().getService(IPartService.class);
		initUIStateListeners();
		super.init();
	}
	
	@Override
	public void teardown() {
		service.removePartListener(stateListener);
		super.teardown();
	}
	
	private IPartService service;
	private IPartListener stateListener;
	
	private void initUIStateListeners() {
		stateListener = new IPartListener() {
			@Override
			public void partOpened(IWorkbenchPart part) { }
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {	}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				if (SessionView.this.part.equals(part)) {
					System.out.println("MainPart closed");
				}
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) { }
			
			@Override
			public void partActivated(IWorkbenchPart part) { }
		};
		service.addPartListener(stateListener);
	}
}
