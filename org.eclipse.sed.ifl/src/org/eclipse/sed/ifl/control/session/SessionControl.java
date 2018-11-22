package org.eclipse.sed.ifl.control.session;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.ScoreListControl;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.session.SessionModel;
import org.eclipse.sed.ifl.model.source.CodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.Method;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.source.Position;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.sed.ifl.view.ScoreListView;
import org.eclipse.sed.ifl.view.SessionView;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;

public class SessionControl extends Control<SessionModel, SessionView> {
	private IJavaProject selectedProject;
	
	public SessionControl(SessionModel model, SessionView view, IJavaProject selectedProject) {
		super(model, view);
		this.selectedProject = selectedProject;
	}

	CodeEntityAccessor accessor = new CodeEntityAccessor(); 
	
	public void startNewSession() {
		List<IMethodDescription> methods = accessor.getMethods(selectedProject).stream()
		.map(method ->
			new Method(
				new MethodIdentity(
					method.getElementName(),
					EU.tryUnchecked(() -> method.getSignature()),
					method.getDeclaringType().getElementName()
				),
				new CodeChunkLocation(
					EU.tryUnchecked(() -> method.getUnderlyingResource().getLocation().toOSString()),
					new Position(EU.tryUnchecked(() -> method.getSourceRange().getOffset())),
					new Position(EU.tryUnchecked(() -> method.getSourceRange().getOffset() + method.getSourceRange().getLength()))
				)
			)
		)
		.collect(Collectors.toUnmodifiableList());
		System.out.printf("%d method found", methods.size());
		addSubControl(new ScoreListControl(new ScoreListModel(methods), new ScoreListView(new ScoreListUI(getView().getUI(), SWT.NONE))));
	}
	
	@Override
	public void init() {
		initUIStateListeners();
		startNewSession();
		super.init();
	}
	
	@Override
	public void teardown() {
		getView().eventClosed().remove(closeListener);
		super.teardown();
	}

	private NonGenericListenerCollection<EmptyEvent> finished = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<EmptyEvent> eventFinished() {
		return finished;
	}
	
	private IListener<IWorkbenchPart> closeListener;
	
	private void initUIStateListeners() {
		closeListener = part -> {
			System.out.println("Session closing...");
			this.finished.invoke(new EmptyEvent());
		};
		getView().eventClosed().add(closeListener);
	}
}
