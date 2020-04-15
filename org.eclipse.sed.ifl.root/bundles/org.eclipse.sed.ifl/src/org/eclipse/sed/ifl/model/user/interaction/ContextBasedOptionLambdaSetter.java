package org.eclipse.sed.ifl.model.user.interaction;

import java.util.Map.Entry;
import java.util.function.Function;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.Relativeable;

public class ContextBasedOptionLambdaSetter {

	private Relativeable<Defineable<Double>> relativeable;
	
	public ContextBasedOptionLambdaSetter(Relativeable<Defineable<Double>> relativeable) {
		this.relativeable = relativeable;
	}
	
	private Defineable<Double> contextBasedFeedbackValueSetter(Relativeable<Defineable<Double>> relativeableValue, Defineable<Double> previousValue) {
		if(!previousValue.isDefinit() ) {
			return previousValue;
		}
		Defineable<Double> newValue = new Defineable<Double>();

		if(relativeableValue.getValue().isDefinit()) {
			if(!relativeableValue.isRelative()) {
				newValue = relativeableValue.getValue();
			} else {
				double newDoubleValue = previousValue.getValue() + (previousValue.getValue() * (relativeableValue.getValue().getValue() * 0.01));
				if(newDoubleValue > 1.0) {
					newDoubleValue = 1.0;
				} else if(newDoubleValue < 0.0) {
					newDoubleValue = 0.0;
				}
				newValue = new Defineable<Double>(newDoubleValue);
			}
		}
		return newValue;
	}
	
	//name should be changed as it is misleading
	public Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> setLambda(){
		Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> returnFunction =
				// If relativeable is defined, then check if the method whose score we are about to change is
				// interactive. If it is, then the return function calculates the new score; if it is not, the
				// return function is null (it won't change anything). 
				
				 relativeable.getValue().isDefinit() ? item -> (item.getKey().isInteractive() && item.getValue().isDefinit()) ? contextBasedFeedbackValueSetter(relativeable, item.getValue()) : null : null;
		return returnFunction;
	}
}
