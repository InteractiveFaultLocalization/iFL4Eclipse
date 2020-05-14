package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

import java.util.Map.Entry;

import org.eclipse.sed.ifl.control.score.Score;
import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class SelectedElementUI extends Composite {
	private Text scoreValueLabel;
	private Text signatureValueLabel;
	private Entry<IMethodDescription, Score> originData;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unchecked")
	public SelectedElementUI(Composite parent, int style, CodeElementUI origin) {
		super(parent, style);
		this.originData = (Entry<IMethodDescription, Score>) origin.getData("entry");
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
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedRemoved.invoke(originData);
				((Control) e.getSource()).getParent().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		Label signatureIcon = new Label(this, SWT.NONE);
		signatureIcon.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/signature_blue.png"));
		
		Label signatureKeyLabel = new Label(this, SWT.NONE);
		signatureKeyLabel.setText("Signature");
		
		signatureValueLabel = new Text(this, SWT.READ_ONLY);
		signatureValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		signatureValueLabel.setText(origin.getSignatureValueLabel().getText());
		new Label(this, SWT.NONE);

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				((ScrolledComposite) getParent().getParent()).setMinSize(getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
				getParent().requestLayout();
			}
			
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private NonGenericListenerCollection<Entry<IMethodDescription, Score>> selectedRemoved = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Entry<IMethodDescription, Score>> eventSelectedRemoved() {
		return selectedRemoved;
	}
	
}
