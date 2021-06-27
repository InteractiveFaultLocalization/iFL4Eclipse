package org.eclipse.sed.ifl.model.user.interaction;


import java.util.Map;

import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.util.wrapper.Defineable;

public class Option {
	private String id;
	private String title;
	private String description;
	private SideEffect sideEffect;

	public Option(String id, String title, String description, SideEffect sideEffect, OptionKind kind) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.sideEffect = sideEffect;
		this.kind = kind;
	}
	
	public Option(String id, String title, String description, SideEffect sideEffect) {
		this(id, title, description, sideEffect, null);
	}
	
	public Option(String id, String title, String description, OptionKind kind) {
		this(id, title, description, SideEffect.NOTHING, kind);
	}

	public Option(String id, String title, String description) {
		this(id, title, description, SideEffect.NOTHING, null);
	}

	public OptionKind getKind() {
		return kind;
	}

	public SideEffect getSideEffect() {
		return sideEffect;
	}
	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
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
		Option other = (Option) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private OptionKind kind; 
	
	public Map<IMethodDescription, Defineable<Double>> apply(IUserFeedback feedback, Map<IMethodDescription, Defineable<Double>> allScores) { 
		throw new UnsupportedOperationException();
	}

}
