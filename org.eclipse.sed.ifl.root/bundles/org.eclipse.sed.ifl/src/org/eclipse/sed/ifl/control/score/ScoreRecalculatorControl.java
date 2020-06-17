package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.score.ScoreListModel;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.*;
import org.eclipse.debug.ui.*;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.*;

public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {

	public ScoreRecalculatorControl() {

	}

	public void recalculate() throws UnsupportedOperationException, CoreException, IOException {
		System.out.println("Recalculating scores are requested...");
		// throw new UnsupportedOperationException("Function is not yet implemented");
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		IExtensionPoint[] points = reg.getExtensionPoints();
		IConfigurationElement[] elements = new IConfigurationElement[points.length];
		IConfigurationElement[] locations = new IConfigurationElement[points.length];
		String[] pointsNames = new String[points.length];
		elements[0] = null;
		for (int i = 0; i < points.length; i++) {
			if (points[i].getSimpleIdentifier().startsWith("")) { // can be used for filtering certain ExtensionPoints
				pointsNames[i] = points[i].getSimpleIdentifier();
			}
			if (points[i].getSimpleIdentifier().contentEquals("targetLocations")) {
				locations = points[i].getConfigurationElements();
			}
		}
		Arrays.sort(pointsNames);

		for (int i = 0; i < points.length; i++) {
			System.out.println(pointsNames[i]);
			if (points[i].getSimpleIdentifier().equals("launchConfigurationTabs")) {
				elements = points[i].getConfigurationElements();
			}
		}
		System.out.println("Configs are:");
		for (int i = 0; i < elements.length; i++) {
			System.out.println(elements[i].getName());
		}

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager(); // Maven pluginnak és JUnitnak
																					// lekérni
		String configType = elements[0].getValue();
		ILaunchConfigurationType type = launchManager
				.getLaunchConfigurationType("org.eclipse.m2e.Maven2LaunchConfigurationType");
		ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations(type); // pluginokban van dedikált
																								// launchManager

		String typeName = "org.eclipse.sed.ifl";
		String goal = "clean install";
		String userSettings = "default";
		boolean skipTests = false;
		boolean resolution = false;

		for (int i = 0; i < configurations.length; i++) {
			ILaunchConfiguration configuration = configurations[i];
			if (configuration.getName().equals("Maven IFL build")) {
				ILaunchConfigurationWorkingCopy t = configuration.getWorkingCopy();
				ILaunchConfiguration config = t.doSave();
				if (config != null) {
					typeName = config.getAttribute("ATTR_MAIN_TYPE_NAME", "org.eclipse.sed.ifl");
					goal = config.getAttribute("M2_GOALS", "clean install");
					userSettings = config.getAttribute("M2_USER_SETTINGS", "ExtensionPointbolKinyerni");
					skipTests = config.getAttribute("M2_SKIP_TESTS", false);
					resolution = config.getAttribute("M2_WORKSPACE_RESOLUTION", false);
					// Map<String, Object> lAttributes = config.getAttributes(); // a more elegant
					// solution instead of above, still WIP
					DebugUITools.launch(config, ILaunchManager.DEBUG_MODE);
					break;
				} else {
					configuration.delete();
					break;
				}
			}
		}

		IVMInstall jre = JavaRuntime.getDefaultVMInstall();
		File jdkHome = jre.getInstallLocation();
		String toolsPathString =  Paths.get(jdkHome.getAbsolutePath() + "lib" + "tools.jar").normalize().toString();
		IPath toolsPath = null;
		toolsPath.append(toolsPathString);
		IRuntimeClasspathEntry toolsEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(toolsPath);
		toolsEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "Maven IFL build");
		workingCopy.setAttribute("ATTR_VM_INSTALL_NAME", jre.getName());
		workingCopy.setAttribute("ATTR_VM_INSTALL_TYPE", jre.getVMInstallType().getId());
		workingCopy.setAttribute("org.eclipse.jdt.launching.JRE_CONTAINER",
				"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/jre1.8.0_202");

		
		String systemLibsPathString =  Paths.get(JavaRuntime.JRE_CONTAINER).normalize().toString();
		IPath systemLibsPath = null;
		systemLibsPath.append(systemLibsPathString);
		IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath,
				IRuntimeClasspathEntry.STANDARD_CLASSES);

		List classpath = new ArrayList();
		classpath.add(toolsEntry.getMemento());
		classpath.add(agentEntry.getMemento());
		classpath.add(systemLibsEntry.getMemento());

		workingCopy.setAttribute("ATTR_CLASSPATH", classpath);
		workingCopy.setAttribute("ATTR_DEFAULT_CLASSPATH", false);

		workingCopy.setAttribute("ATTR_MAIN_TYPE_NAME", typeName);

		workingCopy.setAttribute("ATTR_LOCATION", locations[0]);

		workingCopy.setAttribute("M2_GOALS", goal);

		workingCopy.setAttribute("M2_USER_SETTINGS", userSettings);

		workingCopy.setAttribute("M2_SKIP_TESTS", skipTests);

		workingCopy.setAttribute("M2_WORKSPACE_RESOLUTION", resolution);

		File workingDir = JavaCore.getClasspathVariable("projektNev").append("bin").toFile();
		workingCopy.setAttribute("ATTR_WORKING_DIRECTORY", workingDir.getAbsolutePath());

		ILaunchConfiguration configuration = workingCopy.doSave();
		DebugUITools.launch(configuration, ILaunchManager.RUN_MODE); // Maven vagy JUnit-on keresztül.
		// Mavenbol classpath kinyerése

	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void teardown() {
		super.teardown();
	}

}
