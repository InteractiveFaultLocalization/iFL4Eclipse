package hu.szte.sed.graph.visitor;

import static hu.szte.sed.util.DataOutputStreamHelper.writeNumber;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Stack;

import hu.szte.sed.graph.data.Node;
import hu.szte.sed.util.Constants;

public class ChainCollectorVisitor<T extends Number> implements Visitor<T> {

	private Stack<T> stack;
	private DataOutputStream output;

	public ChainCollectorVisitor(DataOutputStream output) {
		this.stack = new Stack<>();
		this.output = output;
	}

	@Override
	public void visit(final Node<T> node) {
		stack.push(node.getCodeElementId());

		if (node.isFinal()) {
			saveChain(node.getCount());
		}
	}

	@Override
	public void visitEnd(final Node<T> node) {
		stack.pop();
	}

	private void saveChain(final long count) {
		try {
			writeNumber(output, stack.size() - 1);

			for (T id : stack) {
				if (id.equals(Constants.ROOT_ID)) {
					continue;
				}

				writeNumber(output, id);
			}

			writeNumber(output, count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
