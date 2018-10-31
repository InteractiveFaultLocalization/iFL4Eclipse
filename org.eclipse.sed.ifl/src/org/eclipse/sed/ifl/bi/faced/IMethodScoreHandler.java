package org.eclipse.sed.ifl.bi.faced;

import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;

public abstract class IMethodScoreHandler {
	public IMethodScoreHandler(IMavenExecutor executor) { }
	
	public abstract void init(Iterable<IMethodDescription> initialMethods);
	
	public abstract void updateScore(IUserFeedback feedback);
	
	public abstract void updateMethod(IMethodDescription method);
	
	public abstract void addMethod(IMethodDescription method);
	
	public abstract void removeMethod(IMethodDescription method);
	
	public abstract Iterable<Option> getProvidedOptions();
}
