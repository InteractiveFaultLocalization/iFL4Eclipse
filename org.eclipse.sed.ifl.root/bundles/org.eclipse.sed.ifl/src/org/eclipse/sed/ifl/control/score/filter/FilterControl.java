package org.eclipse.sed.ifl.control.score.filter;

import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.FilterModel;
import org.eclipse.sed.ifl.view.FilterView;

public class FilterControl extends Control<FilterModel, FilterView> {
	
	public void showFilterPart() {
		getView().showFilterPart();
	}
	
	public void init() {
		initUIListeners();
		super.init();
	}

	private void initUIListeners() {
		// TODO Auto-generated method stub
		
	}
	
}
