package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.sed.ifl.control.score.filter.BooleanRule;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.LastActionRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class RuleElementUI extends Composite {

	private Rule rule;
	private Image icon = null;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public RuleElementUI(Composite parent, int style, Rule rule) {
		super(parent, style);
		this.rule = rule;
		setLayout(new GridLayout(3, false));
		
		Label domainKeyLabel = new Label(this, SWT.NONE);
		domainKeyLabel.setText("Domain:");
		
		Label domainValueLabel = new Label(this, SWT.NONE);
		domainValueLabel.setText(this.rule.getDomain());
		
		Button removeButton = new Button(this, SWT.NONE);
		removeButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/remove_selection.png"));
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((Control) e.getSource()).getParent().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		Label ruleKeyLabel = new Label(this, SWT.NONE);
		ruleKeyLabel.setText("Rule:");
		
		Label ruleValueLabel = new Label(this, SWT.NONE);
		ruleValueLabel.setText(setRuleValueLabelText());
		if(icon != null) {
			ruleValueLabel.setImage(icon);
		}
		
		GridData gd_ruleValueLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_ruleValueLabel.widthHint = 250;
		ruleValueLabel.setLayoutData(gd_ruleValueLabel);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private String setRuleValueLabelText() {
		String rString = "";
		String containsString = null;
		switch(this.rule.getDomain()) {
		case "Score": rString = ((DoubleRule)this.rule).getRelation().concat(" ").concat(Double.toString(((DoubleRule)this.rule).getValue()));
			break;
		case "Name": containsString = ((StringRule)this.rule).isContains() ? "contains: " : "not contains: ";
			rString = containsString.concat(((StringRule)this.rule).getValue());
			break;
		case "Signature": containsString = ((StringRule)this.rule).isContains() ? "contains: " : "not contains: ";
			rString = containsString.concat(((StringRule)this.rule).getValue());
			break;
		case "Parent type": containsString = ((StringRule)this.rule).isContains() ? "contains: " : "not contains: ";
			rString = containsString.concat(((StringRule)this.rule).getValue());
			break;
		case "Path": containsString = ((StringRule)this.rule).isContains() ? "contains: " : "not contains: ";
			rString = containsString.concat(((StringRule)this.rule).getValue());
			break;
		case "Position": rString = ((DoubleRule)this.rule).getRelation().concat(" ").concat(Double.toString(((DoubleRule)this.rule).getValue()));
			break;
		case "Context size":  rString = ((DoubleRule)this.rule).getRelation().concat(" ").concat(Double.toString(((DoubleRule)this.rule).getValue()));
			break;
		case "Interactivity": rString = ((BooleanRule)this.rule).isValue() == true ? "interactive" : "not interactive";
			break;
		case "Last action": icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", ((LastActionRule)this.rule).getStatus().getIconPath());
			break;
		}
		return rString;
	}
}
