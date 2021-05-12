package hu.szte.sed.graph.algorithm;

import java.util.List;

import hu.szte.sed.graph.data.Node;
import hu.szte.sed.graph.visitor.Visitor;

public class Preorder<T extends Number> {

	public Preorder() {
	}

	public void run(Node<T> node, Visitor<T> visitor) {
		node.accept(visitor);

		List<Node<T>> children = node.getChildren();

		if (children != null) {
			for (Node<T> child : children) {
				run(child, visitor);
			}
		}

		node.acceptEnd(visitor);
	}

}
