package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.control.score.filter.BooleanRule;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.LastActionRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.sed.ifl.control.score.filter.SortRule;
import org.eclipse.sed.ifl.ide.gui.icon.ScoreStatus;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;

public class RuleElementUI extends Composite {

	private Rule rule;
	
	public Rule getRule() {
		return rule;
	}

	private Image icon = null;
	private Label resultsValueLabel;
	
	private NonGenericListenerCollection<Rule> ruleDeleted = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Rule> eventruleDeleted() {
		return ruleDeleted;
	}
	
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
		removeButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		removeButton.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/remove_selection.png"));
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ruleDeleted.invoke(rule);
				((Control) e.getSource()).getParent().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		Label ruleKeyLabel = new Label(this, SWT.NONE);
		ruleKeyLabel.setText("Rule:");
		
		CLabel ruleValueLabel = new CLabel(this, SWT.NONE);
		ruleValueLabel.setText(setRuleValueLabelText());
		if(icon != null) {
			ruleValueLabel.setImage(icon);
		}
		
		GridData gd_ruleValueLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_ruleValueLabel.widthHint = 250;
		ruleValueLabel.setLayoutData(gd_ruleValueLabel);
		
		Label resultsLabel = new Label(this, SWT.NONE);
		resultsLabel.setText("Results in:");
		
		resultsValueLabel = new Label(this, SWT.NONE);
		resultsValueLabel.setText("-");
		
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
	
	private String setRuleValueLabelText() {
		String rString = "";
		String containsString = null;
		switch(this.rule.getDomain()) {
		case "Score": rString = ((DoubleRule)this.rule).getRelation().concat(" ").concat(Double.toString(((DoubleRule)this.rule).getValue()));
			icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/rule_score_3.png");
			break;
		case "Name": containsString = ((StringRule)this.rule).isContains() ? "contains: " : "not contains: ";
			rString = containsString.concat(((StringRule)this.rule).getValue());
			icon =  ((StringRule)this.rule).isContains() == true ? ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_yes.png") : ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_no.png");
			break;
		case "Signature": containsString = ((StringRule)this.rule).isContains() ? "contains: " : "not contains: ";
			rString = containsString.concat(((StringRule)this.rule).getValue());
			icon =  ((StringRule)this.rule).isContains() == true ? ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_yes.png") : ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_no.png");
			break;
		case "Parent type": containsString = ((StringRule)this.rule).isContains() ? "contains: " : "not contains: ";
			rString = containsString.concat(((StringRule)this.rule).getValue());
			icon =  ((StringRule)this.rule).isContains() == true ? ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_yes.png") : ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_no.png");
			break;
		case "Path": containsString = ((StringRule)this.rule).isContains() ? "contains: " : "not contains: ";
			rString = containsString.concat(((StringRule)this.rule).getValue());
			icon =  ((StringRule)this.rule).isContains() == true ? ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_yes.png") : ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/containment_no.png");
			break;
		case "Position": rString = ((DoubleRule)this.rule).getRelation().concat(" ").concat(Integer.toString(((DoubleRule)this.rule).getValue().intValue()));
			icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/position_3.png"); ;
			break;
		case "Context size":  rString = ((DoubleRule)this.rule).getRelation().concat(" ").concat(Integer.toString(((DoubleRule)this.rule).getValue().intValue()));
			icon =  ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/context_size.png");;
			break;
		case "Interactivity": rString = ((BooleanRule)this.rule).isValue() == true ? "interactive" : "not interactive";
			icon = ((BooleanRule)this.rule).isValue() == true ? ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/true.png") : ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/false.png");
			break;
		case "Last action": ScoreStatus status = ((LastActionRule)this.rule).getStatus();
			if(status.equals(ScoreStatus.INCREASED))
				rString = "increased";
			if(status.equals(ScoreStatus.DECREASED))
				rString = "decreased";
			if(status.equals(ScoreStatus.UNDEFINED))
					rString = "unchanged";
			icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", ((LastActionRule)this.rule).getStatus().getIconPath());
			break;
		case "Sort": SortingArg arg = ((SortRule)this.rule).getArg();
			String sortDomain = arg.getDomain();
			if(arg.isDescending()) {
				rString = sortDomain.concat(" descending");
				icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/down_arrow16.png");
			} else if(!arg.isDescending()) {
				rString = sortDomain.concat(" ascending");
				icon = ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/up_arrow16.png");
			}
			break;
		}
		return rString;
	}

	public void setResultNumber(int resultNumber) {
		resultsValueLabel.setText(resultNumber + " entries");
		resultsValueLabel.requestLayout();
	}
}
