package org.eclipse.sed.ifl.util.wrapper;

public class FilterParams {

	private Double min;
	private Double max;
	private Double current;
	
	public FilterParams(double min, double max, double current) {
		this(min, max);
		this.current = current;
	}

	public FilterParams(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getCurrent() {
		return current;
	}

	public void setCurrent(Double current) {
		this.current = current;
	}

}
