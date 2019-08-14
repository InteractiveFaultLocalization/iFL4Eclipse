package org.eclipse.sed.ifl.util.wrapper;

public class Projection<TProjected extends Comparable<TProjected>> {
	private TProjected original;
	
	private Defineable<TProjected> projected = new Defineable<>();
	
	public Projection(TProjected original) {
		this.original = original;
	}

	public Defineable<TProjected> getProjected() {
		return projected;
	}

	public void setProjected(Defineable<TProjected> projected) {
		this.projected = projected;
	}

	public TProjected getOriginal() {
		return original;
	}	
}
