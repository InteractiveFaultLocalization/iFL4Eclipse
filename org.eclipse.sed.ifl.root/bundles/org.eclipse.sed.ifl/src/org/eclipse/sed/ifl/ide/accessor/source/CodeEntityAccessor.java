package org.eclipse.sed.ifl.ide.accessor.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sed.ifl.commons.model.source.CodeChunkLocation;
import org.eclipse.sed.ifl.commons.model.source.MethodIdentity;
import org.eclipse.sed.ifl.commons.model.source.Position;
import org.eclipse.sed.ifl.ide.Activator;
import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
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
		.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}
	
	public List<IMethod> getMethods(IType type) {
		return EU.tryUnchecked(() -> Stream.of(type.getMethods()))
		.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}
		
	public List<IMethod> getMethods(IJavaProject project) {
		return getUnits(project).stream()
		.flatMap(unit -> EU.tryUnchecked(() -> Stream.of(unit.getAllTypes())))
		.flatMap(type -> getMethods(type).stream())
		.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}

	public List<ICompilationUnit> getUnits(IJavaProject project) {
		return Stream.of(EU.tryUnchecked(() -> project.getPackageFragmentRoots()))
		.filter(fragment -> EU.tryUnchecked(() -> fragment.getKind() == IPackageFragmentRoot.K_SOURCE))
		.filter(fragment -> EU.tryUnchecked(() ->!fragment.getRawClasspathEntry().isTest()))
		.flatMap(fragment -> EU.tryUnchecked(() -> Stream.of(fragment.getChildren())))
		.filter(javaElement -> javaElement instanceof IPackageFragment)
		.map(javaElement -> (IPackageFragment)javaElement)
		.flatMap(fragment -> EU.tryUnchecked(() -> Stream.of(fragment.getCompilationUnits())))
		.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
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
			if (!project.isOpen()) {
				throw new WrongSelectionException("The selected project is closed. Please open the project before starting an iFL session.");
			}
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

	
	private String interactivity;
	
	public String getInteractivity() {
		return this.interactivity;
	}
	
	public boolean setInteractivity(Random r) {
		boolean rValue = r.nextBoolean();
		switch(Activator.getDefault().getPreferenceStore().getString("interactivity")) {
		case "random" : interactivity = "random"; return rValue;
		case "allTrue" : interactivity = "true"; return true;
		case "allFalse" : interactivity = "false"; return false;
		default : return rValue;
		}
	}
	
	public MethodIdentity identityFrom(Entry<IMethodBinding, IMethod> method) {
		return new MethodIdentity(method.getKey().getName(), getSignature(method.getKey()),
				method.getKey().getDeclaringClass().getName(), method.getKey().getReturnType().getName(),
				method.getKey().getKey());
	}

	public CodeChunkLocation locationFrom(Entry<IMethodBinding, IMethod> method) {
		return new CodeChunkLocation(
				EU.tryUnchecked(() -> method.getValue().getUnderlyingResource().getLocation().toOSString()),
				new Position(EU.tryUnchecked(() -> method.getValue().getSourceRange().getOffset())),
				new Position(EU.tryUnchecked(() -> method.getValue().getSourceRange().getOffset()
						+ method.getValue().getSourceRange().getLength())));
	}
	
	public List<MethodIdentity> contextFrom(Entry<IMethodBinding, IMethod> method,
			Map<IMethodBinding, IMethod> others) {
		return getSiblings(method, others).entrySet().stream()
				.filter(contextMethod -> !contextMethod.getValue().equals(method.getValue()))
				.map(contextMethod -> identityFrom(contextMethod))
				.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}
	
	@Deprecated
	public Map<IMethodBinding, IMethod> getResolvedMethods(IType type, IJavaProject project) {
		return resolve(project, getMethods(type));
	}
	
	public Map<IMethodBinding, IMethod> getResolvedMethods(IJavaProject project, Predicate<? super IMethod> preFilter, Predicate<? super Entry<IMethodBinding, IMethod>> postFilter) {
		NanoWatch watch = new NanoWatch("resolve method");
		List<IMethod> methods = getMethods(project).stream()
			.filter(preFilter)
			.collect(Collectors.toList());
		Map<IMethodBinding, IMethod> result = resolve(project, methods).entrySet().stream()
			.filter(postFilter)
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		System.out.println(watch);
		return result;
	}
	
	public Map<IMethodBinding, IMethod> getFilteredResolvedMethods(IJavaProject project) {
		Predicate<? super Entry<IMethodBinding, IMethod>> unrelevantFilter = entry -> {
			if (Modifier.isAbstract(entry.getKey().getModifiers())) {
				return false;
			}
			if (entry.getKey().getDeclaringClass().isInterface()) {
				return false;
			}
			return true;
		};
		Predicate<? super IMethod> preUnrelevantFilter = method -> {
			try {
				if (method.getDeclaringType().isClass()) {
					return true;
				}
			} catch (JavaModelException e) {
				return false;
			}
			return false;
		};
		return getResolvedMethods(project, preUnrelevantFilter, unrelevantFilter);
	}
	
	public Map<IMethodBinding, IMethod> getSiblings(Entry<IMethodBinding, IMethod> me, Map<IMethodBinding, IMethod> others) {
		return others.entrySet().stream()
		.filter(method -> method.getValue().getDeclaringType().equals(me.getValue().getDeclaringType()))
		.collect(Collectors.collectingAndThen(Collectors.toMap(method -> method.getKey(), method -> method.getValue()),Collections::unmodifiableMap));
	}
	
	private Map<IMethodBinding, IMethod> resolve(IJavaProject project, List<IMethod> methods) {
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setProject(project);
		IMethod[] ms = new IMethod[methods.size()];
		methods.toArray(ms);

		Map<IMethodBinding, IMethod> resolveds = Stream.of(parser.createBindings(ms, new NullProgressMonitor()))
		.filter(x -> x != null)
		.map(method -> (IMethodBinding)method)
		.collect(Collectors.collectingAndThen(Collectors.toMap(
				method -> method,
				method -> {
					IJavaElement element = method.getJavaElement();
					if (element instanceof IMethod) {
						return (IMethod)element;
					}
					else {
						return null;					
					}
				}),Collections::unmodifiableMap));
		System.out.println("Number of all code elements: " + methods.size());
		System.out.println("Number of unresolved code elements: " + (methods.size() - resolveds.size()));
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
