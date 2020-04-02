package org.eclipse.sed.ifl.util.wrapper;

public class Relativeable<Tvalue extends Defineable<Double>> {

	public Relativeable(boolean isRelative, Defineable<Double> value, String selection) {
		this.isRelative = isRelative;
		this.value = value;
		this.selection = selection;
	}

	public boolean isRelative() {
		return isRelative;
	}
	
	public Defineable<Double> getValue() {
		return this.value;
	}
	
	public String getSelection() {
		return this.selection;
	}
	
	private boolean isRelative;
	private Defineable<Double> value;
	private String selection;
}
