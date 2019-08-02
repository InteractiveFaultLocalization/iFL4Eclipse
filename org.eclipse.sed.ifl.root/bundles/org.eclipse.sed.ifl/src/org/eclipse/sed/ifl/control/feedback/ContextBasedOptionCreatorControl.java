package org.eclipse.sed.ifl.control.feedback;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.user.interaction.ContextBasedOptionCreatorModel;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.view.ContextBasedOptionCreatorView;

public class ContextBasedOptionCreatorControl extends Control<ContextBasedOptionCreatorModel, ContextBasedOptionCreatorView> {

	private List<Defineable<Double>> selecteds = new ArrayList<>();

	private List<Defineable<Double>> contexts = new ArrayList<>();
	
	private List<Defineable<Double>> others = new ArrayList<>();
	
	public ContextBasedOptionCreatorControl(ContextBasedOptionCreatorModel model, ContextBasedOptionCreatorView view) {
		super(model, view);
	}
	
	public void setSelecteds(List<Defineable<Double>> selecteds) {
		this.selecteds = selecteds;
	}

	public void setContexts(List<Defineable<Double>> contexts) {
		this.contexts = contexts;
	}

	public void setOthers(List<Defineable<Double>> others) {
		this.others = others;
	}
	
	public void createNewOption() {
		getView().display();
	}
	
}
