package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.modifier.source.PomModificationException;
import org.eclipse.sed.ifl.ide.modifier.source.PomModifier;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.scorelib.IFormula;
import org.eclipse.sed.ifl.scorelib.IResults;
import org.eclipse.sed.ifl.scorelib.ScoreCalculator;
import org.eclipse.sed.ifl.scorelib.ScoreException;
import org.eclipse.sed.ifl.scorelib.TrcReader;
import org.eclipse.sed.ifl.scorelib.scoreVariables;


public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {
	
	private static final String CONFIGURED_POM_XML = "configuredPom.xml";
	private final String instrumenterJar; // TODO: should be a text-like option in iFL preferences
	private File javaHome;
	private final IJavaProject project;
	
	public ScoreRecalculatorControl(IJavaProject selectedProject) {
		this.instrumenterJar = "method-agent-0.0.4-jar-with-dependencies.jar";

		project = selectedProject;
		
		try {
			javaHome = JavaRuntime.getVMInstall(project).getInstallLocation();
		} catch (CoreException e) {
			System.out.println("Unable to determine the project's VM install");
			e.printStackTrace();
		}
		
		if (javaHome == null) {
			javaHome = JavaRuntime.getDefaultVMInstall().getInstallLocation();
		}
		
		if (javaHome == null) {
			throw new VMNotFoundException("Failed to determine the project's VM install");
		}
	}
	
	public void recalculate() throws Exception {
		final String jUnitVersion = "4.13";
		final String projectName = project.getProject().getName();
		final String pomPath = project.getProject().getRawLocation().append(CONFIGURED_POM_XML).toOSString();

		final Job job = new Job(projectName + " coverage generation") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {				
				try {
					// configuring the selected projet's pom
					try {
						PomModifier modifier = new PomModifier(project);
						modifier.editSurefireConfig(instrumenterJar, "hu.szte.sed.JUnitRunListener");
						modifier.updateJUnitVersion(jUnitVersion);
						modifier.savePOM(pomPath);
					} catch (Exception e) {
						throw new PomModificationException("Unexpected error during pom modification", e);
					}
					
					// configuring maven test
					InvocationRequest request = new DefaultInvocationRequest();
					request.setPomFile(new File(pomPath));
					request.setGoals(Arrays.asList("clean", "test -Dmaven.test.failure.ignore=true"));
					request.setJavaHome(javaHome);
					
					//calculateScores();
					// running maven test
					/*try {
						Invoker invoker = new DefaultInvoker();
						System.out.println("Executing request");
						invoker.execute(request);
					} catch (MavenInvocationException e) {
						System.out.println("Request execution failed (mavenInvoker)");
						e.printStackTrace();
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Request execution failed (mavenInvoker)");
					}*/
				} catch (Exception e) {
					System.out.println("Unexpected error during maven tests");
					e.printStackTrace();
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unexpected error during maven tests");
				}
				return Status.OK_STATUS;
			}
		};
		
		job.setPriority(Job.SHORT);
		job.schedule();
	}	
	
	public void calculateScores() {
		final String coveragePath = project.getProject().getRawLocation().append("coverage").toOSString();
		final TrcReader trcReader = new TrcReader();
		final ScoreCalculator scoreCalculator = new ScoreCalculator();
		final IFormula formula = null; //new IFormula();
		
		Map<Short, scoreVariables> trcMaps = trcReader.readTrc(coveragePath);
		/*try {
			IResults results = scoreCalculator.calculate(trcMaps, formula);
		} catch (ScoreException e) {
			System.out.println("Unable to calculate scores for the trc maps");
			e.printStackTrace();
		}*/
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
