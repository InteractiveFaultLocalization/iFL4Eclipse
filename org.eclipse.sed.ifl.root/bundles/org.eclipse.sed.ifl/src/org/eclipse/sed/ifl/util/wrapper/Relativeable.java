package org.eclipse.sed.ifl.util.wrapper;

public class Relativeable<Tvalue extends Defineable<Double>> {

	public Relativeable(boolean isRelative, Defineable<Double> value) {
		this.isRelative = isRelative;
		this.value = value;
	}

	public boolean isRelative() {
		return isRelative;
	}
	
	public Defineable<Double> getValue() {
		return this.value;
	}
	
	private boolean isRelative;
	private Defineable<Double> value;
}
