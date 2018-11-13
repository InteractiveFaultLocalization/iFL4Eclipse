package org.eclipse.sed.ifl.control.session;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.control.project.ProjectControl;
import org.eclipse.sed.ifl.gui.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.model.project.ProjectModel;
import org.eclipse.sed.ifl.model.session.SessionModel;
import org.eclipse.sed.ifl.model.source.CodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.Method;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.source.Position;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.sed.ifl.view.SessionView;

public class SessionControl extends Control<SessionModel, SessionView> {
	
	public SessionControl(SessionModel model, SessionView view) {
		super(model, view);
	}

	private void addProjectControl(ProjectControl control) {
		addSubControl(control);
	}
	
	public void startNewSession(IJavaProject subject) {
		CodeEntityAccessor accessor = new CodeEntityAccessor();
		List<IMethodDescription> methods = accessor.getMethods(subject).stream()
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
		addProjectControl(new ProjectControl(new ProjectModel(methods)));
	}
}
