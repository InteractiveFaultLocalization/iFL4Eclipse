package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class IntegerRuleCreator implements RuleCreator {

	private String domain;
	private Spinner spinner;
	private Combo addRelationCombo;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public IntegerRuleCreator(Composite parent, String domain) {
		this.domain = domain;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		Label enterValueLabel = new Label(composite, SWT.NONE);
		enterValueLabel.setText("Enter value:");
		
		spinner = new Spinner(composite, SWT.BORDER);
		spinner.setMaximum(Integer.MAX_VALUE);
		
		Label addRelationLabel = new Label(composite, SWT.NONE);
		addRelationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		addRelationLabel.setText("Add relation:");
		
		addRelationCombo = new Combo(composite, SWT.READ_ONLY);
		addRelationCombo.setItems(new String[] {"<", "<=", "=", ">=", ">"});
		addRelationCombo.setText("<");

	}


	@Override
	public Rule getRule() {
		return new DoubleRule(this.domain, spinner.getSelection(), addRelationCombo.getText());
	}

}
