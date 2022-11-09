package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.modifier.source.PomModificationException;
import org.eclipse.sed.ifl.ide.modifier.source.PomModifier;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.ScoreLoaderEntry;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import hu.szte.inf.sed.fl.CSVReportWriter;
import hu.szte.inf.sed.fl.formula.Formula;
import hu.szte.inf.sed.fl.formula.sbfl.Formulas;


public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {

	private static final String UNIQUE_NAME_HEADER = "name";
	private static final String SCORE_HEADER = "score";
	private static final String INTERACTIVITY_HEADER = "interactive";
	private static final String DETAILS_LINK_HEADER = "details";
	private static final CSVFormat CSVFORMAT = CSVFormat.DEFAULT.withQuote('"').withDelimiter(';').withFirstRecordAsHeader();
	private static final String CONFIGURED_POM_XML = "configuredPom.xml";
	private static final String JOB_FAMILY = "coverage_generation";
	private final String instrumenterJar = Activator.getDefault().getPreferenceStore().getString("instrumenter");
	private final String formula = Activator.getDefault().getPreferenceStore().getString("formula");
	private File javaHome;
	private final IJavaProject project;
	CodeEntityAccessor accessor = new CodeEntityAccessor();
	
	public ScoreRecalculatorControl(IJavaProject selectedProject) {
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

	public void recalculate() {
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
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Request execution failed (MavenInvoker)");
					}
					
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							try {
								int updatedCount = calculateScores();

								MessageDialog.open(MessageDialog.INFORMATION, null, "iFL score loading", updatedCount + " scores have been loaded", SWT.NONE);
							} catch (Exception e) {
								MessageDialog.open(MessageDialog.ERROR, null, "Error during iFL score loading", "The plug-in was unable to open the CSV file. Please check if the CSV file is corrupted or is not properly formatted.", SWT.NONE);
								e.printStackTrace();
							}
						}
					});
				} catch (Exception e) {
					System.out.println("Unexpected error during score recalculation");
					e.printStackTrace();
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unexpected error during score recalculation");
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
	
	public int calculateScores() throws Exception {
		final IPath coveragePath = project.getProject().getRawLocation().append("coverage");
		final IPath outputPath = project.getProject().getRawLocation();
		final Formula sbflFormula = Formulas.valueOf(formula.toUpperCase()).getFormula();
		
		try {
			CSVReportWriter report = new CSVReportWriter(coveragePath.toOSString(), outputPath.toOSString(), sbflFormula);

			report.createReport();
		} catch (IOException e) {
			e.printStackTrace();
		}

		NanoWatch watch = new NanoWatch("loading scores from csv");
		File file = new File(outputPath.append(formula + ".csv").toOSString());
		CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFORMAT);
		int recordCount = 0;
		Map<ScoreLoaderEntry, Score> loadedScores = new HashMap<>();
		for (CSVRecord record : parser) {
			recordCount++;
			String name = record.get(UNIQUE_NAME_HEADER);
			double value = Double.parseDouble(record.get(SCORE_HEADER));

			boolean interactivity = !(record.isSet(INTERACTIVITY_HEADER) && record.get(INTERACTIVITY_HEADER).equals("no"));
			ScoreLoaderEntry entry = new ScoreLoaderEntry(name, record.isSet(DETAILS_LINK_HEADER)?record.get(DETAILS_LINK_HEADER):null, interactivity);
			Score score = new Score(value);
			loadedScores.put(entry, score);
		}
		int updatedCount = getModel().loadScore(loadedScores);
		System.out.println(watch);
		return updatedCount;
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
