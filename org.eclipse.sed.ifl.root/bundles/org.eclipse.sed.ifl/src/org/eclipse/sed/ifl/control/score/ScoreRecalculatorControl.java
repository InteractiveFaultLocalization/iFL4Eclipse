package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sed.ifl.control.ViewlessControl;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor;
import org.eclipse.sed.ifl.ide.accessor.source.WrongSelectionException;
import org.eclipse.sed.ifl.ide.accessor.source.CodeEntityAccessor.Natures;
import org.eclipse.sed.ifl.ide.modifier.source.PomModifier;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;


public class ScoreRecalculatorControl extends ViewlessControl<ScoreListModel> {

	/*private final String instrumenterJar;
	private final String javaHome;

	public String getInstrumenterJar() {
		return instrumenterJar;
	}

	public String getJavaHome() {
		return javaHome;
	}*/
	
	public ScoreRecalculatorControl() {}
	
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
	
	//Should this be in the codeEntityAccessor.java?
	private IProject getSelectedProject() {
		ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ISelectionService.class);
		ISelection selection = service.getSelection();
		if (selection != null) {
			CodeEntityAccessor sourceAccessor = new CodeEntityAccessor(); 
			//IResource resource = sourceAccessor.extractSelection(selection);
			IResource resource = extractSelection(selection);
			if (resource == null) {
				throw new WrongSelectionException("Non-resources selected.");
			}
			IProject project = resource.getProject();
			if (EU.tryUnchecked(() -> project.isNatureEnabled(Natures.JAVA.getValue()))) {
				return project;
			} else {
				throw new WrongSelectionException("Non-Java project are selected.");
			}
		} else {
			throw new WrongSelectionException("Nothing is selected.");
		}
	}
	
	public void recalculate() throws Exception {
		IProject project = getSelectedProject();

		String jUnitVersion = "4.13";
		String projectName = project.getName();
		String projectPath = project.getRawLocation().toOSString();
		
		
		//TODO: file.io-ba van-e rá feautre
		List<String> workspacePathList = new ArrayList<String>(Arrays.asList(projectPath.split(Pattern.quote(File.separator))));
		workspacePathList.remove(workspacePathList.size() - 1);	// remove project name from the path
		
		StringJoiner joiner = new StringJoiner(File.separator);
		for (String item : workspacePathList) {
			joiner.add(item);
		}
		String workspacePath = joiner.toString();
		
		final Job job = new Job(projectName + " coverage generation") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				try {
					//TODO: instrumenterJar, setJavaHome adattagként 
					
					// configuring the selected projet's pom
					PomModifier modifier = new PomModifier(projectPath + File.separator + "pom.xml");
					String instrumenterJar = File.separator + "java-instrumenter-master" + File.separator + "target" + File.separator + "method-agent-0.0.4-jar-with-dependencies.jar";
					modifier.editSurefireConfig(workspacePath + instrumenterJar, "hu.szte.sed.JUnitRunListener");
					modifier.updateJUnitVersion(jUnitVersion);
					modifier.savePOM(projectPath + File.separator + "configuredPom.xml");
					
					
					// running maven test
					InvocationRequest request = new DefaultInvocationRequest();
					request.setPomFile( new File(projectPath + File.separator + "configuredPom.xml") );
					request.setGoals( Arrays.asList("clean", "test -Dmaven.test.failure.ignore=true") );
					
					request.setJavaHome( new File("C:/Program Files/Java/jdk1.8.0_202") );
					
					Invoker invoker = new DefaultInvoker();
					System.out.println("Executing request");
					invoker.execute( request );
				} catch (MavenInvocationException e) {
					System.out.println("Request execution failed! (maven)");
					e.printStackTrace();
					return new Status(IStatus.ERROR, "uniqueIdentifier1", "Request execution failed! (maven)");
				} catch (Exception e1) {
					// TODO job statusoknak utána nézni
					e1.printStackTrace();
					return new Status(IStatus.ERROR, "uniqueIdentifier2", "General exception trhown!");
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
