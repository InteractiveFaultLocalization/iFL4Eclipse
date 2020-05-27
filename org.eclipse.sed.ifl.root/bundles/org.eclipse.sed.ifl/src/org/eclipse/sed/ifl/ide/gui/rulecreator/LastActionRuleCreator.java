package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;

public class LastActionRuleCreator extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LastActionRuleCreator(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Group grpLastAction = new Group(this, SWT.NONE);
		grpLastAction.setText("Last action");
		grpLastAction.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button increasedButton = new Button(grpLastAction, SWT.RADIO);
		increasedButton.setText("increased");
		
		Button decreasedButton = new Button(grpLastAction, SWT.RADIO);
		decreasedButton.setText("decreased");
		
		Button unchangedButton = new Button(grpLastAction, SWT.RADIO);
		unchangedButton.setText("unchanged");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
