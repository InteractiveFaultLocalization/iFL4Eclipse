package org.eclipse.sed.ifl.model.user.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.ide.gui.icon.OptionKind;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.items.IMethodDescriptionCollectionUtil;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.swt.SWT;

public class ContextBasedOption extends Option {
	
	private List<IMethodDescription> nonInteractiveContextList;
	
	public List<IMethodDescription> getNonInteractiveContext(){
		return nonInteractiveContextList;
	}

	private Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateSelected;
	private Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateContext;
	private Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateOther;

	public ContextBasedOption(String id, String title, String description, OptionKind kind,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateSelected,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateContext,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateOthers) {
		super(id, title, description, kind);
		this.setUpdateFunctions(updateSelected, updateContext, updateOthers);
	}

	private void setUpdateFunctions(Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateSelected,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateContext,
			Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> updateOthers) {
		this.updateSelected = updateSelected;
		this.updateContext = updateContext;
		this.updateOther = updateOthers;
	}

	private Map<IMethodDescription, Defineable<Double>> applyAll(Function<Entry<IMethodDescription, Defineable<Double>>, Defineable<Double>> function, Map<IMethodDescription, Defineable<Double>> items) {
		Map<IMethodDescription, Defineable<Double>> result = new HashMap<>();
		for (Entry<IMethodDescription, Defineable<Double>> item : items.entrySet()) {
			result.put(item.getKey(), function.apply(item));
		}
		return result;
	}
	
	@Override
	public Map<IMethodDescription, Defineable<Double>> apply(IUserFeedback feedback, Map<IMethodDescription, Defineable<Double>> allScores) {
		Map<IMethodDescription, Defineable<Double>> newScores = new HashMap<>();
		Map<IMethodDescription, Defineable<Double>> selected = null;
		Map<IMethodDescription, Defineable<Double>> context = null;
		Map<IMethodDescription, Defineable<Double>> other = null;
		if (updateSelected != null) {
			selected = feedback.getSubjects();
			newScores.putAll(applyAll(updateSelected, selected));
		}
		if (updateContext != null) {
			context = IMethodDescriptionCollectionUtil.collectContext(feedback.getSubjects(), allScores);
			newScores.putAll(applyAll(updateContext, context));
		}
		if (updateOther != null) {
			if (selected == null) {
				selected = feedback.getSubjects();
			}
			if (context == null) {
				context = IMethodDescriptionCollectionUtil.collectContext(feedback.getSubjects(), allScores);
			}
			other = IMethodDescriptionCollectionUtil.collectOther(allScores, selected, context);
			newScores.putAll(applyAll(updateOther, other));
		}
		return newScores;
	}

	private List<IMethodDescription> collectOther(Map<IMethodDescription, Defineable<Double>> allScores,
			List<IMethodDescription> selected, List<IMethodDescription> context) {
		Set<IMethodDescription> other = new HashSet<>();
		other.addAll(allScores.keySet());
		other.removeAll(selected);
		other.removeAll(context);
		return new ArrayList<>(other);
	}

	private List<IMethodDescription> collectContext(IUserFeedback feedback,
			Map<IMethodDescription, Defineable<Double>> allScores){
		nonInteractiveContextList = new ArrayList<IMethodDescription>(); 
		Set<IMethodDescription> context = new HashSet<>();
		feedback.getSubjects().stream()
			.flatMap(subject -> subject.getContext().stream())
			.forEach(id -> {
				for (IMethodDescription desc : allScores.keySet()) {
					if (desc.getId().equals(id)) {
						if(!desc.isInteractive()) {
							nonInteractiveContextList.add(desc);
						} else {
							context.add(desc);
							break;
						}
					}
				}
			});
		if(!nonInteractiveContextList.isEmpty()) {
			boolean highLightRequest = MessageDialog.open(
					MessageDialog.QUESTION, null,
					"Non-interactive methods removed",
					"Your selection or the context of it contains non-interactive elements. "
					+ "The score of non-interactive elements will not be affected by your feedback. "
					+ "Would you like to highlight the affected non-interactive methods?"
					, SWT.NONE);
			if (!highLightRequest) {
				nonInteractiveContextList.clear();
			}
		}
		
		return new ArrayList<>(context);
	}
}
