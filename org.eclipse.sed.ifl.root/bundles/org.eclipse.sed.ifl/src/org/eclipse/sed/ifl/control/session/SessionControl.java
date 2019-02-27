package org.eclipse.sed.ifl.control.session;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.monitor.ActivityMonitorControl;
import org.eclipse.sed.ifl.control.score.ScoreListControl;
import org.eclipse.sed.ifl.control.score.ScoreLoaderControl;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.gui.ScoreListUI;
import org.eclipse.sed.ifl.model.monitor.ActivityMonitorModel;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.model.session.SessionModel;
import org.eclipse.sed.ifl.model.source.CodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.Method;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.source.Position;
import org.eclipse.sed.ifl.model.user.interaction.SideEffect;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
import org.eclipse.sed.ifl.view.ScoreListView;
import org.eclipse.sed.ifl.view.ScoreLoaderView;
import org.eclipse.sed.ifl.view.SessionView;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;

public class SessionControl extends Control<SessionModel, SessionView> {
	private IJavaProject selectedProject;
	
	private ActivityMonitorControl activityMonitor = new ActivityMonitorControl(new ActivityMonitorModel());
	
	public SessionControl(SessionModel model, SessionView view, IJavaProject selectedProject) {
		super(model, view);
		this.selectedProject = selectedProject;
	}

	private CodeEntityAccessor accessor = new CodeEntityAccessor();
	
	private ScoreListControl scoreListControl;
	
	private void startNewSession() {
		NanoWatch watch = new NanoWatch("starting session");
		Map<IMethodBinding, IMethod> resolvedMethods = accessor.getResolvedMethods(selectedProject);
		
		List<IMethodDescription> methods = resolvedMethods.entrySet().stream()
		.map(method -> new Method(identityFrom(method), locationFrom(method), contextFrom(method, resolvedMethods)))
		.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
		System.out.printf("%d method found\n", methods.size());

		Random r = new Random();
		Map<String, Double> sampleScores = methods.stream()
		.map(method -> method.getId().toCSVKey())
		.collect(Collectors.collectingAndThen(Collectors.toMap(id -> id, id -> r.nextDouble()),Collections::unmodifiableMap));
		ScoreLoaderControl.saveSample(sampleScores, new File("sampleFor_" + selectedProject.getElementName() + ".csv"));

		ScoreListModel model = new ScoreListModel(methods);
		scoreListControl = new ScoreListControl(model, new ScoreListView(new ScoreListUI(getView().getUI(), SWT.NONE)));
		scoreLoaderControl = new ScoreLoaderControl(model, new ScoreLoaderView());
		addSubControl(scoreLoaderControl);
		addSubControl(scoreListControl);
		System.out.println(watch);
	}

	private List<MethodIdentity> contextFrom(Entry<IMethodBinding, IMethod> method, Map<IMethodBinding, IMethod> others) {
		return accessor.getSiblings(method, others).entrySet().stream()
		.filter(contextMethod -> !contextMethod.getValue().equals(method.getValue()))
		.map(contextMethod -> identityFrom(contextMethod))
		.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
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
		addSubControl(activityMonitor);
		
		initUIStateListeners();
		startNewSession();
		scoreListControl.eventTerminationRequested().add(terminationReqestedListener);
		super.init();
		activityMonitor.log(SessionEvent.start(selectedProject));
	}
	
	@Override
	public void teardown() {
		scoreListControl.eventTerminationRequested().remove(terminationReqestedListener);
		getView().eventClosed().remove(closeListener);
		getView().eventHideUndefinedRequested().remove(hideUndefinedListener);
		super.teardown();
		scoreListControl = null;
		scoreLoaderControl = null;
		activityMonitor = null;
	}
	
	private IListener<SideEffect> terminationReqestedListener = event -> {
		getView().close();
	};

	private NonGenericListenerCollection<EmptyEvent> finished = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<EmptyEvent> eventFinished() {
		return finished;
	}
	
	private IListener<IWorkbenchPart> closeListener = part -> {
		terminate();
	};

	private void terminate() {
		System.out.println("Session closing...");
		activityMonitor.log(SessionEvent.stop(selectedProject));
		this.finished.invoke(new EmptyEvent());
	}

	private ScoreLoaderControl scoreLoaderControl;
	
	private IListener<EmptyEvent> scoreLoadRequestedListener =__ -> {
		System.out.println("Loading scores from files are requested...");
		this.scoreLoaderControl.load();
	};
	private IListener<Boolean> hideUndefinedListener = status -> scoreListControl.setHideUndefinedScores(status);
	
	private void initUIStateListeners() {
		getView().eventClosed().add(closeListener);
		getView().eventScoreLoadRequested().add(scoreLoadRequestedListener);
		getView().eventHideUndefinedRequested().add(hideUndefinedListener);
	}
}