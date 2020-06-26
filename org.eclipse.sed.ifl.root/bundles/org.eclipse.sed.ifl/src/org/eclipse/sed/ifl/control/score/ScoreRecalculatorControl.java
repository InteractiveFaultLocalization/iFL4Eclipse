package org.eclipse.sed.ifl.control.score;

import org.apache.maven.project.MavenProject;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.score.ScoreListModel;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.*;
import org.eclipse.debug.ui.*;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.*;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;

public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {

	private IJavaProject selectedProject;
	private IProject currentIProject;

	public ScoreRecalculatorControl(IJavaProject selectedProject) {
		this.selectedProject = selectedProject;
		this.currentIProject = selectedProject.getProject();
	}

	public void recalculate() throws UnsupportedOperationException, CoreException, IOException {
		System.out.println("Recalculating scores are requested...");
		// throw new UnsupportedOperationException("Function is not yet implemented");

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager(); // Maven pluginnak és JUnitnak
																					// lekérni
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

		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "Maven IFL build");

		boolean isJREConfigured = false;

		for (IClasspathEntry entry : selectedProject.getRawClasspath()) {
			if (JavaRuntime.JRE_CONTAINER.equals(entry.getPath().segment(0))) {
				IPath jreContainerPath = entry.getPath();
				isJREConfigured = true;
				break;
			}
		}

		/*
		 * if(isJREConfigured == false) { throw new
		 * CoreException(StatusUtil.error(ScoreRecalculatorControl.class, "Project " +
		 * currentIProject.getName() + " does not have JRE configured.")); }
		 */
		// Unsure about proper CodeException handling

		IVMInstall jre = JavaRuntime.getDefaultVMInstall();
		File jdkHome = jre.getInstallLocation();
		String toolsPathString = Paths.get(jdkHome.getAbsolutePath() + "lib" + "tools.jar").normalize().toString();
		IPath toolsPath = null;
		toolsPath.append(toolsPathString);
		IRuntimeClasspathEntry toolsEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(toolsPath);
		toolsEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

		workingCopy.setAttribute(ILaunchManager.ATTR_PRIVATE, true);
		workingCopy.setAttribute("ATTR_VM_INSTALL_NAME", jre.getName());
		workingCopy.setAttribute("ATTR_VM_INSTALL_TYPE", jre.getVMInstallType().getId());
		workingCopy.setAttribute("org.eclipse.jdt.launching.JRE_CONTAINER",
				"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/jre1.8.0_202");

		String systemLibsPathString = Paths.get(JavaRuntime.JRE_CONTAINER).normalize().toString();
		IPath systemLibsPath = null;
		systemLibsPath.append(systemLibsPathString);
		IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath,
				IRuntimeClasspathEntry.STANDARD_CLASSES);

		IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
		IMavenProjectFacade projectFacade = projectManager.create(currentIProject, new NullProgressMonitor());
		MavenProject mavenProject = projectFacade.getMavenProject(new NullProgressMonitor());

		String buildDirectory = mavenProject.getBuild().getDirectory();
		String finalName = mavenProject.getBuild().getFinalName();
		String finalArtifactPathString = buildDirectory + "/" + finalName + "." + mavenProject.getPackaging();
		
		IPath finalArtifactPath = null;
		finalArtifactPath.append(finalArtifactPathString);

		List classpath = new ArrayList();

		IClasspathEntry[] projectClassPath = this.selectedProject.getRawClasspath();

		String pomPath = projectClassPath[0].getPath().toOSString() + "/bundles/pom.xml";

		String pomPath2 = this.currentIProject.getRawLocation().toOSString() + "/bundles/pom.xml";

		System.out.println(pomPath);
		System.out.println(pomPath);
		
		

		workingCopy.setAttribute("ATTR_CLASSPATH", classpath);
		workingCopy.setAttribute("ATTR_DEFAULT_CLASSPATH", false);

		workingCopy.setAttribute("ATTR_MAIN_TYPE_NAME", typeName);

		workingCopy.setAttribute("ATTR_LOCATION", "locationHelye");

		workingCopy.setAttribute("M2_GOALS", goal);

		workingCopy.setAttribute("M2_USER_SETTINGS", userSettings);

		workingCopy.setAttribute("M2_SKIP_TESTS", skipTests);

		workingCopy.setAttribute("M2_WORKSPACE_RESOLUTION", resolution);

		workingCopy.setAttribute("M2_ATTR_POM_DIR", pomPath2);

		workingCopy.setAttribute(RefreshUtil.ATTR_REFRESH_SCOPE, "${project}");

		workingCopy.setAttribute(RefreshUtil.ATTR_REFRESH_RECURSIVE, true);

		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, systemLibsPath.toString());

		File workingDir = JavaCore.getClasspathVariable("projektNev").append("bin").toFile();
		workingCopy.setAttribute("ATTR_WORKING_DIRECTORY", workingDir.getAbsolutePath());

		ILaunchConfiguration configuration = workingCopy.doSave();
		JavaCore.getClasspathVariable("amire szükség van");
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
