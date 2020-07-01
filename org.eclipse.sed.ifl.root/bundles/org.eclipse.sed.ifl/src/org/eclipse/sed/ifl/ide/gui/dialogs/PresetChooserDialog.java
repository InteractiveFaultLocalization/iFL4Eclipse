package org.eclipse.sed.ifl.ide.gui.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wb.swt.ResourceManager;

public class PresetChooserDialog extends Dialog {

	protected Object result;
	protected Shell shlChoosePreset;

	private Label descriptionLabel;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PresetChooserDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlChoosePreset.open();
		shlChoosePreset.layout();
		Display display = getParent().getDisplay();
		while (!shlChoosePreset.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlChoosePreset = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shlChoosePreset.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/ico8.png"));
		shlChoosePreset.setSize(450, 300);
		shlChoosePreset.setText("Choose preset");
		shlChoosePreset.setLayout(new GridLayout(2, false));
		
		Label choosePresetLabel = new Label(shlChoosePreset, SWT.NONE);
		choosePresetLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		choosePresetLabel.setText("Choose preset:");
		
		Tree tree = new Tree(shlChoosePreset, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object dataObject = tree.getSelection()[0].getData("description");
				descriptionLabel.setText(dataObject == null ? "" : (String)dataObject );
				descriptionLabel.requestLayout();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		TreeItem domainItem = new TreeItem(tree, SWT.NONE);
		domainItem.setText("Domain");
		
		TreeItem trtmScore = new TreeItem(domainItem, SWT.NONE);
		trtmScore.setText("Score");
		
		TreeItem trtmShowTop = new TreeItem(trtmScore, SWT.NONE);
		trtmShowTop.setText("Show top 10 suspicious items");
		trtmShowTop.setData("description", "Shows the top 10 most suspicious items based on the score of the items.");
		trtmScore.setExpanded(true);
		
		TreeItem trtmName = new TreeItem(domainItem, SWT.NONE);
		trtmName.setText("Name");
		
		TreeItem trtmSignature = new TreeItem(domainItem, SWT.NONE);
		trtmSignature.setText("Signature");
		
		TreeItem trtmParentType = new TreeItem(domainItem, SWT.NONE);
		trtmParentType.setText("Parent type");
		
		TreeItem trtmPath = new TreeItem(domainItem, SWT.NONE);
		trtmPath.setText("Path");
		trtmPath.setExpanded(true);
		
		TreeItem trtmPosition = new TreeItem(domainItem, SWT.NONE);
		trtmPosition.setText("Position");
		
		TreeItem trtmContextSize = new TreeItem(domainItem, SWT.NONE);
		trtmContextSize.setText("Context size");
		
		TreeItem trtmInteractivity = new TreeItem(domainItem, SWT.NONE);
		trtmInteractivity.setText("Interactivity");
		
		TreeItem trtmLastAction = new TreeItem(domainItem, SWT.NONE);
		trtmLastAction.setText("Last action");
		domainItem.setExpanded(true);
		
		Label lblDescription = new Label(shlChoosePreset, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblDescription.setText("Description:");
		
		descriptionLabel = new Label(shlChoosePreset, SWT.WRAP);
		
		Button btnAddPreset = new Button(shlChoosePreset, SWT.NONE);
		btnAddPreset.setText("Add preset");
		new Label(shlChoosePreset, SWT.NONE);

	}

}
