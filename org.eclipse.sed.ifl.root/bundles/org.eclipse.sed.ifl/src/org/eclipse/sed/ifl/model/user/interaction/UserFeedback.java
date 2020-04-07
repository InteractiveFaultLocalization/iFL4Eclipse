package org.eclipse.sed.ifl.model.user.interaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.identification.DefaultUser;
import org.eclipse.sed.ifl.model.user.identification.IUser;

public class UserFeedback implements IUserFeedback {

	public UserFeedback(Option option, List<IMethodDescription> subjects) {
		this(option, subjects, new DefaultUser());
	}
	
	public UserFeedback(Option option, List<IMethodDescription> subjects, IUser user) {
		super();
		this.option = option;
		this.subjects.addAll(subjects);
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

	private List<IMethodDescription> subjects = new ArrayList<>();
	
	@Override
	public List<IMethodDescription> getSubjects() {
		return Collections.unmodifiableList(subjects);
	}
}
