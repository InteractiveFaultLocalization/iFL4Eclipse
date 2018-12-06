package org.eclipse.sed.ifl.core;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.sed.ifl.model.user.interaction.Option;
import org.junit.Test;

public class BasicIflMethodScoreHandlerTest {

	BasicIflMethodScoreHandler handler = new BasicIflMethodScoreHandler(null);

	@Test
	public void testUpdateScore() {
//
//		class IUserFeedbackExample1 implements IUserFeedback {
//
//			@Override
//			public IUser getUser() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public Option getChoise() {
//				return new Option("YES", "", "");
//			}
//
//			@Override
//			public Iterable<IMethodDescription> getSubjects() {
//				List<IMethodDescription> methods = new ArrayList<>();
//				class MethodExample implements IMethodDescription {
//
//					public MethodIdentity method;
//
//					@Override
//					public MethodIdentity getId() {
//
//						return method;
//					}
//
//					@Override
//					public ICodeChunkLocation getLocation() {
//						// TODO Auto-generated method stub
//						return null;
//					}
//
//					@Override
//					public Iterable<IMethodDescription> getContext() {
//						// TODO Auto-generated method stub
//						return null;
//					}
//
//				}
//				MethodExample methExample = new MethodExample();
//				methExample.method = new MethodIdentity("test", "signature()", "parentType");
//				methods.add(methExample);
//				return methods;
//			}
//
//		}
//		@mock
//		IUserFeedback ex1 = new IUserFeedbackExample1();
//		handler.updateScore(ex1);
	}

	@Test
	public void testGetProvidedOptions() {
		List<Option> providedOptions = (List<Option>) handler.getProvidedOptions();

		Option yes = new Option("YES", "", "");
		Option noButSuspicious = new Option("NO_BUT_SUSPICIOUS", "", "");
		Option noAndNotSuspicious = new Option("NO_AND_NOT_SUSPICIOUS", "", "");

		assertTrue(providedOptions.size() == 3);
		assertTrue(providedOptions.contains(yes));
		assertTrue(providedOptions.contains(noButSuspicious));
		assertTrue(providedOptions.contains(noAndNotSuspicious));
	}

}
