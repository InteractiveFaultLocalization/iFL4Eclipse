package org.eclipse.sed.ifl.model.user.interaction;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.identification.IUser;

public class UserFeedback implements IUserFeedback {

	public UserFeedback(Option option, List<IMethodDescription> subjects) {
		super();
		this.option = option;
		this.subjects.addAll(subjects);
	}

	@Override
	public IUser getUser() {
		return null;
	}

	private Option option;
	
	@Override
	public Option getChoise() {
		return option;
	}

	private List<IMethodDescription> subjects = new ArrayList<>();
	
	@Override
	public Iterable<IMethodDescription> getSubjects() {
		return subjects;
	}

}
