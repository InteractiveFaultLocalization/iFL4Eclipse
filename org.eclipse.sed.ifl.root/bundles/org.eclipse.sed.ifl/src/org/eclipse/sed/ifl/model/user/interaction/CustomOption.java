package org.eclipse.sed.ifl.model.user.interaction;

import java.util.function.Function;

import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.wrapper.Defineable;

public class CustomOption extends ContextBasedOption {

	//private CustomValue customValue;

	public CustomOption(String id, String title, String description, OptionKind kind,
			Function<IMethodDescription, Defineable<Double>> updateSelected,
			Function<IMethodDescription, Defineable<Double>> updateContext,
			Function<IMethodDescription, Defineable<Double>> updateOthers) {
		super(id, title, description, kind, updateSelected, updateContext, updateOthers);
		this.setUpdateFunctions(updateSelected, updateContext, updateOthers);
	}

	private void setUpdateFunctions(Function<IMethodDescription, Defineable<Double>> updateSelected,
			Function<IMethodDescription, Defineable<Double>> updateContext,
			Function<IMethodDescription, Defineable<Double>> updateOthers) {
		this.setUpdateSelected(updateSelected);
		this.setUpdateContext(updateContext);
		this.setUpdateOther(updateOthers);
		
	}

}
