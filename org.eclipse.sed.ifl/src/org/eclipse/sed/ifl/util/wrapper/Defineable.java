package org.eclipse.sed.ifl.util.wrapper;

public class Defineable<TValue> {
	private TValue value;

	public Defineable(TValue value) {
		this.setValue(value);
	}

	public TValue getValue() {
		if (isDefined) {
			return value;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void setValue(TValue value) {
		this.value = value;
		this.isDefined = true;
	}
	
	private Boolean isDefined = false;
	
	public void undefine() {
		isDefined = false;
	}
}
