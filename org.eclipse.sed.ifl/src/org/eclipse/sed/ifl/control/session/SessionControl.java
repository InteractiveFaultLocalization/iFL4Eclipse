package org.eclipse.sed.ifl.control.session;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.ScoreListControl;
import org.eclipse.sed.ifl.control.score.ScoreLoaderControl;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.session.SessionModel;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.Method;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.view.ScoreListView;
import org.eclipse.sed.ifl.view.ScoreLoaderView;
import org.eclipse.sed.ifl.view.SessionView;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;

public class SessionControl extends Control<SessionModel, SessionView> {
	private IJavaProject selectedProject;
	
	public SessionControl(SessionModel model, SessionView view, IJavaProject selectedProject) {
		super(model, view);
		this.selectedProject = selectedProject;
	}

	private CodeEntityAccessor accessor = new CodeEntityAccessor();
	
	private ScoreListControl scoreListControl;
	
	private void startNewSession() {
		var resolvedMethods = accessor.getResolvedMethods(selectedProject);
		List<IMethodDescription> methods = resolvedMethods.stream()
		.map(method -> 
			new Method(
				new MethodIdentity(
					method.getName(),
					accessor.getSignature(method),
					method.getDeclaringClass().getName(),
					method.getReturnType().getName(),
					method.getKey()
				),
				null
				/*new CodeChunkLocation(
					EU.tryUnchecked(() -> method.getKey().getUnderlyingResource().getLocation().toOSString()),
					new Position(EU.tryUnchecked(() -> method.getKey().getSourceRange().getOffset())),
					new Position(EU.tryUnchecked(() -> method.getKey().getSourceRange().getOffset() + method.getKey().getSourceRange().getLength()))
				)*/
			)
		)
		.collect(Collectors.toUnmodifiableList());
		System.out.printf("%d method found\n", methods.size());
		ScoreListModel model = new ScoreListModel(methods);
		scoreListControl = new ScoreListControl(model, new ScoreListView(new ScoreListUI(getView().getUI(), SWT.NONE)));
		scoreLoaderControl = new ScoreLoaderControl(model, new ScoreLoaderView());
		addSubControl(scoreLoaderControl);
		addSubControl(scoreListControl);
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
		scoreListControl = null;
		scoreLoaderControl = null;
	}

	private NonGenericListenerCollection<EmptyEvent> finished = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<EmptyEvent> eventFinished() {
		return finished;
	}
	
	private IListener<IWorkbenchPart> closeListener;

	private ScoreLoaderControl scoreLoaderControl;
	private IListener<EmptyEvent> scoreLoadRequestedListener;
	
	private void initUIStateListeners() {
		closeListener = part -> {
			System.out.println("Session closing...");
			this.finished.invoke(new EmptyEvent());
		};
		getView().eventClosed().add(closeListener);
		scoreLoadRequestedListener = __ -> {
			System.out.println("Loading scores from files are requested...");
			this.scoreLoaderControl.load();
		};
		getView().eventScoreLoadRequested().add(scoreLoadRequestedListener);
	}
	
	public void updateRandomScores(int count) {
		scoreListControl.updateRandomScores(count);
	}

	
}