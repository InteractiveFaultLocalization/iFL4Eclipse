package org.eclipse.sed.ifl.control.feedback;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.general.INamed;
import org.eclipse.sed.ifl.model.user.interaction.ScoreSetterModel;
import org.eclipse.sed.ifl.view.ScoreSetterView;

public class ScoreSetterControl extends Control<ScoreSetterModel, ScoreSetterView> implements INamed {

	private String name = "noname";

	public ScoreSetterControl(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void init() {
		getView().setTitle(getName());
		super.init();
	}
}
