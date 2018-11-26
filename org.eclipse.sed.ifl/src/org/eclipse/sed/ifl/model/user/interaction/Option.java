package org.eclipse.sed.ifl.model.user.interaction;

public class Option {
	private String id;
	private String Title;
	private String Description;

	public Option(String id, String title, String description) {
		super();
		this.id = id;
		Title = title;
		Description = description;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return Title;
	}

	public String getDescription() {
		return Description;
	}

}
