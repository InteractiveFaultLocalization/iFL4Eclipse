package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.control.project.ProjectControl;
import org.eclipse.sed.ifl.model.EmptyModel;

public class MainControl extends ViewlessControl<EmptyModel> {

	public MainControl(EmptyModel model) {
		super(model);
	}
	
	public void addProjectControl(ProjectControl control) {
		addSubControl(control);
	}
}
