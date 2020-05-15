package org.eclipse.sed.ifl.ide.gui.icon;

public enum ScoreStatus implements IconSet {
	NONE(null), DECREASED("icons/down_tri16.png"), UNDEFINED("icons/undef16.png"), INCREASED("icons/up_tri16.png");

	private final String iconPath;

	ScoreStatus(String iconPath) {
		this.iconPath = iconPath;
	}

	@Override
	public String getIconPath() {
		return iconPath;
	}
}