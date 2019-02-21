package org.eclipse.sed.ifl.model.monitor.resource;

import org.eclipse.sed.ifl.model.monitor.Node;

public abstract class Resource extends Node {
	@Override
	protected final String getLabel() {
		return "resource";
	}
}
