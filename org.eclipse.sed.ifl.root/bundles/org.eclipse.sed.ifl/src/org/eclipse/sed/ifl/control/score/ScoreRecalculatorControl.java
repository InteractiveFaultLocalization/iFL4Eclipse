package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.source.Method;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.control.score.ScoreLoaderControl.Entry;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.modifier.source.PomModificationException;
import org.eclipse.sed.ifl.ide.modifier.source.PomModifier;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.scorelib.Formula;
import org.eclipse.sed.ifl.scorelib.Results;
import org.eclipse.sed.ifl.scorelib.ScoreCalculator;
import org.eclipse.sed.ifl.scorelib.ScoreException;
import org.eclipse.sed.ifl.scorelib.ScoreVariables;
import org.eclipse.sed.ifl.scorelib.TrcReader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {

	private static final String CONFIGURED_POM_XML = "configuredPom.xml";
	private static final String JOB_FAMILY = "coverage_generation";
	private final String instrumenterJar; // TODO: should be a text-like option in iFL preferences
	private File javaHome;
	private final IJavaProject project;
	CodeEntityAccessor accessor = new CodeEntityAccessor();
	
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
					// configuring the selected project's pom
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
					
					// deleting coverage folder
					String folder_path = project.getProject().getRawLocation().append("coverage").toOSString();
					FileUtils.deleteDirectory(new File(folder_path));
					
					// running maven test
					try {
						Invoker invoker = new DefaultInvoker();
						System.out.println("Executing request");
						invoker.execute(request);
					} catch (MavenInvocationException e) {
						System.out.println("Request execution failed (mavenInvoker)");
						e.printStackTrace();
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Request execution failed (mavenInvoker)");
					}
					
					
					// load scores from coverage files
					Map<Entry, Score> methodScores = calculateScores();
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							int updatedCount = getModel().loadScore(methodScores);
							MessageDialog.open(MessageDialog.INFORMATION, null,"iFL score loading", updatedCount + " scores are loaded", SWT.NONE);
						}
					});
				} catch (Exception e) {
					System.out.println("Unexpected error during maven tests");
					e.printStackTrace();
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unexpected error during maven tests");
				}
				return Status.OK_STATUS;
			}
			
			@Override
			public boolean belongsTo(Object family) {
		         return JOB_FAMILY.equals(family);
		    }
		};

		IJobManager jobManager = Job.getJobManager();
		Job[] build = jobManager.find(JOB_FAMILY);
		
		if (build.length == 0) {
			job.setPriority(Job.SHORT);
			job.schedule();
		} else {
			MessageDialog.open(MessageDialog.ERROR, null, "Coverage generation", "A project's coverage generation is already running.", SWT.NONE);
		}
	}
	
	public Map<Entry, Score> calculateScores() {
		final String coveragePath = project.getProject().getRawLocation().append("coverage").toOSString();
		final TrcReader trcReader = new TrcReader();
		final ScoreCalculator scoreCalculator = new ScoreCalculator();
		final Formula formula = new Formula();
		
		Random r = new Random();
		Map<IMethodBinding, IMethod> resolvedMethods = accessor.getFilteredResolvedMethods(project);
		
		List<IMethodDescription> methods = resolvedMethods.entrySet().stream()
		.map(method -> new Method(accessor.identityFrom(method), accessor.locationFrom(method), accessor.contextFrom(method, resolvedMethods), accessor.setInteractivity(r)))
		.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
		System.out.printf("%d method found\n", methods.size());

		Map<Short, ScoreVariables> trcMaps = trcReader.readTrc(coveragePath);
		Results results = null;
		try {
			results = (Results) scoreCalculator.calculate(trcMaps, formula);
		} catch (ScoreException e) {
			System.out.println("Unable to calculate scores for trc maps");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Unexpected error during calculating scores for trc maps");
			e.printStackTrace();
		}

		//new Score(results.getMethodScore(currMethod));
		Map<Entry, Score> methodScores = new HashMap<>();
		for (IMethodDescription method : methods) {
			String name = method.getId().toCSVKey();
			String details = method.hasDetailsLink()?method.getDetailsLink():null;
			boolean interactivity = method.isInteractive();
			Entry entry = new Entry(name, details, interactivity);
			methodScores.put(entry, new Score(r.nextDouble()));
		}
		
		return methodScores;
	}

	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void teardown() {
		IJobManager jobManager = Job.getJobManager();
		jobManager.suspend();
		
		Job[] build = jobManager.find(JOB_FAMILY);
		for (Job job : build) {
			if (job.getState() == Job.RUNNING) job.getThread().interrupt();
		}
		
		super.teardown();
	}

}
