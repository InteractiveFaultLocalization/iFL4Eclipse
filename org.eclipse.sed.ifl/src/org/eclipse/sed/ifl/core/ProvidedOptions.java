package org.eclipse.sed.ifl.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.sed.ifl.model.user.interaction.Option;

public class ProvidedOptions implements Iterable<Option> {

	private List<Option> providedOptions = new ArrayList<Option>();

	public void addOption(Option option) {
		providedOptions.add(option);
	}

	public void removeOption(Option option) {
		providedOptions.remove(option);
	}

	@Override
	public Iterator<Option> iterator() {
		return providedOptions.iterator();
	}

}
