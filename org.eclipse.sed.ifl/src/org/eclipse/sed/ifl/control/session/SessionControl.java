package org.eclipse.sed.ifl.control.session;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.score.ScoreListControl;
import org.eclipse.sed.ifl.control.score.ScoreLoaderControl;
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
		
		List<IMethodDescription> methods = resolvedMethods.entrySet().stream()
		.map(method -> new Method(identityFrom(method), locationFrom(method), contextFrom(method, resolvedMethods)))
		.collect(Collectors.toUnmodifiableList());
		System.out.printf("%d method found\n", methods.size());

		Random r = new Random();
		var sampleScores = methods.stream()
		.map(method -> method.getId().toCSVKey())
		.collect(Collectors.toUnmodifiableMap(id -> id, id -> r.nextDouble()));
		ScoreLoaderControl.saveSample(sampleScores, new File("sampleFor_" + selectedProject.getElementName() + ".csv"));

		ScoreListModel model = new ScoreListModel(methods);
		scoreListControl = new ScoreListControl(model, new ScoreListView(new ScoreListUI(getView().getUI(), SWT.NONE)));
		scoreLoaderControl = new ScoreLoaderControl(model, new ScoreLoaderView());
		addSubControl(scoreLoaderControl);
		addSubControl(scoreListControl);
	}

	private List<MethodIdentity> contextFrom(Entry<IMethodBinding, IMethod> method, Map<IMethodBinding, IMethod> others) {
		return accessor.getSiblings(method, others).entrySet().stream()
		.filter(contextMethod -> !contextMethod.getValue().equals(method.getValue()))
		.map(contextMethod -> identityFrom(contextMethod))
		.collect(Collectors.toUnmodifiableList());
	}

	private CodeChunkLocation locationFrom(Entry<IMethodBinding, IMethod> method) {
		return new CodeChunkLocation(
			EU.tryUnchecked(() -> method.getValue().getUnderlyingResource().getLocation().toOSString()),
			new Position(EU.tryUnchecked(() -> method.getValue().getSourceRange().getOffset())),
			new Position(EU.tryUnchecked(() -> method.getValue().getSourceRange().getOffset() + method.getValue().getSourceRange().getLength()))
		);
	}

	private MethodIdentity identityFrom(Entry<IMethodBinding, IMethod> method) {
		return new MethodIdentity(
			method.getKey().getName(),
			accessor.getSignature(method.getKey()),
			method.getKey().getDeclaringClass().getName(),
			method.getKey().getReturnType().getName(),
			method.getKey().getKey()
		);
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
		getView().eventHideUndefinedRequested().remove(hideUndefinedListener);
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
	private IListener<Boolean> hideUndefinedListener;
	
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
		hideUndefinedListener = status -> scoreListControl.setHideUndefinedScores(status);
		getView().eventHideUndefinedRequested().add(hideUndefinedListener);
	}
	
	public void updateRandomScores(int count) {
		scoreListControl.updateRandomScores(count);
	}

	
}