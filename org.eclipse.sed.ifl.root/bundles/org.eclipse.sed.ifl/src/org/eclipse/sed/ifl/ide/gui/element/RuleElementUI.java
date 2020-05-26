package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class RuleElementUI extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public RuleElementUI(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		Label domainKeyLabel = new Label(this, SWT.NONE);
		domainKeyLabel.setText("Domain:");
		
		Label domainValueLabel = new Label(this, SWT.NONE);
		domainValueLabel.setText("New Label");
		
		Button removeButton = new Button(this, SWT.NONE);
		removeButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/remove_selection.png"));
		
		Label ruleKeyLabel = new Label(this, SWT.NONE);
		ruleKeyLabel.setText("Rule:");
		
		Label ruleValueLabel = new Label(this, SWT.NONE);
		GridData gd_ruleValueLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_ruleValueLabel.widthHint = 140;
		ruleValueLabel.setLayoutData(gd_ruleValueLabel);
		ruleValueLabel.setText("New Label");
		new Label(this, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
