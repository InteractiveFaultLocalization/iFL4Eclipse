package org.eclipse.sed.ifl.ide.gui.treeview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.control.score.Score;

public class ElementNode {

	private ElementNode parent;
	private List<ElementNode> children = new ArrayList<>();
	
	private Entry<IMethodDescription, Score> codeElement;

	public ElementNode(ElementNode parent, List<ElementNode> children, Entry<IMethodDescription, Score> codeElement) {
		super();
		this.parent = parent;
		//this.children = children;
		this.codeElement = codeElement;
	}
	
	public ElementNode getParent() {
		return parent;
	}

	public void setParent(ElementNode parent) {
		this.parent = parent;
	}

	public List<ElementNode> getChildren() {
		return children;
	}

	public void setChildren(List<ElementNode> children) {
		this.children = children;
	}

	public void addChild(ElementNode node) {
		this.children.add(node);
		node.setParent(this);
	}
	
	public Entry<IMethodDescription, Score> getCodeElement() {
		return codeElement;
	}

	public void setCodeElement(Entry<IMethodDescription, Score> codeElement) {
		this.codeElement = codeElement;
	}
}
