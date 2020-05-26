package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

public class DoubleRuleCreator extends Composite {
	private Text addValueText;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DoubleRuleCreator(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label addValueLabel = new Label(this, SWT.NONE);
		addValueLabel.setText("Enter value:");
		
		addValueText = new Text(this, SWT.BORDER);
		addValueText.setToolTipText("Value must be <=0.0 and >=1.0");
		addValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label addRelationLabel = new Label(this, SWT.NONE);
		addRelationLabel.setText("Choose relation:");
		
		Combo combo = new Combo(this, SWT.READ_ONLY);
		combo.setItems(new String[] {"<=", ">="});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
