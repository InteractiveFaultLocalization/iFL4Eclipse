package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;

public interface RuleCreator {

	public Rule getRule();
	
	final NonGenericListenerCollection<Rule> ruleCreated = new NonGenericListenerCollection<>();
	
	public default INonGenericListenerCollection<Rule> eventRuleCreated() {
		return ruleCreated;
	}
	
}
