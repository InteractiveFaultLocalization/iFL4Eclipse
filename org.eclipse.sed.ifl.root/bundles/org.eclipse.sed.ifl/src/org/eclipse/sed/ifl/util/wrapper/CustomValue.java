package org.eclipse.sed.ifl.util.wrapper;

public class CustomValue extends Defineable<Double>{

	public CustomValue(boolean isActive, int scaleValue, boolean isAbsoluteValue, int absoluteValue) {
		this.isActive = isActive;
		this.scaleValue = scaleValue;
		this.isAbsoluteValue = isAbsoluteValue;
		this.absoluteValue = absoluteValue;
	}

	public int getScaleValue() {
		return scaleValue;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isAbsoluteValue() {
		return isAbsoluteValue;
	}

	public int getAbsoluteValue() {
		return absoluteValue;
	}

	private int scaleValue;
	private boolean isActive;
	private boolean isAbsoluteValue;
	private int absoluteValue;
	
}
