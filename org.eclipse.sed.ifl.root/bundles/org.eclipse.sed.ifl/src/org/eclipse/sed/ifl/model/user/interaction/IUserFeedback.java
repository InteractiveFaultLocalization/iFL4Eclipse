package org.eclipse.sed.ifl.model.user.interaction;

import java.util.Map;

import org.eclipse.sed.ifl.model.user.identification.IUser;
import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.commons.model.util.wrapper.Defineable;

public interface IUserFeedback {
	public IUser getUser();
	
	public Option getChoice();
	
	public Map<IMethodDescription,Defineable<Double>> getSubjects();
}
