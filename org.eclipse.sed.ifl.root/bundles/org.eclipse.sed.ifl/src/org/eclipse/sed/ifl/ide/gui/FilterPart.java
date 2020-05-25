package org.eclipse.sed.ifl.ide.gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.score.SortingArg;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.custom.ScrolledComposite;

public class FilterPart extends ViewPart implements IEmbeddable, IEmbedee {

	public static final String ID = "org.eclipse.sed.ifl.views.IFLFilterView";
	
	private static final double SLIDER_PRECISION = 10000.0;
	private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	private static final DecimalFormat LIMIT_FORMAT = new DecimalFormat("#0.0000", symbols);
	
	private static int toScale(double value) {
		return Double.valueOf(value * SLIDER_PRECISION).intValue();
	}
	
	public static void setRestoreStateNeeded(Boolean restoreStateNeeded) {
		FilterPart.restoreStateNeeded = restoreStateNeeded;
	}

	private static double fromScale(int value) {
		return value / SLIDER_PRECISION;
	}
	
	@Inject IWorkbench workbench;
	
	private Composite composite;
	
	private static double minValue;
	private static double maxValue;
	
	private static Boolean restoreStateNeeded = false;
	
	private static Double scaleValue;
	private static Boolean limitFilterEnabled;
	private static Boolean limitFilterChecked;
	private static Boolean contextFilterEnabled;
	private static Boolean contextFilterChecked;
	private static String limitFilterRelation;
	private static String contextFilterRelation;
	private static Integer contextFilterNumber;
	private static String nameFilterString;
	private static Boolean sortEnabled;
	private static Boolean sortChecked;
	private static String sortString;
	private static Boolean sortDescendingChosen;
	private static Boolean sortAscendingChosen;
	
	public FilterPart() {
		System.out.println("filter part ctr");
	}

	
	private void saveState() {
		limitFilterEnabled = enabledCheckButton.isEnabled();
		limitFilterChecked = enabledCheckButton.getSelection();
		contextFilterEnabled = contextSizeCheckBox.isEnabled();
		contextFilterChecked = contextSizeCheckBox.getSelection();
		
		scaleValue = Double.parseDouble(manualText.getText());
		
		limitFilterRelation = limitFilterCombo.getText();
		contextFilterRelation = contextSizeCombo.getText();
		contextFilterNumber = contextSizeSpinner.getSelection();
		nameFilterString = nameFilterText.getText();
		sortEnabled = sortCheckButton.isEnabled();
		sortChecked = sortCheckButton.getSelection();
		sortString = sortCombo.getText();
		sortAscendingChosen = sortAscendingButton.getSelection();
		sortDescendingChosen = sortDescendingButton.getSelection();
		
		restoreStateNeeded = true;
	}
	
	private void restoreState() {
		enabledCheckButton.setEnabled(limitFilterEnabled);
		enabledCheckButton.setSelection(limitFilterChecked);
		lowerScoreLimitEnabled.invoke(enabledCheckButton.getSelection());
		
		scale.setEnabled(enabledCheckButton.getSelection());
		manualText.setEnabled(enabledCheckButton.getSelection());
		manualButton.setEnabled(enabledCheckButton.getSelection());
		limitFilterCombo.setEnabled(enabledCheckButton.getSelection());
		scale.setSelection(toScale(scaleValue));
		manualText.setText(LIMIT_FORMAT.format(scaleValue));
		limitFilterCombo.setText(limitFilterRelation);
		updateLimitFilterRelation(limitFilterCombo.getText());
		
		contextSizeCheckBox.setEnabled(contextFilterEnabled);
		contextSizeCheckBox.setSelection(contextFilterChecked);
		contextSizeSpinner.setEnabled(contextSizeCheckBox.getSelection());
		contextSizeCombo.setEnabled(contextSizeCheckBox.getSelection());
		contextSizeCombo.setText(contextFilterRelation);
		contextSizeSpinner.setSelection(contextFilterNumber);
		contextSizeLimitEnabled.invoke(contextSizeCheckBox.getSelection());
		
		sortCheckButton.setEnabled(sortEnabled);
		sortCheckButton.setSelection(sortChecked);
		sortCombo.setText(sortString);
		sortAscendingButton.setSelection(sortAscendingChosen);
		sortDescendingButton.setSelection(sortDescendingChosen);
		sortCheckButton.notifyListeners(SWT.Selection, new Event());
		
		setScoreFilter(minValue, maxValue, scaleValue);

		nameFilterText.setText(nameFilterString);
		updateNameFilter(nameFilterText.getText());
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
		
		resetAllButton = new Button(parent, SWT.NONE);
		resetAllButton.setText("Reset all");
		
		scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		rulesComposite = new Composite(scrolledComposite, SWT.NONE);
		rulesComposite.setSize(new Point(378, 427));
		scrolledComposite.setContent(rulesComposite);
		scrolledComposite.setMinSize(rulesComposite.getSize());
		
		if(restoreStateNeeded) {
			restoreState();
		}
	}

	public double userInputTextValidator(String input) {
		double returnValue;
		try {
			returnValue = Double.parseDouble(input);
		} catch (NumberFormatException e) {
			returnValue = Double.valueOf(LIMIT_FORMAT.format(minValue));
			MessageDialog.open(MessageDialog.ERROR, null, "Input format error",
			"User provided upper score limit is not a number." + System.lineSeparator() + 
			"Your input " + input + " could not be interpreted as a number." + System.lineSeparator() +
			"Upper score limit has been set to minimum value.", SWT.NONE);
		}
		return returnValue;
	}
			
	public double userInputRangeValidator(double input) {
		double returnValue = input;
		if (input < Double.valueOf(LIMIT_FORMAT.format(minValue))) {
			returnValue = Double.valueOf(LIMIT_FORMAT.format(minValue));
			MessageDialog.open(MessageDialog.ERROR, null, "Input range error",
			"User provided upper score limit " + LIMIT_FORMAT.format(input) + 
			" is lesser than " + LIMIT_FORMAT.format(minValue) + " minimum value." + System.lineSeparator() + 
			"Upper score limit has been set to minimum value.", SWT.NONE);
		}
		if (input > Double.valueOf(LIMIT_FORMAT.format(maxValue))) {
			returnValue = Double.valueOf(LIMIT_FORMAT.format(maxValue));
			MessageDialog.open(MessageDialog.ERROR, null, "Input format error",
			"User provided upper score limit " + LIMIT_FORMAT.format(input) + 
			" is greater than " + LIMIT_FORMAT.format(maxValue) + " maximum value." + System.lineSeparator() + 
			"Upper score limit has been set to maximum value.", SWT.NONE);
		}
		return returnValue;
	}
	
	public void setScoreFilter(Double min, Double max, Double current) {
		setScoreFilter(min, max);
		if (min < current && current < max) {
			scale.setSelection(toScale(current));
			updateScoreFilterLimit(current);
		}
		saveState();
	}
	
	public void setScoreFilter(Double min, Double max) {
		descLabel.setVisible(false);
		LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
		minValue = min;
		maxValue = max;
		maxLabel.setText(LIMIT_FORMAT.format((max)));
		
		minLabel.setText(LIMIT_FORMAT.format((min)));
	
		/*This if statement below is needed because the setMaximum function of Scale
		 * does not set the maximum value of the scale if said value is lesser than or
		 * equal to the minimum value of the scale. This can happen when all elements' scores
		 * are set to 0. In this scenario, the minimum value of the scale would be set
		 * to 0 and then the function tries to set the maximum value to 0 but fails to do so
		 * because (max value = min value).
		 * @Dhorvath1294
		 */
		if(max <= min) {
			scale.setMaximum(toScale(max+(1/SLIDER_PRECISION)));
		}
		scale.setMinimum(toScale(min));
		scale.setMaximum(toScale(max));
		scale.setMinimum(toScale(min));
		enabledCheckButton.setEnabled(true);
		if(!restoreStateNeeded) {
			enabledCheckButton.setSelection(true);
			lowerScoreLimitEnabled.invoke(true);
		}
		manualText.setEnabled(enabledCheckButton.getSelection());
		manualButton.setEnabled(enabledCheckButton.getSelection());
		scale.setEnabled(enabledCheckButton.getSelection());
		contextSizeCheckBox.setEnabled(true);
		nameFilterText.setEnabled(true);
		nameFilterClearButton.setEnabled(true);
		limitFilterCombo.setEnabled(true);
		sortCheckButton.setEnabled(true);
		updateScoreFilterLimit(min);
		maxLabel.requestLayout();
		minLabel.requestLayout();
	}
	
	private void updateScoreFilterLimit(double value) {
		scale.setSelection(toScale(value));
		String formattedValue = LIMIT_FORMAT.format(value);
		manualText.setText(formattedValue);
		enabledCheckButton.setText("Show scores");
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
	private Button addRuleButton;
	private ScrolledComposite scrolledComposite;
	private Button resetAllButton;
	private Composite rulesComposite;
	
	@Override
	public void setFocus() {
	}
	
	@Override
	public void dispose() {
		this.getSite().getPage().hideView(this);
	}
	
}
