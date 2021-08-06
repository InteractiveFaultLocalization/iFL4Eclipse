package org.eclipse.sed.ifl.ide.accessor.gui;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;

public class PartAccessor {
	
	@Inject
	EPartService partService;
	
	public PartAccessor() {
	}
	
	public MPart getPart(String id){
		return partService.findPart(id);
	}
	
	public void addListenerToAllPages(IPartListener eclipseListener) {
        partService.addPartListener(eclipseListener);
	}

	public void removeListenerToAllPages(IPartListener eclipseListener) {
		partService.removePartListener(eclipseListener);
	}
}
