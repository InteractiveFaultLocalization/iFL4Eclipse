package org.eclipse.sed.ifl.gui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sed.ifl.control.MainControl;
import org.eclipse.sed.ifl.model.MainModel;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.sed.ifl";

	private static Activator plugin;
	
	private MainControl control = new MainControl(new MainModel());
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		initMVC();
	}

	private void initMVC() {
		control.init();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		control.teardown();
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
