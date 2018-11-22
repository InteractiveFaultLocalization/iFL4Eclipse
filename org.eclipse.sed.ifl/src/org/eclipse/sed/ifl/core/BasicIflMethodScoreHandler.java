package org.eclipse.sed.ifl.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sed.ifl.bi.faced.MethodScoreHandler;
import org.eclipse.sed.ifl.bi.faced.execution.IMavenExecutor;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.model.user.interaction.IUserFeedback;
import org.eclipse.sed.ifl.model.user.interaction.Option;

public class BasicIflMethodScoreHandler extends MethodScoreHandler {

	public BasicIflMethodScoreHandler(IMavenExecutor executor) {
		super(executor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(Iterable<IMethodDescription> initialMethods) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateScore(IUserFeedback feedback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMethod(IMethodDescription method) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMethod(IMethodDescription method) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeMethod(IMethodDescription method) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<Option> getProvidedOptions() {
		List<Option> providedOptions = new ArrayList<Option>();

		Option yes = new Option("YES", "Is faulty", "Select this option if the subject is faulty");
		providedOptions.add(yes);
		Option no = new Option("NO", "Not faulty", "Select this option if the subject is NOT faulty");
		providedOptions.add(no);
		Option noButSuspicious = new Option("NO_BUT_SUSPICIOUS", "Is not faulty but its neighbours are suspicious",
				"Select this option if the subject is not faulty but its neighbours are suspicious");
		providedOptions.add(noButSuspicious);
		Option noAndNotSuspicious = new Option("NO_AND_NOT_SUSPICIOUS",
				"Neither the subject, nor its neighbours are faulty",
				"Select this option if neither the subject, nor its neighbours are faulty");
		providedOptions.add(noAndNotSuspicious);

		return providedOptions;
	}

}
