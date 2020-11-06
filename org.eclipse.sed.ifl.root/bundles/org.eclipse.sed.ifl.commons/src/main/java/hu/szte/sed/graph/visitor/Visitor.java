package main.java.hu.szte.sed.graph.visitor;

import main.java.hu.szte.sed.graph.data.Node;

public interface Visitor<T extends Number> {

	public void visit(Node<T> node);

	public void visitEnd(Node<T> node);

}
