package org.eclipse.sed.ifl.control.score.filter;

public class DoubleRule implements Rule {
	
	private String domain;
	private Double value;
	private String relation;
	private boolean negated = false;

	public DoubleRule(String domain, double value, String relation) {
		this.domain = domain;
		this.value = value;
		this.relation = relation;
	}

	public Double getValue() {
		return value;
	}

	public String getRelation() {
		return relation;
	}

	@Override
	public String getDomain() {
		return domain;
	}
	
	@Override
	public void setNegated(boolean value) {
		this.negated = value;
	}

	@Override
	public boolean isNegated() {
		return this.negated;
	}

}
