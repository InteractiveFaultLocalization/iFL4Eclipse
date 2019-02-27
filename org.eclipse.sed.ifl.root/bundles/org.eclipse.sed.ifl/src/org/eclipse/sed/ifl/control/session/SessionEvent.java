package org.eclipse.sed.ifl.control.session;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sed.ifl.model.monitor.event.Event;
import org.eclipse.sed.ifl.model.monitor.resource.Project;

public class SessionEvent extends Event {

	private SessionEvent(String state, IJavaProject project) {
		super(new HashMap<String, Object>() {{put("state", state);}});

		resources.put(new Project(project.getElementName()), "target");
	}

	public static SessionEvent start(IJavaProject project) {
		return new SessionEvent("start", project);
	}

	public static SessionEvent stop(IJavaProject project) {
		return new SessionEvent("stop", project);
	}
}
