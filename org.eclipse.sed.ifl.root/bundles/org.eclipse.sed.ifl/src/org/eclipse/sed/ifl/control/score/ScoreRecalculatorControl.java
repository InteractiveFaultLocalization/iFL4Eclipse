package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.modifier.source.PomModifier;
import org.eclipse.sed.ifl.model.score.ScoreListModel;


public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {
	
	private static final String CONFIGURED_POM_XML = "configuredPom.xml";
	private final String instrumenterJar; // TODO: should be a text-like option in iFL preferences
	private File javaHome;
	private final IJavaProject project;
	
	public ScoreRecalculatorControl() {
//		this.instrumenterJar = "java-instrumenter-master" + File.separator + "target" + File.separator + "method-agent-0.0.4-jar-with-dependencies.jar";
		this.instrumenterJar = "method-agent-0.0.4-jar-with-dependencies.jar"; 
//		this.javaHome = "C:/Program Files/Java/jdk1.8.0_202";
//		this.javaHome = System.getenv("JAVA_HOME");

		project = new CodeEntityAccessor().getSelectedProject();
		
		try {
			javaHome = JavaRuntime.getVMInstall(project).getInstallLocation();
		} catch (CoreException e) { // if unable to determine the project's VM install
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (javaHome == null) {
			javaHome = JavaRuntime.getDefaultVMInstall().getInstallLocation();
		}
	}
	
//	private URI getOutputLocation(IJavaProject project) throws JavaModelException {
//		IPath outputLocation = project.getOutputLocation();
//
//		return ResourcesPlugin.getWorkspace().getRoot().findMember(outputLocation).getLocationURI();
//	}
	
	public void recalculate() throws Exception {
		final String jUnitVersion = "4.13";
		final String projectName = project.getProject().getName();
		final String pomPath = project.getProject().getRawLocation().append(CONFIGURED_POM_XML).toOSString();

		final Job job = new Job(projectName + " coverage generation") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {					
					// configuring the selected projet's pom
					PomModifier modifier = new PomModifier(project);
					modifier.editSurefireConfig(instrumenterJar, "hu.szte.sed.JUnitRunListener");
					modifier.updateJUnitVersion(jUnitVersion);
					modifier.savePOM(pomPath);
					
					// running maven test
					InvocationRequest request = new DefaultInvocationRequest();
					request.setPomFile(new File(pomPath));
					request.setGoals(Arrays.asList("clean", "test -Dmaven.test.failure.ignore=true"));
					
					request.setJavaHome(javaHome);
					
					Invoker invoker = new DefaultInvoker();
					System.out.println("Executing request");
					invoker.execute(request);
				} catch (MavenInvocationException e) {
					System.out.println("Request execution failed! (maven)");
					e.printStackTrace();
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Request execution failed! (maven)");
				} catch (Exception e1) {
					e1.printStackTrace();
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "General exception thrown!");
				}
				return Status.OK_STATUS;
			}
		};
		
		job.setPriority(Job.SHORT);
		job.schedule();
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
