package org.eclipse.sed.ifl.util.wrapper;

public class CustomValue {
//TODO engedjük el a UI-t
//TODO +1 adattag defineable-höz képest: abszolút-e vagy sem
//TODO átnevezni (ne feltétlen származzon a defineable-bõl)
	public CustomValue(boolean isAbsolute, int value) {
		this.isAbsolute = isAbsolute;
		this.value = value;
	}

	public boolean isAbsolute() {
		return isAbsolute;
	}
	
	public int getValue() {
		return this.value;
	}
	
	private boolean isAbsolute;
	private int value;
}
