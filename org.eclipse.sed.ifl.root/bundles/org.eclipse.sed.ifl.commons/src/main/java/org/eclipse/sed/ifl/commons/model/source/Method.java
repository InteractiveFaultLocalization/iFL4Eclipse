package org.eclipse.sed.ifl.commons.model.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sed.ifl.commons.model.exception.LineNotFoundException;

public class Method implements IMethodDescription {

	private MethodIdentity id;
	private CodeChunkLocation location;
	private List<MethodIdentity> context = new ArrayList<>();
	private boolean interactivity = true;
	private Map<Line, Score> lines = new HashMap<>();
	
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

	@Override
	public Map<Line, Score> getLines() {
		return lines;
	}

	@Override
	public void setLines(Map<Long, Score> lines) {
		for(Map.Entry<Long, Score> line : lines.entrySet()) {
			addLine(line.getKey(), line.getValue());
		}
	}

	// TODO: add semaphore if concurrent execution is introduced
	@Override
	public void addLine(long lineNumber, Score score) {
		Line newLine = createLine(lineNumber);
		if(!lines.containsKey(newLine)) {
			newLine.setMethod(this);
			lines.put(newLine, score);
		} else {
			lines.put(newLine, score);
		}
	}
	
	private Line createLine(long lineNumber) {
		return new Line(lineNumber);
	}

	@Override
	public void removeLine(Line line) {
		Line originalLine = getLineFromLines(line);
		lines.remove(originalLine);
		originalLine.setMethod(null);
	}
	
	private Line getLineFromLines(Line line) {
		for(Line key : lines.keySet()) {
			if(key.equals(line)) {
				return key;
			}
		}
		throw new LineNotFoundException();
	}
}
