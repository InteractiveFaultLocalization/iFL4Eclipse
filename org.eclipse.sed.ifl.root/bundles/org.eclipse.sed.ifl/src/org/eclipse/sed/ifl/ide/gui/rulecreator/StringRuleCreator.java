package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class StringRuleCreator extends Composite implements RuleCreator{
	private Text text;
	private String domain;
	private Label containsLabel;
	private Button containsButton;
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
	public StringRuleCreator(Composite parent, int style, String domain) {
		super(parent, style);
		this.domain = domain;
		setLayout(new GridLayout(2, false));
		
		Label lblEnterString = new Label(this, SWT.NONE);
		lblEnterString.setText("Enter string:");
		
		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		containsLabel = new Label(this, SWT.NONE);
		containsLabel.setText("Containment:");
		
		containsButton = new Button(this, SWT.TOGGLE);
		containsButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_yes.png"));
		containsButton.setText("contains");
		containsButton.setSelection(true);
		containsButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				containsButton.setImage(containsButton.getSelection() ? 
						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_yes.png") :
							ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_no.png"));
				containsButton.setText(containsButton.getSelection() ? "contains" : "not contains");
				containsButton.requestLayout();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		matchingLabel = new Label(this, SWT.NONE);
		matchingLabel.setText("Matching:");
		
		matchingButton = new Button(this, SWT.TOGGLE);
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
		
		caseLabel = new Label(this, SWT.NONE);
		caseLabel.setText("Case-sensitive:");
		
		caseButton = new Button(this, SWT.TOGGLE);
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
		
		regexLabel = new Label(this, SWT.NONE);
		regexLabel.setText("Regular expression:");
		
		regexButton = new Button(this, SWT.TOGGLE);
		regexButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/regex_no.png"));
		regexButton.setText("no");
		regexButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				regexButton.setImage(regexButton.getSelection() ? 
						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/regex_yes.png") :
							ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/regex_no.png"));
				regexButton.setText(regexButton.getSelection() ? "yes" : "no");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public Rule getRule() {
		String validation = text.getText().trim();
		validation.replaceAll("\\s+","");
		if(!validation.equals("")) {
			return new StringRule(this.domain, text.getText(), containsButton.getSelection(), matchingButton.getSelection(), caseButton.getSelection(), regexButton.getSelection());
		} else {
			MessageDialog.open(MessageDialog.ERROR, null, "Empty string input", "The provided input is an empty string."
					+ "Please enter a string that is not empty.", SWT.NONE);
			return null;
		}
	}
	
}
