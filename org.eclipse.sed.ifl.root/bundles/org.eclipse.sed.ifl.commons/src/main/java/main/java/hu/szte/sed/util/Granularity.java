package main.java.hu.szte.sed.util;

public enum Granularity {

	BINARY("binary"), COUNT("count"), CHAIN("chain");

	private String text;

	private Granularity(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public static Granularity fromText(final String text) {
		switch (text) {
			case "binary":
				return BINARY;
			case "count":
				return COUNT;
			case "chain":
				return CHAIN;
			default:
				throw new IllegalArgumentException(String.format("Invalid granularity value '%s'", text));
		}
	}

}
