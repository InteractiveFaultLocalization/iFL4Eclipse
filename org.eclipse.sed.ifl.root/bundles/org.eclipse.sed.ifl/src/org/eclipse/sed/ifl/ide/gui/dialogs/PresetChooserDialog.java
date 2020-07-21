package org.eclipse.sed.ifl.ide.gui.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;

import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
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
import org.eclipse.wb.swt.SWTResourceManager;

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
		shlChoosePreset.setSize(450, 350);
		shlChoosePreset.setText("Choose preset");
		shlChoosePreset.setLayout(new GridLayout(2, false));
		
		Label choosePresetLabel = new Label(shlChoosePreset, SWT.NONE);
		choosePresetLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
		choosePresetLabel.setText("Choose preset:");
		
		Tree tree = new Tree(shlChoosePreset, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
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
		
		
		TreeItem trtmScore = new TreeItem(domainItem, SWT.NONE);
		
		
		TreeItem trtmShowTop = new TreeItem(trtmScore, SWT.NONE);
		
		trtmShowTop.setData("description", "Shows the top 10 most suspicious items based on the score of the items.\n"
				+ "Applies one fine-tuned Score domain rule.");
		trtmShowTop.setData("name", "TOP_10");
		trtmScore.setExpanded(true);
		
		TreeItem trtmName = new TreeItem(domainItem, SWT.NONE);
		TreeItem trtmSignature = new TreeItem(domainItem, SWT.NONE);
		TreeItem trtmParentType = new TreeItem(domainItem, SWT.NONE);
		TreeItem trtmPath = new TreeItem(domainItem, SWT.NONE);
		TreeItem trtmPosition = new TreeItem(domainItem, SWT.NONE);
		TreeItem trtmContextSize = new TreeItem(domainItem, SWT.NONE);
		TreeItem trtmInteractivity = new TreeItem(domainItem, SWT.NONE);
		TreeItem trtmLastAction = new TreeItem(domainItem, SWT.NONE);
		TreeItem trtmMultidomainPresets = new TreeItem(tree, SWT.NONE);
		
		/*setText of tree items is needed here - in the end - because children count would show false numbers
		  had the texts been set earlier*/
		domainItem.setText("Single-domain presets".concat(addChildrenCountToName(domainItem)));
		trtmScore.setText("Score".concat(addChildrenCountToName(trtmScore)));
		trtmShowTop.setText("Show top 10 suspicious items");
		trtmName.setText("Name".concat(addChildrenCountToName(trtmName)));
		trtmSignature.setText("Signature".concat(addChildrenCountToName(trtmSignature)));
		trtmParentType.setText("Parent type".concat(addChildrenCountToName(trtmParentType)));
		trtmPath.setText("Path".concat(addChildrenCountToName(trtmPath)));
		trtmPosition.setText("Position".concat(addChildrenCountToName(trtmPosition)));
		trtmContextSize.setText("Context size".concat(addChildrenCountToName(trtmContextSize)));
		trtmInteractivity.setText("Interactivity".concat(addChildrenCountToName(trtmInteractivity)));
		trtmLastAction.setText("Last action".concat(addChildrenCountToName(trtmLastAction)));
		trtmMultidomainPresets.setText("Multi-domain presets".concat(addChildrenCountToName(trtmMultidomainPresets)));
		
		Label lblDescription = new Label(shlChoosePreset, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
		lblDescription.setText("Description:");
		
		descriptionLabel = new Label(shlChoosePreset, SWT.WRAP);
		descriptionLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		
		Button btnAddPreset = new Button(shlChoosePreset, SWT.NONE);
		btnAddPreset.setText("Apply preset");
		new Label(shlChoosePreset, SWT.NONE);
		btnAddPreset.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(tree.getSelection()[0].getData("name") != null) {
					presetChosen.invoke((String)tree.getSelection()[0].getData("name"));
					System.out.println((String)tree.getSelection()[0].getData("name"));
				}
			}
			
		});
	}

	private String addChildrenCountToName(TreeItem item) {
		return " " + "(" + item.getItemCount() + ")";
	}
	
	private NonGenericListenerCollection<String> presetChosen = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventPresetChosen() {
		return presetChosen;
	}
}
