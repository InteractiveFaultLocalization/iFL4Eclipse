package org.eclipse.sed.ifl.ide.gui.rulecreator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.SortRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

public class SortRuleCreator extends Composite implements RuleCreator {
	
	private String domain;
	
	private Combo sortByCombo;
	private Button sortDirectionButton;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SortRuleCreator(Composite parent, int style, String domain) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		this.domain = domain;
		
		Label sortByLabel = new Label(this, SWT.NONE);
		sortByLabel.setText("Sort by:");
		
		sortByCombo = new Combo(this, SWT.READ_ONLY);
		sortByCombo.setItems(new String[] {"Score", "Name", "Signature", "Parent type", "Path", "Position", "Context size", "Interactivity", "Last action"});
		sortByCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		sortByCombo.setText("Score");
		
		Label sortDirectionLabel = new Label(this, SWT.NONE);
		sortDirectionLabel.setText("Sort direction:");
		
		sortDirectionButton = new Button(this, SWT.TOGGLE);
		sortDirectionButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/down_arrow16.png"));
		sortDirectionButton.setText("descending");
		sortDirectionButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				sortDirectionButton.setImage(sortDirectionButton.getSelection() ? 
						ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/up_arrow16.png") :
							ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/down_arrow16.png"));
				sortDirectionButton.setText(sortDirectionButton.getSelection() ? "ascending" : "descending");
				sortDirectionButton.requestLayout();
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
		
		int dir;
		if(sortDirectionButton.getSelection()) {
			dir = SWT.UP;
		} else {
			dir = SWT.DOWN;
		}
		
		SortingArg arg;
		String text = sortByCombo.getText();
		
		switch(text) {
		case "Score": arg = SortingArg.Score;
			break;
		case "Name": arg = SortingArg.Name;
			break;
		case "Signature": arg = SortingArg.Signature;
			break;
		case "Parent type": arg = SortingArg.ParentType;
			break;
		case "Path": arg = SortingArg.Path;
			break;
		case "Context size": arg = SortingArg.ContextSize;
			break;
		case "Position": arg = SortingArg.Position;
			break;
		case "Interactivity": arg = SortingArg.Interactivity;
			break;
		case "Last action":  arg = SortingArg.LastAction;
			break;
		default: arg = SortingArg.Score;
			break;
		}
		
		arg.setDescending(dir == SWT.DOWN);
		
		return new SortRule(domain, arg);
	}

}
