package org.eclipse.sed.ifl.ide.gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.control.score.filter.Rule;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.ide.gui.dialogs.RuleCreatorDialog;
import org.eclipse.sed.ifl.ide.gui.element.RuleElementUI;
import org.eclipse.sed.ifl.ide.gui.rulecreator.BooleanRuleCreator;
import org.eclipse.sed.ifl.ide.gui.rulecreator.DoubleRuleCreator;
import org.eclipse.sed.ifl.ide.gui.rulecreator.IntegerRuleCreator;
import org.eclipse.sed.ifl.ide.gui.rulecreator.LastActionRuleCreator;
import org.eclipse.sed.ifl.ide.gui.rulecreator.StringRuleCreator;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.ScrolledComposite;

public class FilterPart extends ViewPart implements IEmbeddable, IEmbedee {

	public static final String ID = "org.eclipse.sed.ifl.views.IFLFilterView";
	
	@Inject IWorkbench workbench;
	
	private Composite composite;
	private Button addRuleButton;
	private ScrolledComposite scrolledComposite;
	private Button resetAllButton;
	private Composite rulesComposite;
	
	private List<Rule> rules = new ArrayList<Rule>();
	
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

	@Override
	public void createPartControl(Composite parent) {
		composite = parent;
		composite.setLayout(new GridLayout(2, false));
		
		addRuleButton = new Button(parent, SWT.NONE);
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
		
		resetAllButton = new Button(parent, SWT.NONE);
		resetAllButton.setText("Reset all");
		resetAllButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Control control : rulesComposite.getChildren()) {
					control.dispose();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		GridData gd_scrolledComposite = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		 gd_scrolledComposite.widthHint = 360;
		 gd_scrolledComposite.heightHint = 280;
		scrolledComposite.setLayoutData( gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		rulesComposite = new Composite(scrolledComposite, SWT.NONE);
		rulesComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		rulesComposite.setSize(new Point(360, 280));
		rulesComposite.setLayout(new GridLayout(1, false));
		scrolledComposite.setContent(rulesComposite);
		scrolledComposite.setMinSize(rulesComposite.getSize());
	}

	private IListener<Rule> ruleCreatedListener = event -> {
		RuleElementUI ruleElement = null;
		ruleElement = new RuleElementUI(rulesComposite, SWT.None, event);
		scrolledComposite.setMinSize(rulesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		ruleElement.requestLayout();
	};
	
	public void setScoreFilter(Double min, Double max, Double current) {
		setScoreFilter(min, max);
	}
	
	public void setScoreFilter(Double min, Double max) {
		
	}
	
	private void updateScoreFilterLimit(double value) {
		lowerScoreLimitChanged.invoke(value);
	}
	
	private void updateLimitFilterRelation(String text) {
		limitRelationChanged.invoke(text);
	}
	
	private void updateContextSizeLimit(int value) {
		contextSizeLimitChanged.invoke(value);
	}
	
	private void updateContextSizeRelation(String text) {
		contextSizeRelationChanged.invoke(text);
	}
	
	private void updateNameFilter(String text) {
		nameFilterChanged.invoke(text);
	}
	
	private NonGenericListenerCollection<Double> lowerScoreLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Double> eventlowerScoreLimitChanged() {
		return lowerScoreLimitChanged;
	}

	private NonGenericListenerCollection<Boolean> lowerScoreLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventlowerScoreLimitEnabled() {
		return lowerScoreLimitEnabled;
	}
	
	private NonGenericListenerCollection<Boolean> contextSizeLimitEnabled = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Boolean> eventContextSizeLimitEnabled() {
		return contextSizeLimitEnabled;
	}
	
	private NonGenericListenerCollection<Integer> contextSizeLimitChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Integer> eventContextSizeLimitChanged() {
		return contextSizeLimitChanged;
	}
	
	private NonGenericListenerCollection<String> limitRelationChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventLimitRelationChanged() {
		return limitRelationChanged;
	}
	
	private NonGenericListenerCollection<String> contextSizeRelationChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventContextSizeRelationChanged() {
		return contextSizeRelationChanged;
	}
	
	private NonGenericListenerCollection<String> nameFilterChanged = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<String> eventNameFilterChanged() {
		return nameFilterChanged;
	}
	
	private NonGenericListenerCollection<SortingArg> sortRequired = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<SortingArg> eventSortRequired() {
		return sortRequired;
	}
	
	/*
	Listener sortListener = new Listener() {
		public void handleEvent(Event e) {
			int dir;
			if(sortDescendingButton.getSelection()) {
				dir = SWT.DOWN;
			} else {
				dir = SWT.UP;
			}
			
			SortingArg arg;
			String text = sortCombo.getText();
			
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
			
			sortRequired.invoke(arg);
			saveState();
		}
		
	};
	
	*/
	
	@Override
	public void setFocus() {
	}
	
	@Override
	public void dispose() {
		this.getSite().getPage().hideView(this);
	}
	
}
