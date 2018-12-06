package org.eclipse.sed.ifl.util.wrapper;

public class Defineable<TValue> {
	private TValue value;

	public Defineable() {
		this.undefine();
	}
	
	public Defineable(TValue value) {
		this.setValue(value);
	}

	public TValue getValue() {
		if (definit) {
			return value;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void setValue(TValue value) {
		this.value = value;
		this.definit = true;
	}
	
	private Boolean definit = false;
	
	public Boolean isDefinit() {
		return definit;
	}
	
	public void undefine() {
		definit = false;
	}
	
	@Override
	public String toString() {
		if (isDefinit()) {
			return "definit[" + getValue() + "]";
		} else {
			return "undefined";
		}
	}
}
