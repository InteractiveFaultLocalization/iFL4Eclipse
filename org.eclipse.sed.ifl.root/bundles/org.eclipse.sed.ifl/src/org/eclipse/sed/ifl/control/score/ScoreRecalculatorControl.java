package org.eclipse.sed.ifl.control.score;

import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.model.score.ScoreListModel;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
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

	/*
	 * public void listWrite(int depth, int children, IConfigurationElement[]
	 * elemList, IConfigurationElement[] childList, IConfigurationElement[]
	 * tempList) throws IOException { for (int i = 0; i < elemList.length; i++) { if
	 * (children == 0) { for (int x = depth; x > 0; x--) { System.out.print("-"); }
	 * System.out.print(elemList[i].getName() + " == " + elemList[i].getValue()); }
	 * else { tempList=elemList; elemList=childList;
	 * childList=elemList[i].getChildren(); children=childList.length; depth++;
	 * listWrite(depth,children,elemList,childList,tempList); } }
	 * 
	 * }
	 */ // This is not gonna work...

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
			if (points[i].getSimpleIdentifier().equals("launchConfigurationTypes")) {
				elements = points[i].getConfigurationElements();
			}
		}
		System.out.println("Configs are:");
		for (int i = 0; i < elements.length; i++) {
			System.out.println(elements[i].getNamespaceIdentifier());
		}

		/*
		 * try (PrintStream printStream = new PrintStream(new File("points.txt"))) { int
		 * depth = 0; int children = 0; IConfigurationElement[] elemList = new
		 * IConfigurationElement[1000]; IConfigurationElement[] childList = new
		 * IConfigurationElement[1000]; IConfigurationElement[] tempList = new
		 * IConfigurationElement[1000]; for (int i = 0; i < points.length; i++) {
		 * printStream.println(" ******* ");
		 * printStream.println(points[i].getSimpleIdentifier());
		 * printStream.println(" "); elemList = points[i].getConfigurationElements();
		 * tempList = points[i].getConfigurationElements(); childList =
		 * elemList[i].getChildren(); depth = 1; children = childList.length;
		 * listWrite(depth, children, elemList, childList, tempList); }
		 * 
		 * } catch (IOException e) { System.err.println("Unexpected error occured: " +
		 * e.getMessage()); }
		 */
		// This is getting too convulated
		
		/*
		 *  DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElementNS(points[0]);
            doc.appendChild(mainRootElement);
 
           
            mainRootElement.appendChild());
            mainRootElement.appendChild());
            mainRootElement.appendChild());
 
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
            DOMSource source = new DOMSource(doc);
            StreamResult console = new StreamResult(System.out);
            transformer.transform(source, console);
 
           
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
		 
		 */

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager(); //Maven pluginnak és JUnitnak lekérni
		String configType = elements[0].getValue();
		ILaunchConfigurationType type = launchManager
				.getLaunchConfigurationType("org.eclipse.m2e.Maven2LaunchConfigurationType");
		ILaunchConfiguration[] configurations = launchManager.getLaunchConfigurations(type); //pluginokban van dedikált launchManager

		for (int i = 0; i < configurations.length; i++) {
			ILaunchConfiguration configuration = configurations[i];
			if (!configuration.getName().equals("Maven IFL build")) {
				System.out.println("");
				System.out.println("A configuration neve: " +configuration.getName());
				//configuration.delete();
				//break;
			}
		}

		IVMInstall jre = JavaRuntime.getDefaultVMInstall();
		File jdkHome = jre.getInstallLocation();
		IPath toolsPath = (IPath)  Paths.get(jdkHome.getAbsolutePath() + "lib" + "tools.jar");
		IRuntimeClasspathEntry toolsEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(toolsPath); 
		toolsEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "Maven IFL build");
		workingCopy.setAttribute("ATTR_VM_INSTALL_NAME", jre.getName());
		workingCopy.setAttribute("ATTR_VM_INSTALL_TYPE", jre.getVMInstallType().getId());
		workingCopy.setAttribute("org.eclipse.jdt.launching.JRE_CONTAINER",
				"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/jre1.8.0_202");

		IPath agentPath = (IPath) Paths
				.get("JavaMethodInstrumenterAgent" + "bin" + "JavaMethodInstrumenterAgent-jar-with-dependencies.jar");
		IRuntimeClasspathEntry agentEntry = JavaRuntime.newVariableRuntimeClasspathEntry(agentPath); //talán nem szükséges

		IPath systemLibsPath = (IPath) Paths.get(JavaRuntime.JRE_CONTAINER);
		IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath,
				IRuntimeClasspathEntry.STANDARD_CLASSES);

		List classpath = new ArrayList();
		classpath.add(toolsEntry.getMemento());
		classpath.add(agentEntry.getMemento());
		classpath.add(systemLibsEntry.getMemento());
		workingCopy.setAttribute("ATTR_CLASSPATH", classpath);
		workingCopy.setAttribute("ATTR_DEFAULT_CLASSPATH", false);

		workingCopy.setAttribute("ATTR_MAIN_TYPE_NAME", "org.eclipse.sed.ifl");

		workingCopy.setAttribute("ATTR_LOCATION", locations[0]);

		workingCopy.setAttribute("M2_GOALS", "clean install");

		workingCopy.setAttribute("M2_USER_SETTINGS", "ExtensionPointbolKinyerni");

		workingCopy.setAttribute("M2_SKIP_TESTS", false);

		workingCopy.setAttribute("M2_WORKSPACE_RESOLUTION", false);
		
		

		File workingDir = JavaCore.getClasspathVariable("projektNev").append("bin").toFile();
		workingCopy.setAttribute("ATTR_WORKING_DIRECTORY", workingDir.getAbsolutePath());

		ILaunchConfiguration configuration = workingCopy.doSave();
		 DebugUITools.launch(configuration, ILaunchManager.RUN_MODE); //Maven vagy JUnit-on keresztül.
		 //Mavenbol classpath kinyerése

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
