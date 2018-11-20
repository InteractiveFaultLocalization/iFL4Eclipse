package org.eclipse.sed.ifl.ide;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sed.ifl.control.session.SessionAlreadyActiveException;
import org.eclipse.sed.ifl.control.session.SessionControl;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.sed.ifl";

	private static Activator plugin;
	
	public Activator() {
		System.out.println("activator");
	}

	public void start(BundleContext context) throws Exception {
		System.out.println("activator.start() {");
		super.start(context);
		plugin = this;
		System.out.println("} activator.start()");
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	private SessionControl session;
	
	public SessionControl getSession() {
		return session;
	}
	
	public Boolean isSessionActive() {
		return session != null;
	}
	
	public void setSession(SessionControl newSession) {
		if (!isSessionActive()) {
			session = newSession; 
		}
		else {
			throw new SessionAlreadyActiveException();
		}
	}
}
