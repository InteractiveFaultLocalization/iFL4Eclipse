package org.eclipse.sed.ifl.ide.gui.icon;

public enum ScoreStatus implements IconSet {
	NONE(null), INCREASED("icons/up_arrow16.png"), DECREASED("icons/down_arrow16.png"), UNDEFINED("icons/undef16.png");

	private final String iconPath;

	ScoreStatus(String iconPath) {
		this.iconPath = iconPath;
	}

	@Override
	public String getIconPath() {
		return iconPath;
	}
}