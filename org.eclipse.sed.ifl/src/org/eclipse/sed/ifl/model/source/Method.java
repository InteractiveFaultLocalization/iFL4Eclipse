package org.eclipse.sed.ifl.model.source;

import java.util.ArrayList;
import java.util.List;

public class Method implements IMethodDescription {

	private MethodIdentity id;
	private CodeChunkLocation location;
	private List<IMethodDescription> context = new ArrayList<>();
	
	public Method(MethodIdentity id, CodeChunkLocation location, List<IMethodDescription> context) {
		super();
		this.id = id;
		this.location = location;
		this.context.addAll(context);
	}
	
	public Method(MethodIdentity id, CodeChunkLocation location) {
		this(id, location, new ArrayList<>());
	}

	@Override
	public MethodIdentity getId() {
		return id;
	}

	@Override
	public ICodeChunkLocation getLocation() {
		return location;
	}

	@Override
	public Iterable<IMethodDescription> getContext() {
		return context;
	}

}
