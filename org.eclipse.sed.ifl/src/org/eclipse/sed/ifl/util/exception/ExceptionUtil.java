package org.eclipse.sed.ifl.util.exception;

public class ExceptionUtil {
	@FunctionalInterface
	public interface FunctionWithException<TIn, TOut, TExc extends Exception>{
		TOut apply(TIn in) throws TExc;
	}
	
	public static <TIn, TOut, TExc extends Exception> TOut tryUnchecked(TIn in, FunctionWithException<TIn, TOut, TExc> function) {
		try {
			return function.apply(in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
