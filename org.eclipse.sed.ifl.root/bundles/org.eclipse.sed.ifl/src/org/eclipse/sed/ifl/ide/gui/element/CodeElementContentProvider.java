package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.sed.ifl.ide.gui.treeview.ElementNode;

public class CodeElementContentProvider implements ILazyTreeContentProvider {
	
	private TreeViewer viewer;
	
	public CodeElementContentProvider(TreeViewer treeViewer) {
		super();
		this.viewer = treeViewer;
	}

	@Override
	public void updateElement(Object parent, int index) {
		ElementNode element = ((ElementNode) parent).getChildren().get(index);
		viewer.replace(parent, index, element);
		viewer.setChildCount(element, element.getChildren().size());
	}

	@Override
	public void updateChildCount(Object element, int currentChildCount) {
		viewer.setChildCount(element, ((ElementNode) element).getChildren().size());		
	}

	@Override
	public Object getParent(Object element) {
		return ((ElementNode) element).getParent();
	}
}
