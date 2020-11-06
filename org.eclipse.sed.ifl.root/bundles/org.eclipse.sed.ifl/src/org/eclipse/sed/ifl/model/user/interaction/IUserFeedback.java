package org.eclipse.sed.ifl.model.user.interaction;

import java.util.Map;

import org.eclipse.sed.ifl.model.user.identification.IUser;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public interface IUserFeedback {
	public IUser getUser();
	
	public Option getChoice();
	
	public Map<IMethodDescription,Defineable<Double>> getSubjects();
}
