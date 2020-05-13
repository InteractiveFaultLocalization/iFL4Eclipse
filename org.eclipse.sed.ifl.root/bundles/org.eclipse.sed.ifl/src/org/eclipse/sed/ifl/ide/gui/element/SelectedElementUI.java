package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class SelectedElementUI extends Composite {
	private Text scoreValueLabel;
	private Text signatureValueLabel;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SelectedElementUI(Composite parent, int style, CodeElementUI origin) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 350;
		setLayoutData(data);
		GridLayout gridLayout = new GridLayout(4, false);
		setLayout(gridLayout);
		
		Label scoreIcon = new Label(this, SWT.NONE);
		scoreIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/score_blue.png"));
		
		Label scoreKeyLabel = new Label(this, SWT.NONE);
		scoreKeyLabel.setText("Score:");
		
		scoreValueLabel = new Text(this, SWT.READ_ONLY);
		scoreValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scoreValueLabel.setText(origin.getScoreValueLabel().getText());
		
		Button removeButton = new Button(this, SWT.NONE);
		removeButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/remove_selection.png"));
		
		Label signatureIcon = new Label(this, SWT.NONE);
		signatureIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/signature_blue.png"));
		
		Label signatureKeyLabel = new Label(this, SWT.NONE);
		signatureKeyLabel.setText("Signature");
		
		signatureValueLabel = new Text(this, SWT.READ_ONLY);
		signatureValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		signatureValueLabel.setText(origin.getSignatureValueLabel().getText());
		new Label(this, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
