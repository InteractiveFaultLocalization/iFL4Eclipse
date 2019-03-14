package org.eclipse.sed.ifl.ide;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sed.ifl.control.monitor.LogOnlyModeControl;
import org.eclipse.sed.ifl.control.session.SessionAlreadyActiveException;
import org.eclipse.sed.ifl.control.session.SessionControl;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.sed.ifl";

	private static Activator plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		if (session != null) {
			deactivateSession();
		}
		if (logOnlyMode != null) {
			logOnlyMode.teardown();
		}
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	private SessionControl session;

	private IListener<EmptyEvent> sessionFinishedListener;
	
	public SessionControl getSession() {
		return session;
	}
	
	private void deactivateSession() {
		//session.eventFinished().remove(sessionFinishedListener);
		session.teardown();
		session = null;
		System.out.println("Session deactivated.");
	}
	
	public Boolean isSessionActive() {
		return session != null;
	}
	
	public void setSession(SessionControl newSession) {
		if (!isSessionActive()) {
			session = newSession;
			sessionFinishedListener = e -> deactivateSession();
			session.eventFinished().add(sessionFinishedListener);
		}
		else {
			throw new SessionAlreadyActiveException();
		}
	}
	
	private LogOnlyModeControl logOnlyMode;
	
	public void setLogOnlyMode(LogOnlyModeControl value) {
		logOnlyMode = value;
	}
	
	public boolean isLogOnlyModeActive() {
		return logOnlyMode != null;
	}
	
	public LogOnlyModeControl getLogOnlyMode() {
		return logOnlyMode;
	}
}
