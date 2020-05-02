package org.eclipse.sed.ifl.ide.modifier.source;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.model.*;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sed.ifl.ide.accessor.source.WrongSelectionException;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor.Natures;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.*;

public final class PomModifier {

	private final Model POM;
	private final String includeLine;
	private final String excludeLine;
	
	private IResource extractSelection(ISelection sel) {
	      if (!(sel instanceof IStructuredSelection))
	         return null;
	      IStructuredSelection ss = (IStructuredSelection) sel;
	      Object element = ss.getFirstElement();
	      if (element instanceof IResource)
	         return (IResource) element;
	      if (!(element instanceof IAdaptable))
	         return null;
	      IAdaptable adaptable = (IAdaptable)element;
	      Object adapter = adaptable.getAdapter(IResource.class);
	      return (IResource) adapter;
	   }

	
	private IJavaProject getSelectedProject() {
		ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ISelectionService.class);
		ISelection selection = service.getSelection();
		if (selection != null) {
			IResource resource = extractSelection(selection);
			if (resource == null) {
				throw new WrongSelectionException("Non-resources selected.");
			}
			IProject project = resource.getProject();
			if (EU.tryUnchecked(() -> project.isNatureEnabled(Natures.JAVA.getValue()))) {
				return JavaCore.create(project);
			}
			else {
				throw new WrongSelectionException("Non-Java project are selected.");
			}
		}
		else {
			throw new WrongSelectionException("Nothing is selected.");
		}
	}	
	
	private String extractPath(IJavaProject actualProject) {
		IPath path = actualProject.getPath();
		String pathString = path.toOSString() +"/pom.xml0";
		return pathString;
	}
	
	
	private Model ParsePOM(String POMPath) throws Exception {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			File POMFile = new File(POMPath);
			Model model = reader.read(new FileReader(POMFile)); // parsing XML
			return model;
		} catch (Exception e) {
			return null;
		}
	}

	public PomModifier() throws Exception {
		IJavaProject actualProject = getSelectedProject();
		String POMPath = extractPath(actualProject);
		Model model = ParsePOM(POMPath);
		this.POM = model;
		this.includeLine = model.getBuild().getOutputDirectory();
		this.excludeLine = model.getBuild().getTestOutputDirectory();
	}

	public Model getPOM() {
		return POM;
	}

	public String getIncludeLine() {
		return includeLine;
	}

	public String getExcludeLine() {
		return excludeLine;
	}

	public void updateJUnitVersion(String newVersion) {
		List<Dependency> dependencies = this.POM.getDependencies();
		Iterator<Dependency> dIterator = dependencies.iterator();
		while (dIterator.hasNext()) {
			Dependency current = dIterator.next();
			if (current.getArtifactId().contentEquals("junit")) { // jUnit was found
				String oldVersion = current.getVersion();
				String majorOldVersionString = oldVersion.split("\\.")[0];
				String minorOldVersionString = oldVersion.split("\\.")[1];
				String majorNewVersionString = newVersion.split("\\.")[0];
				String minorNewVersionString = newVersion.split("\\.")[1];
				int majorOldVersion = Integer.parseInt(majorOldVersionString);
				int minorOldVersion = Integer.parseInt(minorOldVersionString);
				int majorNewVersion = Integer.parseInt(majorNewVersionString);
				int minorNewVersion = Integer.parseInt(minorNewVersionString);
				if (majorOldVersion < majorNewVersion) { // checking if version is lower, than 4.12
					current.setVersion("newVersion");
				} else if (majorOldVersion >= majorNewVersion && minorOldVersion < minorNewVersion) {
					current.setVersion("newVersion");
				}
				break;
			}
		}
		this.POM.setDependencies(dependencies);
	}

	public void editSurefireConfig(String agentFilePath, String listener) {
		Build mBuild = this.POM.getBuild();
		PluginManagement pManagement = mBuild.getPluginManagement();
		List<Plugin> pList = pManagement.getPlugins();
		Plugin surefire = pList.get(0);
		int pNumber = 0;
		for (int i = 0; i < pList.size(); i++) {
			if (pList.get(i).getArtifactId().contentEquals("maven-surefire-plugin")) {
				surefire = pList.get(i); // found surefire
				pNumber = i;
				break;
			}
		}

		Xpp3Dom conf = new Xpp3Dom("configuration");
		Xpp3Dom argline = new Xpp3Dom("argLine");
		Xpp3Dom properties = new Xpp3Dom("properties");
		Xpp3Dom property = new Xpp3Dom("property");
		Xpp3Dom name = new Xpp3Dom("name");
		Xpp3Dom value = new Xpp3Dom("value");

		argline.setValue("-javaagent:file://" + agentFilePath + "=includes=" + this.includeLine + ".*,excludes="
				+ this.excludeLine + ".*");
		conf.addChild(argline);
		conf.addChild(properties);
		properties.addChild(property);
		name.setValue("listener");
		property.addChild(name);
		value.setValue(listener);
		property.addChild(value);

		Xpp3Dom newConfiguration = (Xpp3Dom) surefire.getConfiguration();
		if (newConfiguration.getChildCount() == 0) {
			newConfiguration = conf;
		} else {
			Xpp3Dom[] children = newConfiguration.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getName().compareTo("argLine") == 0) {
					String arguments = children[i].getValue();
					arguments = arguments + " " + argline.getValue();
					argline.setValue(arguments);
					newConfiguration.removeChild(i);
					newConfiguration.addChild(argline);
				} else {
					newConfiguration.addChild(argline);
				}

				if (children[i].getName().compareTo("properties") == 0) {
					children[i].addChild(property);
					properties = children[i];
					newConfiguration.removeChild(i);
					newConfiguration.addChild(properties);
				}

				else {
					newConfiguration.addChild(properties);

				}

			}
		}
		surefire.setConfiguration(newConfiguration); // applying changes to input model
		pList.set(pNumber, surefire);
		pManagement.setPlugins(pList);
		mBuild.setPluginManagement(pManagement);
		this.POM.setBuild(mBuild);
	}

	public void savePOM(String newPOMPath) throws FileNotFoundException {
		try {
			MavenXpp3Writer writer = new MavenXpp3Writer();
			File newPOMFile = new File(newPOMPath);
			FileOutputStream f = new FileOutputStream(newPOMFile);
			writer.write(f, this.POM);
			f.close();

		} catch (Exception FileNotFoundException) {

		}
	}

}
