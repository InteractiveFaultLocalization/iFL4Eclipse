package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;

public class StringRuleCreator extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StringRuleCreator(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label lblEnterString = new Label(this, SWT.NONE);
		lblEnterString.setText("Enter string:");
		
		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Group containmentGroup = new Group(this, SWT.NONE);
		GridData gd_containmentGroup = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_containmentGroup.widthHint = 264;
		containmentGroup.setLayoutData(gd_containmentGroup);
		containmentGroup.setText("Containment");
		containmentGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button containsButton = new Button(containmentGroup, SWT.RADIO);
		containsButton.setText("contains");
		
		Button notContainsButton = new Button(containmentGroup, SWT.RADIO);
		notContainsButton.setText("not contains");
		
		Group matchingGroup = new Group(this, SWT.NONE);
		matchingGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		matchingGroup.setText("Matching");
		matchingGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button exactMatchingButton = new Button(matchingGroup, SWT.RADIO);
		exactMatchingButton.setText("exact");
		
		Button partialMatchingButton = new Button(matchingGroup, SWT.RADIO);
		partialMatchingButton.setText("partial");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
