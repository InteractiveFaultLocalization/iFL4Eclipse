package org.eclipse.sed.ifl.bi.faced;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public abstract class MethodScoreHandler {

	protected Map<IMethodDescription, Defineable<Double>> methodsScoreMap = new HashMap<IMethodDescription, Defineable<Double>>();

	public MethodScoreHandler(IMavenExecutor executor) {
	}

	public abstract void init(Iterable<IMethodDescription> initialMethods);

	public abstract void updateScore(IUserFeedback feedback);

	public abstract void updateMethod(IMethodDescription method);

	public abstract void addMethod(IMethodDescription method);

	public abstract void removeMethod(IMethodDescription method);

	public abstract Iterable<Option> getProvidedOptions();

	public class ScoreUpdateArgs {
		private Map<IMethodDescription, Defineable<Double>> newScores;
		private IUserFeedback cause;
		
		public Map<IMethodDescription, Defineable<Double>> getNewScores() {
			return newScores;
		}
		public IUserFeedback getCause() {
			return cause;
		}
		public ScoreUpdateArgs(Map<IMethodDescription, Defineable<Double>> newScores, IUserFeedback cause) {
			super();
			this.newScores = newScores;
			this.cause = cause;
		}
	}
	
	protected NonGenericListenerCollection<ScoreUpdateArgs> scoreUpdated = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<ScoreUpdateArgs> eventScoreUpdated() {
		return scoreUpdated;
	}
	

	public void loadMethodsScoreMap(Map<IMethodDescription, Defineable<Double>> map) {
		methodsScoreMap.putAll(new HashMap<IMethodDescription, Defineable<Double>>(map));
	}

}
