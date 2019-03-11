package org.eclipse.sed.ifl.ide.accessor.gui;

import java.net.URL;

import org.eclipse.sed.ifl.util.exception.EU;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;

public class FeatureAccessor {
	public void openLink(URL url) {
		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
