package org.eclipse.sed.ifl.control.session;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.sed.ifl.model.monitor.resource.Project;
import org.eclipse.sed.ifl.util.Maps;

public class SessionEvent extends Event {

	private SessionEvent(String state, IJavaProject project) {
		super(Maps.<String, Object>builder().put("state", state).unmodifiable(true).build());

		resources.put(new Project(project.getElementName()), "target");
	}

	public static SessionEvent start(IJavaProject project) {
		return new SessionEvent("start", project);
	}

	public static SessionEvent stop(IJavaProject project) {
		return new SessionEvent("stop", project);
	}
}
