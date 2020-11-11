package main.java.hu.szte.sed.graph.data;

import java.util.ArrayList;
import java.util.List;

import main.java.hu.szte.sed.graph.visitor.Visitor;

public class Node<T extends Number> {

	private final T codeElementId;
	private boolean isFinal;
	private long count;
	private final Node<T> parent;
	private List<Node<T>> children;

	public Node(final T codeElementId, final Node<T> parent) {
		this.codeElementId = codeElementId;
		this.isFinal = false;
		this.count = 0;
		this.parent = parent;
	}

	public T getCodeElementId() {
		return codeElementId;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(final boolean isFinal) {
		this.isFinal = isFinal;
	}

	public long getCount() {
		return count;
	}

	public void increaseCount() {
		++count;
	}

	public Node<T> getParent() {
		return parent;
	}

	public List<Node<T>> getChildren() {
		return children;
	}

	public Node<T> addChildIfNotExists(final T id) {
		if (children == null) {
			children = new ArrayList<Node<T>>(1); // TODO: set initial capacity based on stats
		} else {
			for (Node<T> child : children) {
				if (child.getCodeElementId().equals(id)) {
					return child;
				}
			}
		}

		return addChild(id);
	}

	private Node<T> addChild(final T id) {
		final Node<T> child = new Node<T>(id, this);

		children.add(child);

		return child;
	}

	public void accept(final Visitor<T> visitor) {
		visitor.visit(this);
	}

	public void acceptEnd(final Visitor<T> visitor) {
		visitor.visitEnd(this);
	}

	@Override
	public String toString() {
		if (isFinal) {
			return "[" + codeElementId + "]";
		} else {
			return "(" + codeElementId + ")";
		}
	}

}
