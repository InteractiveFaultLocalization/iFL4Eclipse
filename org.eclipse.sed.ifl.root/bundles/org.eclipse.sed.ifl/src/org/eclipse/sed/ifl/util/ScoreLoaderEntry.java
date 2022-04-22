package org.eclipse.sed.ifl.util;

public class ScoreLoaderEntry {
	private String name;
	private String detailsLink;
	private boolean interactivity;
	
	public ScoreLoaderEntry(String name, String detailsLink, boolean interactivity) {
		super();
		this.name = name;
		this.detailsLink = detailsLink;
		this.interactivity = interactivity;
	}

	public String getName() {
		return name;
	}

	public String getDetailsLink() {
		return detailsLink;
	}
	
	public boolean isInteractive() {
		return interactivity;
	}
}
