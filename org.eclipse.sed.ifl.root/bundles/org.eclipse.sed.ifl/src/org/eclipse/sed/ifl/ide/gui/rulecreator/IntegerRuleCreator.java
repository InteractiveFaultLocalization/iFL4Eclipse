package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;

public class IntegerRuleCreator extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public IntegerRuleCreator(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label enterValueLabel = new Label(this, SWT.NONE);
		enterValueLabel.setText("Enter value:");
		
		Spinner spinner = new Spinner(this, SWT.BORDER);
		spinner.setMaximum(10000);
		
		Label addRelationLabel = new Label(this, SWT.NONE);
		addRelationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		addRelationLabel.setText("Add relation:");
		
		Combo addRelationCombo = new Combo(this, SWT.READ_ONLY);
		addRelationCombo.setItems(new String[] {"<", "<=", "=", ">=", ">"});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
