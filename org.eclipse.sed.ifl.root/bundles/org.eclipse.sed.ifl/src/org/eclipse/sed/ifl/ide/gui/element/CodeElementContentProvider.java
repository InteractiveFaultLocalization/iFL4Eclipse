package org.eclipse.sed.ifl.ide.gui.element;

import java.util.Map;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;
import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.ui.model.WorkbenchContentProvider;

public class CodeElementContentProvider extends WorkbenchContentProvider {

	@Override
	public Object[] getElements(Object element) {
		@SuppressWarnings("unchecked")
		Map<IMethodDescription, Score> entryMap = (Map<IMethodDescription, Score>) element;
		return entryMap.entrySet().toArray();
	}

}
