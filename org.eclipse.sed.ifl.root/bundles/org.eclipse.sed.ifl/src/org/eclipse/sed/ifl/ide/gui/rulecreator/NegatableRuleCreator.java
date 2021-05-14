package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class NegatableRuleCreator extends Composite implements RuleCreator {
	
	public NegatableRuleCreator(Composite parent, int style, String domain) {
		super(parent, style);
		
		setLayout(new GridLayout(1, false));
		
		addNegationControls(this);
		
		switch (domain) {
		case "Score": ruleCreator = new DoubleRuleCreator(this, domain);
			break;
		case "Name": ruleCreator = new StringRuleCreator(this, domain);
			break;
		case "Signature": ruleCreator = new StringRuleCreator(this, domain);
			break;
		case "Parent type": ruleCreator = new StringRuleCreator(this, domain);
			break;
		case "Path": ruleCreator = new StringRuleCreator(this, domain);
			break;
		case "Position": ruleCreator = new IntegerRuleCreator(this, domain);
			break;
		case "Context size": ruleCreator = new IntegerRuleCreator(this, domain);
			break;
		case "Interactivity": ruleCreator = new BooleanRuleCreator(this, domain);
			break;
		case "Last action": ruleCreator = new LastActionRuleCreator(this, domain);
			break;
		}
		
	}

	private Button negateButton;
	private RuleCreator ruleCreator;
	private static final String NEGATE = "Negate rule";
	private static final String DISABLE_NEGATION = "Rule is negated";
	
	private void addNegationControls(Composite parent) {
		this.negateButton = new Button(parent, SWT.TOGGLE);
		this.negateButton.setText(NEGATE);
		this.negateButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (negateButton.getSelection()) {
					negateButton.setText(DISABLE_NEGATION);
				} else {
					negateButton.setText(NEGATE);
				}
				negateButton.requestLayout();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
	}
	
	private boolean isNegated() {
		return this.negateButton.getSelection();
	}

	@Override
	public Rule getRule() {
		Rule rule = this.ruleCreator.getRule();
		if (rule != null) {
			rule.setNegated(isNegated());
		}	
		return rule;
	}

}
