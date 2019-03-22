package org.eclipse.sed.ifl.view;

import org.eclipse.jface.action.Action;
import org.eclipse.sed.ifl.ide.gui.MainPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
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
		scoreLoadRequestedListener = event -> {
			scoreLoadRequested.invoke(new EmptyEvent());
		};
		part.eventScoreLoadRequested().add(scoreLoadRequestedListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		service.removePartListener(stateListener);
		part.eventScoreLoadRequested().remove(scoreLoadRequestedListener);
		part.eventHideUndefinedRequested().remove(hideUndefinedListener);
		super.teardown();
	}
	
	private NonGenericListenerCollection<IWorkbenchPart> closed = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<IWorkbenchPart> eventClosed() {
		return closed;
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
					SessionView.this.closed.invoke(part);
				}
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) { }
			
			@Override
			public void partActivated(IWorkbenchPart part) { }
		};
		service.addPartListener(stateListener);
		hideUndefinedListener = status -> hideUndefinedRequested.invoke(status);
		part.eventHideUndefinedRequested().add(hideUndefinedListener);
	}
	
	private NonGenericListenerCollection<EmptyEvent> scoreLoadRequested = new NonGenericListenerCollection<>();
	private IListener<Action> scoreLoadRequestedListener;
	
	public INonGenericListenerCollection<EmptyEvent> eventScoreLoadRequested() {
		return scoreLoadRequested;
	}
	
	private NonGenericListenerCollection<Boolean> hideUndefinedRequested = new NonGenericListenerCollection<>();
	private IListener<Boolean> hideUndefinedListener;
	
	public INonGenericListenerCollection<Boolean> eventHideUndefinedRequested() {
		return hideUndefinedRequested;
	}

	public void close() {
		part.getSite().getPage().hideView(part);
	}

}
