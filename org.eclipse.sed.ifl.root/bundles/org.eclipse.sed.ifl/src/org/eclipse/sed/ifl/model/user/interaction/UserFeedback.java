package org.eclipse.sed.ifl.model.user.interaction;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sed.ifl.model.user.identification.DefaultUser;
import org.eclipse.sed.ifl.model.user.identification.IUser;
import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.util.wrapper.Defineable;

public class UserFeedback implements IUserFeedback {

	public UserFeedback(Option option, Map<IMethodDescription,Defineable<Double>> subjects) {
		this(option, subjects, new DefaultUser());
	}
	
	public UserFeedback(Option option, Map<IMethodDescription, Defineable<Double>> subjects, IUser user) {
		super();
		this.option = option;
		this.subjects.putAll(subjects);
		this.user = user;
	}
	
	private IUser user;
	
	@Override
	public IUser getUser() {
		return user;
	}

	private Option option;
	
	@Override
	public Option getChoice() {
		return option;
	}

	private Map<IMethodDescription, Defineable<Double>> subjects = new HashMap<>();
	
	@Override
	public Map<IMethodDescription, Defineable<Double>> getSubjects() {
		return this.subjects;
	}
}
