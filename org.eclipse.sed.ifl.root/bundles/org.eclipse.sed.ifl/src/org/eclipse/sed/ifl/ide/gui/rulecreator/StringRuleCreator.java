package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class StringRuleCreator implements RuleCreator{
	private Text text;
	private String domain;
	private Label matchingLabel;
	private Button matchingButton;
	private Label caseLabel;
	private Button caseButton;
	private Label regexLabel;
	private Button regexButton;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StringRuleCreator(Composite parent, String domain) {
		this.domain = domain;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblEnterString = new Label(composite, SWT.NONE);
		lblEnterString.setText("Enter string:");
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		matchingLabel = new Label(composite, SWT.NONE);
		matchingLabel.setText("Matching:");
		
		matchingButton = new Button(composite, SWT.TOGGLE);
		matchingButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/matching_no_v2.png"));
		matchingButton.setText("partial");
		matchingButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				matchingButton.setImage(matchingButton.getSelection() ? 
						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/matching_yes_v2.png") :
							ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/matching_no_v2.png"));
				matchingButton.setText(matchingButton.getSelection() ? "exact" : "partial");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		caseLabel = new Label(composite, SWT.NONE);
		caseLabel.setText("Case-sensitive:");
		
		caseButton = new Button(composite, SWT.TOGGLE);
		caseButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/c_sensitive_no.png"));
		caseButton.setText("no");
		caseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				caseButton.setImage(caseButton.getSelection() ? 
						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/c_sensitive_yes.png") :
							ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/c_sensitive_no.png"));
				caseButton.setText(caseButton.getSelection() ? "yes" : "no");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		regexLabel = new Label(composite, SWT.NONE);
		regexLabel.setText("Regular expression:");
		
		regexButton = new Button(composite, SWT.TOGGLE);
		regexButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/regex_no.png"));
		regexButton.setText("no");
		regexButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				regexButton.setImage(regexButton.getSelection() ? 
						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/regex_yes.png") :
							ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/regex_no.png"));
				regexButton.setText(regexButton.getSelection() ? "yes" : "no");
				matchingButton.setEnabled(!regexButton.getSelection());
				caseButton.setEnabled(!regexButton.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});

	}

	private boolean isInputValid(String input) {
		//checking if input is empty or consists of only whitespace chars
		String validation = text.getText().trim();
		validation.replaceAll("\\s+","");
		if(validation.equals("")) {
			MessageDialog.open(MessageDialog.ERROR, null, "Empty string input", "The provided input is an empty string."
					+ " Please enter a string that is not empty.", SWT.NONE);
			return false;
		}
		//checking if regex is valid
		if (regexButton.getSelection()) {
			try {
				Pattern.compile(input);
			} catch (PatternSyntaxException e) {
				MessageDialog.open(MessageDialog.ERROR, null, "Invalid regular expression", "The provided regular expression is invalid."
						+ " Please enter a valid regular expression.", SWT.NONE);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Rule getRule() {
		if(isInputValid(text.getText())) {
			return new StringRule(this.domain, text.getText(), matchingButton.getSelection(), caseButton.getSelection(), regexButton.getSelection());
		} else {
			return null;
		}
	}
	
}
