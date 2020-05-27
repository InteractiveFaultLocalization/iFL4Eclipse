package org.eclipse.sed.ifl.control.score.filter;

public class DoubleRule implements Rule {

	private String domain;
	private double value;
	private String relation;

	public DoubleRule(String domain, double value, String relation) {
		this.domain = domain;
		this.value = value;
		this.relation = relation;
	}

	public double getValue() {
		return value;
	}

	public String getRelation() {
		return relation;
	}

	@Override
	public String getDomain() {
		return domain;
	}

}
