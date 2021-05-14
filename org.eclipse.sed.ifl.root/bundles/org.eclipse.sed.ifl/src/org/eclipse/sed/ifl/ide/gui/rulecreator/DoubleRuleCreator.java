package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

public class DoubleRuleCreator implements RuleCreator{
	private Text addValueText;
	private String domain;
	private Combo combo;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DoubleRuleCreator(Composite parent, String domain) {
		this.domain = domain;
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		Label addValueLabel = new Label(composite, SWT.NONE);
		addValueLabel.setText("Enter value:");
		
		addValueText = new Text(composite, SWT.BORDER);
		addValueText.setToolTipText("Value must be <=0.0 and >=1.0");
		addValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label addRelationLabel = new Label(composite, SWT.NONE);
		addRelationLabel.setText("Choose relation:");
		
		combo = new Combo(composite, SWT.READ_ONLY);
		combo.setItems(new String[] {"<=", ">="});
		combo.setText(">=");

	}

	@Override
	public Rule getRule() {
		if(isUserInputValid(addValueText.getText())) {
			return new DoubleRule(this.domain, Double.parseDouble(addValueText.getText()), combo.getText());
		} else {
			return null;
		}
	}

	private boolean isUserInputValid(String text) {
		boolean rValue = false;
		try {
			double value = Double.parseDouble(text);
			if(value > 1.0 || value < 0.0) {
				throw new IllegalArgumentException();
			} else {
				rValue = true;
			}
		} catch (NumberFormatException e) {
			MessageDialog.open(MessageDialog.ERROR, null, "Input is not a number", "The provided input is not a number."
					+ " Please enter a string that can be converted into a number.", SWT.NONE);
		} catch (IllegalArgumentException e) {
			MessageDialog.open(MessageDialog.ERROR, null, "Input number is out of range", 
					"The provided input is out of range. The filter value must be >=0 and <=1.", SWT.NONE);
		}
		return rValue;
	}
}
