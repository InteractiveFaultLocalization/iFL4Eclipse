package org.eclipse.sed.ifl.bi.faced;

import java.util.Map;

import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;

public abstract class MethodScoreHandler {
	public MethodScoreHandler(IMavenExecutor executor) { }
	
	public abstract void init(Iterable<IMethodDescription> initialMethods);
	
	public abstract void updateScore(IUserFeedback feedback);
	
	public abstract void updateMethod(IMethodDescription method);
	
	public abstract void addMethod(IMethodDescription method);
	
	public abstract void removeMethod(IMethodDescription method);
	
	public abstract Iterable<Option> getProvidedOptions();
	
	//Just call this.scoreUpdated.invoke(Map<IMethodDescription, Double>) to raise the event.
	protected NonGenericListenerCollection<Map<IMethodDescription, Double>> scoreUpdated;
	
	public INonGenericListenerCollection<Map<IMethodDescription, Double>> eventScoreUpdated(){
		return scoreUpdated;
	}
}
