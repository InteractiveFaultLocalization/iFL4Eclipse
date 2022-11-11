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
import org.eclipse.core.runtime.SubMonitor;
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
		final String pomPath = project.getProject().getLocation().append(CONFIGURED_POM_XML).toOSString();
		
		final Job job = new Job(projectName + " score recalculation") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

				try {
					// configuring the selected project's pom
					try {
						subMonitor.setTaskName("Configuring the POM");
						PomModifier modifier = new PomModifier(project);
	modifier.editSurefireConfig(Activator.getDefault().getPreferenceStore().getString("instrumenter"), "hu.szte.sed.JUnitRunListener");
						modifier.updateJUnitVersion(jUnitVersion);
						modifier.savePOM(pomPath);
						subMonitor.worked(10);
					} catch (Exception e) {
						throw new PomModificationException("Unexpected error during POM modification", e);
					}

					if (subMonitor.isCanceled()) {
			            return Status.CANCEL_STATUS;
			        }

					subMonitor.setTaskName("Deleting old coverage data");
					// deleting coverage folder
					String folder_path = project.getProject().getLocation().append("coverage").toOSString();
					FileUtils.deleteDirectory(new File(folder_path));
					subMonitor.worked(25);

					if (subMonitor.isCanceled()) {
			            return Status.CANCEL_STATUS;
			        }

					// running maven test
					try {
						subMonitor.setTaskName("Running Maven tests");
						// configuring maven test
						InvocationRequest request = new DefaultInvocationRequest();
						request.setPomFile(new File(pomPath));
						request.setGoals(Arrays.asList("clean", "test -Dmaven.test.failure.ignore=true"));
						request.setJavaHome(javaHome);

						Invoker invoker = new DefaultInvoker();
						System.out.println("Executing request");
						invoker.execute(request);
						subMonitor.worked(35);
					} catch (MavenInvocationException e) {
						throw new Exception("Request execution failed (MavenInvoker)", e);
					}

					if (subMonitor.isCanceled()) {
			            return Status.CANCEL_STATUS;
			        }
					
					try {
						subMonitor.setTaskName("Calculating scores");

						Map<ScoreLoaderEntry, Score> scores = calculateScores();

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								int updatedCount = getModel().loadScore(scores);

								MessageDialog.openInformation(null, "Score recalculation", updatedCount + " scores have been loaded");
							}
						});

						subMonitor.worked(30);
					} catch (Exception e) {
						throw new Exception("Unexpected error during score calculation", e);
					}
				} catch (Exception e) {
					System.out.println("Unexpected error during score recalculation");
					e.printStackTrace();

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(null, "Score recalculation", "Unexpected error during score recalculation");
						}
					});
					return Status.error("Unexpected error during score recalculation");
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
			job.setPriority(Job.LONG);
			job.setUser(true);
			job.schedule();
		} else {
			MessageDialog.open(MessageDialog.ERROR, null, "Coverage generation", "A project's coverage generation is already running.", SWT.NONE);
		}
	}
	
	public Map<ScoreLoaderEntry, Score> calculateScores() throws Exception {
		final IPath coveragePath = project.getProject().getLocation().append("coverage");
		final IPath outputPath = project.getProject().getLocation();
		final Formula sbflFormula = Formulas.valueOf(Activator.getDefault().getPreferenceStore().getString("formula").toUpperCase()).getFormula();
		NanoWatch watch1 = new NanoWatch("calculating scores");
		try {
			CSVReportWriter report = new CSVReportWriter(coveragePath.toOSString(), outputPath.toOSString(), sbflFormula);
			report.createReport();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(watch1);
		NanoWatch watch2 = new NanoWatch("loading scores from csv");
		File file = new File(outputPath.append(Activator.getDefault().getPreferenceStore().getString("formula") + ".csv").toOSString());
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
		System.out.println(watch2);
		return loadedScores;
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
