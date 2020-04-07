package org.eclipse.sed.ifl.model.user.interaction;

import java.util.List;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.identification.IUser;

public interface IUserFeedback {
	public IUser getUser();
	
	public Option getChoice();
	
	public List<IMethodDescription> getSubjects();
}
