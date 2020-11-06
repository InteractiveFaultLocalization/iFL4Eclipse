package main.java.org.eclipse.sed.ifl.commons.model.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Method implements IMethodDescription {

	private MethodIdentity id;
	private CodeChunkLocation location;
	private List<MethodIdentity> context = new ArrayList<>();
	private boolean interactivity = true;
	
	public Method(MethodIdentity id, CodeChunkLocation location, String detailsLink, List<MethodIdentity> context) {
		this(id, location, context);
		this.detailsLink = detailsLink;
	}
	
	public Method(MethodIdentity id, CodeChunkLocation location, List<MethodIdentity> context) {
		super();
		this.id = id;
		this.location = location;
		this.context.addAll(context);
	}
	
	public Method(MethodIdentity id, CodeChunkLocation location, List<MethodIdentity> context, Boolean interactivity) {
		this(id, location, context);
		this.interactivity = interactivity;
	}
	
	public Method(MethodIdentity id, CodeChunkLocation location, String detailsLink) {
		this(id, location);
		this.detailsLink = detailsLink;
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
	public List<MethodIdentity> getContext() {
		return Collections.unmodifiableList(context);
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

	@Override
	public String toString() {
		return "Method [id=" + id + ", location=" + location + ", size(context)=" + context.size() + "]";
	}

	private String detailsLink = null;
	
	@Override
	public String getDetailsLink() {
		return detailsLink;
	}

	@Override
	public boolean hasDetailsLink() {
		return detailsLink != null;
	}

	@Override
	public void setDetailsLink(String link) {
		this.detailsLink = link;
	}

	public boolean isInteractive() {
		return interactivity;
	}

	public void setInteractivity(boolean interactivity) {
		this.interactivity = interactivity;
	}
}
