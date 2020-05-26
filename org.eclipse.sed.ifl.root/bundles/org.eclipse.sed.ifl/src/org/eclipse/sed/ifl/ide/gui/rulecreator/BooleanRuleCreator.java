package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;

public class BooleanRuleCreator extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public BooleanRuleCreator(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Group grpCondition = new Group(this, SWT.NONE);
		grpCondition.setText("Condition");
		grpCondition.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button trueButton = new Button(grpCondition, SWT.RADIO);
		trueButton.setText("true");
		
		Button falseButton = new Button(grpCondition, SWT.RADIO);
		falseButton.setText("false");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
