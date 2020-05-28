package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.sed.ifl.control.score.filter.BooleanRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;

public class BooleanRuleCreator extends Composite implements RuleCreator {

	private String domain;
	Button trueButton;
	Button falseButton;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public BooleanRuleCreator(Composite parent, int style, String domain) {
		super(parent, style);
		this.domain = domain;
		setLayout(new GridLayout(1, false));
		
		Group grpCondition = new Group(this, SWT.NONE);
		grpCondition.setText("Condition");
		grpCondition.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		trueButton = new Button(grpCondition, SWT.RADIO);
		trueButton.setText("true");
		
		falseButton = new Button(grpCondition, SWT.RADIO);
		falseButton.setSelection(true);
		falseButton.setText("false");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public Rule getRule() {
		return new BooleanRule(this.domain, trueButton.getSelection());
	}

}
