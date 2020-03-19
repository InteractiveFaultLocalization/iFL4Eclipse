package org.eclipse.sed.ifl.ide.gui.icon;

public enum OptionKind implements IconSet {
	NONE(null),
	CONTEXT_XX0("icons/option_--0.png"),
	CONTEXT_0XX("icons/option_0--.png"),
	CONTEXT_0X0("icons/option_0-0.png"),
	CONTEXT_00X("icons/option_00-.png"),
	CONTEXT_FXX("icons/option_F--.png"),
	CUSTOM(null);
	
	private final String iconPath;

	OptionKind(String iconPath) {
		this.iconPath = iconPath;
	}

	@Override
	public String getIconPath() {
		return iconPath;
	}
}
