package org.eclipse.sed.ifl.ide.accessor.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

public class CodeEntityAccessor {
	IWorkspace currentWorkspace = ResourcesPlugin.getWorkspace();
	
	public List<IProject> getProjects() {
		return Collections.unmodifiableList(Arrays.asList(currentWorkspace.getRoot().getProjects()));
	}
	
	public enum Natures {
		JAVA("org.eclipse.jdt.core.javanature");
		
		private String value;
		
		Natures(String value) {
			this.value = value; 
		}
		
		public String getValue() {
			return value;
		}
	}
	
	public List<IJavaProject> getJavaProjects() {
		return getProjects().stream()
		.filter(prj -> EU.tryUnchecked(() -> prj.isNatureEnabled(Natures.JAVA.getValue())))
		.map(prj -> JavaCore.create(prj))
		.collect(Collectors.toUnmodifiableList());
	}
	
	public List<IMethod> getMethods(IType type) {
		return EU.tryUnchecked(() -> Stream.of(type.getMethods()))
		.collect(Collectors.toUnmodifiableList());
	}
	
	public List<IMethod> getMethods(IJavaProject project) {
		return getUnits(project).stream()
		.flatMap(unit -> EU.tryUnchecked(() -> Stream.of(unit.getAllTypes())))
		.flatMap(type -> getMethods(type).stream())
		.collect(Collectors.toUnmodifiableList());
	}

	public List<ICompilationUnit> getUnits(IJavaProject project) {
		return Stream.of(EU.tryUnchecked(() -> project.getPackageFragments()))
		.filter(fragment -> EU.tryUnchecked(() -> fragment.getKind() == IPackageFragmentRoot.K_SOURCE))
		.flatMap(fragment -> EU.tryUnchecked(() -> Stream.of(fragment.getCompilationUnits())))
		.collect(Collectors.toUnmodifiableList());
	}

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
	
	public IJavaProject getSelectedProject() {
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

	public List<IMethodBinding> getResolvedMethods(IJavaProject project) {
		var methods = getMethods(project);		
		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setProject(project);
		IMethod[] ms = new IMethod[methods.size()];
		methods.toArray(ms);
		var resolveds = Stream.of(parser.createBindings(ms, new NullProgressMonitor()))
		.map(method -> (IMethodBinding)method)
		.collect(Collectors.toUnmodifiableList());
		return resolveds;
	}
	
	public String getSignature(IMethodBinding method) {
		String key = method.getKey();
		String paramsAndReturn = key.replaceAll(".*(\\(.*\\)[^\\|]*).*", "$1");

		StringBuilder signature = new StringBuilder();
		signature.append(method.getDeclaringClass().getQualifiedName()).append('.')
				.append(method.isConstructor() ? "<init>" : method.getName()).append(paramsAndReturn);

		return signature.toString();
	}
}
