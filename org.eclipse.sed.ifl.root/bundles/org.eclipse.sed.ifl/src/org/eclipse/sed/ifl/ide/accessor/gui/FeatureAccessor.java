package org.eclipse.sed.ifl.ide.accessor.gui;

import java.net.URI;

public class FeatureAccessor {
	public void openLink(URI url) {
		try {
			java.awt.Desktop.getDesktop().browse(url);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
