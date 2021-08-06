package org.eclipse.sed.ifl.ide.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.filter.BooleanRule;
import org.eclipse.sed.ifl.control.score.filter.DoubleRule;
import org.eclipse.sed.ifl.control.score.filter.LastActionRule;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.control.score.filter.SortRule;
import org.eclipse.sed.ifl.control.score.filter.StringRule;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.dialogs.PresetChooserDialog;
import org.eclipse.sed.ifl.ide.gui.dialogs.RuleCreatorDialog;
import org.eclipse.sed.ifl.ide.gui.element.RuleElementUI;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class FilterPart implements IEmbeddable, IEmbedee {

	public static final String ID = "org.eclipse.sed.ifl.views.IFLFilterView";

	private Composite composite;
	private Button addRuleButton;
	private ScrolledComposite scrolledComposite;
	private Button resetAllButton;
	private Composite rulesComposite;

	private static Boolean filterEnabled = false;

	private static List<Rule> rules = new ArrayList<>();

	public FilterPart() {
		System.out.println("filter part ctr");
	}

	@Override
	public void embed(IEmbeddable embedded) {
		embedded.setParent(composite);
	}

	@Override
	public void setParent(Composite parent) {
		composite.setParent(parent);
	}

	@PostConstruct
	public void createPartControl(Composite parent) {

		composite = parent;
		composite.setLayout(new GridLayout(3, false));

		enableInfoLabel = new Label(parent, SWT.NONE);
		enableInfoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		enableInfoLabel.setText("Load some defined scores to enable filtering.");

		addRuleButton = new Button(parent, SWT.NONE);
		addRuleButton.setEnabled(false);
		addRuleButton.setText("Add rule");
		addRuleButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RuleCreatorDialog ruleDialog = new RuleCreatorDialog(Display.getCurrent().getActiveShell(), SWT.NONE);
				ruleDialog.eventRuleCreated().add(ruleCreatedListener);
				ruleDialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
				
		choosePresetButton = new Button(parent, SWT.NONE);
		choosePresetButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		choosePresetButton.setText("Choose preset");
		choosePresetButton.setEnabled(false);
		choosePresetButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				PresetChooserDialog presetDialog = new PresetChooserDialog(Display.getCurrent().getActiveShell(),
						SWT.NONE);
				presetDialog.eventPresetChosen().add(presetChosenListener);
				presetDialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
		
		resetAllButton = new Button(parent, SWT.NONE);
		resetAllButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		resetAllButton.setEnabled(false);
		resetAllButton.setText("Reset all");
		resetAllButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteRules.invoke(rules);
				for (Control control : rulesComposite.getChildren()) {
					control.dispose();
				}
			}
				@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
		
		scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		GridData gd_scrolledComposite = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		gd_scrolledComposite.widthHint = 320;
		gd_scrolledComposite.heightHint = 280;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		rulesComposite = new Composite(scrolledComposite, SWT.NONE);
		rulesComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		rulesComposite.setSize(new Point(320, 280));
		rulesComposite.setLayout(new GridLayout(1, false));
		scrolledComposite.setContent(rulesComposite);
		scrolledComposite.setMinSize(new Point(320, 280));

		System.out.println(rules.size());

		if (filterEnabled.booleanValue()) {
			enableFiltering();
		}
		showRules();
	}

	private NonGenericListenerCollection<List<Rule>> deleteRules = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<List<Rule>> eventDeleteRules() {
		return deleteRules;
	}

	private IListener<Rule> ruleDeleted = rule -> {
		rules.remove(rule);
		List<Rule> list = new ArrayList<>();
		list.add(rule);
		deleteRules.invoke(list);
	};

	private IListener<String> presetChosenListener = string -> {
		boolean answer = MessageDialog.open(MessageDialog.QUESTION, null, "Warning",
				"Applying a preset overwrites all currently applied rules.\n" + "Proceed?", SWT.NONE);
		if (answer) {
			deleteRules.invoke(rules);
			for (Control control : rulesComposite.getChildren()) {
				control.dispose();
			}
			switch (string) {
			case "TOP_10":
				getTopTenLimit();
				break;
			default:
				break;
			}
		}
	};

	private void getTopTenLimit() {
		getTopTenLimit.invoke(new EmptyEvent());
	}

	private NonGenericListenerCollection<EmptyEvent> getTopTenLimit = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<EmptyEvent> eventGetTopTenLimit() {
		return getTopTenLimit;
	}

	private void showRules() {
		if (!rules.isEmpty()) {
			for (Rule rule : rules) {

				RuleElementUI ruleElement = null;
				ruleElement = new RuleElementUI(rulesComposite, SWT.None, rule);
				ruleElement.eventruleDeleted().add(ruleDeleted);
				scrolledComposite.setMinSize(rulesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				ruleElement.requestLayout();
			}
		}
	}

	public void setResultNumber(Rule rule, int resultNumber) {
		if (rulesComposite.isDisposed()) {
			return;
		}
		for (Control control : rulesComposite.getChildren()) {
			if (rule == ((RuleElementUI) control).getRule()) {
				((RuleElementUI) control).setResultNumber(resultNumber);
			}
		}
	}

	private IListener<Rule> ruleCreatedListener = event -> {
		RuleElementUI ruleElement = null;
		ruleElement = new RuleElementUI(rulesComposite, SWT.None, event);
		ruleElement.eventruleDeleted().add(ruleDeleted);
		scrolledComposite.setMinSize(rulesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		ruleElement.requestLayout();
		rules.add(event);
		ruleAdded(event);
	};

	private NonGenericListenerCollection<StringRule> stringRuleAdded = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<StringRule> eventStringRuleAdded() {
		return stringRuleAdded;
	}

	private void ruleAdded(Rule rule) {


		if (rule instanceof StringRule) {
			stringRuleAdded.invoke((StringRule) rule);
		}
		if (rule instanceof DoubleRule) {
			doubleRuleAdded.invoke((DoubleRule) rule);
		}
		if (rule instanceof BooleanRule) {
			booleanRuleAdded.invoke((BooleanRule) rule);
		}
		if (rule instanceof LastActionRule) {
			lastActionRuleAdded.invoke((LastActionRule) rule);
		}
	}

	private NonGenericListenerCollection<SortRule> sortRuleAdded = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<SortRule> eventSortRuleAdded() {
		return sortRuleAdded;
	}

	private NonGenericListenerCollection<DoubleRule> doubleRuleAdded = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<DoubleRule> eventDoubleRuleAdded() {
		return doubleRuleAdded;
	}

	private NonGenericListenerCollection<BooleanRule> booleanRuleAdded = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<BooleanRule> eventBooleanRuleAdded() {
		return booleanRuleAdded;
	}

	private NonGenericListenerCollection<LastActionRule> lastActionRuleAdded = new NonGenericListenerCollection<>();
	private Label enableInfoLabel;
	private Button choosePresetButton;

	public INonGenericListenerCollection<LastActionRule> eventLastActionRuleAdded() {
		return lastActionRuleAdded;
	}

	@Focus
	public void setFocus() {
	}


	public void dispose() {
		//this.getSite().getPage().hideView(this);
	}

	public void enableFiltering() {
		enableInfoLabel.setVisible(false);
		enableInfoLabel.requestLayout();
		addRuleButton.setEnabled(true);
		resetAllButton.setEnabled(true);
		choosePresetButton.setEnabled(true);

		filterEnabled = true;
	}

	public void terminate() {
		deleteRules.invoke(rules);
		rules.clear();
		if(!rulesComposite.isDisposed()) {
			for (Control control : rulesComposite.getChildren()) {
				control.dispose();
			}
		}
		filterEnabled = false;
	}
	
	public void applyTopScorePreset(Double limit) {
		ruleCreatedListener.invoke(new DoubleRule("Score", limit, ">="));
	}

}
