package org.eclipse.sed.ifl.view;

import org.eclipse.jface.action.Action;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.accessor.gui.PartAccessor;
import org.eclipse.sed.ifl.ide.gui.MainPart;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

public class SessionView extends View implements IEmbeddable, IEmbedee {
	private MainPart part;

	public SessionView(PartAccessor partAccessor) {
		this.part = (MainPart) partAccessor.getPart(MainPart.ID);
	}

	@Override
	public void setParent(Composite parent) {
		part.setParent(parent);
	}

	@Override
	public void embed(IEmbeddable embedded) {
		part.embed(embedded);
	}

	@Override
	public void init() {
		service = part.getSite().getService(IPartService.class);
		initUIStateListeners();
		scoreLoadRequestedListener = event -> {
			scoreLoadRequested.invoke(new EmptyEvent());
		};
		scoreRecalculateRequestedListener = event -> {
			scoreRecalculateRequested.invoke(new EmptyEvent());
		};
		part.eventScoreLoadRequested().add(scoreLoadRequestedListener);
		part.eventScoreRecalculateRequested().add(scoreRecalculateRequestedListener);
		super.init();
	}

	@Override
	public void teardown() {
		service.removePartListener(stateListener);
		part.eventScoreLoadRequested().remove(scoreLoadRequestedListener);
		part.eventHideUndefinedRequested().remove(hideUndefinedListener);
		part.eventScoreRecalculateRequested().remove(scoreRecalculateRequestedListener);
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
			public void partOpened(IWorkbenchPart part) {
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				if (SessionView.this.part.equals(part)) {
					System.out.println("MainPart closed");
					SessionView.this.closed.invoke(part);
				}
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
			}

			@Override
			public void partActivated(IWorkbenchPart part) {
			}
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

	private NonGenericListenerCollection<EmptyEvent> scoreRecalculateRequested = new NonGenericListenerCollection<>();
	private IListener<Action> scoreRecalculateRequestedListener;

	public INonGenericListenerCollection<EmptyEvent> eventScoreRecalculateRequested() {
		return scoreRecalculateRequested;
	}

	public void close() {
		part.getSite().getPage().hideView(part);
	}

}
