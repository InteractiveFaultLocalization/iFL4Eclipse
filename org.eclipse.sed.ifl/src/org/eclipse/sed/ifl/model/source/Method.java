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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Method other = (Method) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	

}
