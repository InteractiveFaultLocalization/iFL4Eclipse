package org.eclipse.sed.ifl.control;

import org.eclipse.sed.ifl.control.session.SessionControl;
import org.eclipse.sed.ifl.model.EmptyModel;
import org.eclipse.sed.ifl.model.session.SessionModel;
import org.eclipse.sed.ifl.view.SessionView;

public class MainControl extends ViewlessControl<EmptyModel> {
	public MainControl(EmptyModel model) {
		super(model);
	}

	@Override
	public void init() {
		addSubControl(new SessionControl(new SessionModel(), new SessionView()));
		super.init();
	}
}
