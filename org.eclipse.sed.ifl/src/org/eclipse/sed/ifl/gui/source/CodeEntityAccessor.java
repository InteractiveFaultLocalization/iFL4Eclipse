package org.eclipse.sed.ifl.gui.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sed.ifl.util.exception.ExceptionUtil;

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
		return Collections.unmodifiableList(getProjects().stream()
		.filter(prj -> {try {return prj.isNatureEnabled(Natures.JAVA.getValue());} catch (CoreException e) {throw new RuntimeException(e);}})
		.filter(prj -> ExceptionUtil.tryUnchecked(prj, p->p.isNatureEnabled(Natures.JAVA.getValue())))
		.map(prj -> JavaCore.create(prj))
		.collect(Collectors.toList()));
	}
	
//	public List<IMethod> getMethods(IJavaProject project){
//		Arrays.asList(project.getPackageFragments()).stream()
//		.flatMap(f -> Arrays.asList(f.getCompilationUnits()).stream());
//	}
}
