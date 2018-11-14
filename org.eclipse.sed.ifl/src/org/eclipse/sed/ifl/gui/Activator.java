package org.eclipse.sed.ifl.gui;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sed.ifl.control.MainControl;
import org.eclipse.sed.ifl.control.project.ProjectControl;
import org.eclipse.sed.ifl.gui.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.model.MainModel;
import org.eclipse.sed.ifl.model.project.ProjectModel;
import org.eclipse.sed.ifl.model.source.CodeChunkLocation;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.source.Method;
import org.eclipse.sed.ifl.model.source.MethodIdentity;
import org.eclipse.sed.ifl.model.source.Position;
import org.eclipse.sed.ifl.util.exception.EU;
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
		CodeEntityAccessor accessor = new CodeEntityAccessor();
		for (IJavaProject project : accessor.getJavaProjects()) {
			List<IMethodDescription> methods = accessor.getMethods(project).stream()
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
			control.addProjectControl(new ProjectControl(new ProjectModel(methods)));
		}
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
