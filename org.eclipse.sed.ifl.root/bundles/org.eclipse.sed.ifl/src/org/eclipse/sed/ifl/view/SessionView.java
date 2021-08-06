package org.eclipse.sed.ifl.view;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
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

public class SessionView extends View implements IEmbeddable, IEmbedee {
	private MainPart part;

	public SessionView(PartAccessor partAccessor) {
		this.part = (MainPart) service.findPart(MainPart.ID);
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

	private NonGenericListenerCollection<MPart> closed = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<MPart> eventClosed() {
		return closed;
	}

	@Inject
	private EPartService service;
	private IPartListener stateListener;

	private void initUIStateListeners() {
		stateListener = new IPartListener() {
			@Override
			public void partActivated(MPart part) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void partBroughtToTop(MPart part) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void partDeactivated(MPart part) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void partHidden(MPart part) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void partVisible(MPart part) {
				// TODO Auto-generated method stub
				
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
		service.hidePart(service.findPart(MainPart.ID));
	}

}
