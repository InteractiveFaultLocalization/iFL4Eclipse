package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;

public class StringRuleCreator extends Composite implements RuleCreator{
	private Text text;
	private String domain;
	
	Button containsButton;
	Button notContainsButton;
	Button exactMatchingButton;
	Button partialMatchingButton;
	Button caseYes;
	Button caseNo;
	Button regexYes;
	Button regexNo;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StringRuleCreator(Composite parent, int style, String domain) {
		super(parent, style);
		this.domain = domain;
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
		
		containsButton = new Button(containmentGroup, SWT.RADIO);
		containsButton.setSelection(true);
		containsButton.setText("contains");
		
		notContainsButton = new Button(containmentGroup, SWT.RADIO);
		notContainsButton.setText("not contains");
		
		Group matchingGroup = new Group(this, SWT.NONE);
		matchingGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		matchingGroup.setText("Matching");
		matchingGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		exactMatchingButton = new Button(matchingGroup, SWT.RADIO);
		exactMatchingButton.setText("exact");
		
		partialMatchingButton = new Button(matchingGroup, SWT.RADIO);
		partialMatchingButton.setSelection(true);
		partialMatchingButton.setText("partial");
		
		Group caseGroup = new Group(this, SWT.NONE);
		caseGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		caseGroup.setText("Case-sensitive");
		caseGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		caseYes = new Button(caseGroup, SWT.RADIO);
		caseYes.setText("yes");
		
		caseNo = new Button(caseGroup, SWT.RADIO);
		caseNo.setSelection(true);
		caseNo.setText("no");
		
		Group regexGroup = new Group(this, SWT.NONE);
		regexGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		regexGroup.setText("Regular expression");
		regexGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		regexYes = new Button(regexGroup, SWT.RADIO);
		regexYes.setText("yes");
		
		regexNo = new Button(regexGroup, SWT.RADIO);
		regexNo.setSelection(true);
		regexNo.setText("no");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public Rule getRule() {
		String validation = text.getText();
		validation.replaceAll("\\s+","");
		if(!validation.isEmpty()) {
			return new StringRule(this.domain, text.getText(), containsButton.getSelection(), exactMatchingButton.getSelection(), caseYes.getSelection(), regexYes.getSelection());
		} else {
			MessageDialog.open(MessageDialog.ERROR, null, "Empty string input", "The provided input is an empty string."
					+ "Please enter a string that is not empty.", SWT.NONE);
			return null;
		}
	}
	
}
