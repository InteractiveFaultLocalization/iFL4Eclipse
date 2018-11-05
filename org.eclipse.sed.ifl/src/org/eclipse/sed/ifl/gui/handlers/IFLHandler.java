package org.eclipse.sed.ifl.gui.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewReference;
import org.eclipse.sed.ifl.Activator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sed.ifl.gui.views.IFLMainView;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class IFLHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		// Activator.getDefault()
		// marker annotation spec
		// ICompelation unit
		ArrayList<IMethod> listOfMethods = new ArrayList<>();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject prj : projects) {
			try {
				if (prj.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
					IJavaProject javaPrj = JavaCore.create(prj);
					for (IPackageFragment pkg : javaPrj.getPackageFragments()) {
						if (pkg.getKind() == IPackageFragmentRoot.K_SOURCE) {
							for (ICompilationUnit unit : pkg.getCompilationUnits()) {
								for (IType type : unit.getAllTypes()) {
									for (IMethod method : type.getMethods()) {
										String methodAsString = method.getElementName() + method.getSignature();
										System.out.println(methodAsString);
										listOfMethods.add(method);
									}
								}
							}
						}
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		try {
			window.getActivePage().showView(IFLMainView.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		for (IViewReference viewRef : window.getActivePage().getViewReferences()) {
			if (IFLMainView.ID.equals(viewRef.getId())) {
				IFLMainView view = (IFLMainView) viewRef.getView(false);
				view.setMethodList(listOfMethods);
			}
		}
		return null;
	}
}
