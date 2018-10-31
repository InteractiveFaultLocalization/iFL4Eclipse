package org.eclipse.sed.ifl.bi.faced;

import java.util.Map;

import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.IListenerCollection;
import org.eclipse.sed.ifl.util.event.IListenerInvoker;

public abstract class MethodScoreHandler {
	public MethodScoreHandler(IMavenExecutor executor) { }
	
	public abstract void init(Iterable<IMethodDescription> initialMethods);
	
	public abstract void updateScore(IUserFeedback feedback);
	
	public abstract void updateMethod(IMethodDescription method);
	
	public abstract void addMethod(IMethodDescription method);
	
	public abstract void removeMethod(IMethodDescription method);
	
	public abstract Iterable<Option> getProvidedOptions();
	
	//Yes, I know. Just call this.scoreUpdated.invoke(Map<IMethodDescription, Double>) to raise the event.
	private IListenerInvoker<Map<IMethodDescription, Double>, IListener<Map<IMethodDescription, Double>>> scoreUpdated;
	
	public IListenerCollection<Map<IMethodDescription, Double>, IListener<Map<IMethodDescription, Double>>> eventScoreUpdated(){
		return scoreUpdated;
	}
}
