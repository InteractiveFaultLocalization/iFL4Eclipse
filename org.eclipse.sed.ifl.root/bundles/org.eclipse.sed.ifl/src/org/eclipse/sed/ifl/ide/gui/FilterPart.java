package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.general.IEmbeddable;
import org.eclipse.sed.ifl.general.IEmbedee;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

public class FilterPart extends ViewPart implements IEmbeddable, IEmbedee {

	public static final String ID = "org.eclipse.sed.ifl.views.IFLFilterView";
	
	private static final double SLIDER_PRECISION = 10000.0;
	private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	private static final DecimalFormat LIMIT_FORMAT = new DecimalFormat("#0.0000", symbols);
	
	private static int toScale(double value) {
		return Double.valueOf(value * SLIDER_PRECISION).intValue();
	}
	
	private static double fromScale(int value) {
		return value / SLIDER_PRECISION;
	}
	
	@Inject IWorkbench workbench;
	
	private Composite composite;
	private Composite limitFilterComposite;
	private Composite contextSizeComposite;
	private Composite nameFilterComposite;
	private Combo contextSizeCombo;
	private Combo limitFilterCombo;
	private Button contextSizeCheckBox;
	private Spinner contextSizeSpinner;
	private Button enabledCheckButton;
	private Scale scale;
	private Text manualText;
	private Button manualButton;
	private Text nameFilterText;
	private Button nameFilterClearButton;
	private Label minLabel;
	private Label maxLabel;
	
	private double minValue;
	private double maxValue;
	
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
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(1, false));
		
		limitFilterComposite = new Composite(composite, SWT.NONE);
		limitFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		limitFilterComposite.setSize(0, 100);
		limitFilterComposite.setLayout(new GridLayout(8, false));
		
		contextSizeComposite = new Composite(composite, SWT.NONE);
		contextSizeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		contextSizeComposite.setSize(0, 100);
		contextSizeComposite.setLayout(new GridLayout(11, false));
		
		enabledCheckButton = new Button(limitFilterComposite, SWT.CHECK);
		enabledCheckButton.setToolTipText("enable");
		enabledCheckButton.setEnabled(false);
		enabledCheckButton.setText("Load some defined scores to enable filters");
		enabledCheckButton.setSelection(true);
		enabledCheckButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				lowerScoreLimitEnabled.invoke(enabledCheckButton.getSelection());
				scale.setEnabled(enabledCheckButton.getSelection());
				manualText.setEnabled(enabledCheckButton.getSelection());
				manualButton.setEnabled(enabledCheckButton.getSelection());
				limitFilterCombo.setEnabled(enabledCheckButton.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		limitFilterCombo = new Combo(limitFilterComposite, SWT.READ_ONLY);
		limitFilterCombo.add("<=");
		limitFilterCombo.add(">=");
		limitFilterCombo.setText(">=");
		limitFilterCombo.setEnabled(false);
		limitFilterCombo.addSelectionListener(new SelectionListener () {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String text = limitFilterCombo.getText();
				updateLimitFilterRelation(text);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		manualText = new Text(limitFilterComposite, SWT.BORDER);
		manualText.setToolTipText("You may enter the score value manually here");
		manualText.setEnabled(false);
		manualText.addListener(SWT.Traverse, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if(event.detail == SWT.TRAVERSE_RETURN) {
		            double value = userInputTextValidator(manualText.getText());
		            double correctValue = userInputRangeValidator(value);
		            updateScoreFilterLimit(correctValue);
		        }
			}
		});
		
		manualButton = new Button(limitFilterComposite, SWT.NONE);
		manualButton.setText("Apply");
		manualButton.setEnabled(false);
		manualButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				double value = userInputTextValidator(manualText.getText());
		        double correctValue = userInputRangeValidator(value);
		        updateScoreFilterLimit(correctValue);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
					
			}
		});
		
		minLabel = new Label(limitFilterComposite, SWT.NONE);
		minLabel.setText("");
		
		scale = new Scale(limitFilterComposite, SWT.NONE);
		scale.setEnabled(false);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scale.setSelection(0);
		scale.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				double value = fromScale(scale.getSelection());
				updateScoreFilterLimit(value);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		maxLabel = new Label(limitFilterComposite, SWT.NONE);
		maxLabel.setText("");
		
		contextSizeCheckBox = new Button(contextSizeComposite, SWT.CHECK);
		contextSizeCheckBox.setEnabled(false);
		contextSizeCheckBox.setText("Show context size");
		contextSizeCheckBox.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				contextSizeLimitEnabled.invoke(contextSizeCheckBox.getSelection());
				contextSizeSpinner.setEnabled(contextSizeCheckBox.getSelection());
				contextSizeCombo.setEnabled(contextSizeCheckBox.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		contextSizeCombo = new Combo(contextSizeComposite, SWT.READ_ONLY);
		contextSizeCombo.add("<");
		contextSizeCombo.add("<=");
		contextSizeCombo.add("=");
		contextSizeCombo.add(">=");
		contextSizeCombo.add(">");
		contextSizeCombo.setText(">=");
		contextSizeCombo.setEnabled(false);
		contextSizeCombo.addSelectionListener(new SelectionListener () {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String text = contextSizeCombo.getText();
				System.out.println("Combo selected item: "+text);
				updateContextSizeRelation(text);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		contextSizeSpinner = new Spinner(contextSizeComposite, SWT.BORDER);
		contextSizeSpinner.setEnabled(false);
		contextSizeSpinner.setMaximum(1000);
		contextSizeSpinner.setMinimum(0);
		contextSizeSpinner.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int value = contextSizeSpinner.getSelection();
				updateContextSizeLimit(value);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		nameFilterComposite = new Composite(contextSizeComposite, SWT.NONE);
		nameFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		nameFilterComposite.setSize(0, 100);
		GridLayout nameLayout = new GridLayout(2, false);
		nameLayout.marginLeft = 60;
		nameFilterComposite.setLayout(nameLayout);
		
		nameFilterText = new Text(nameFilterComposite, SWT.BORDER);
		nameFilterText.setMessage("filter by name...");
		nameFilterText.setEnabled(false);
		nameFilterText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateNameFilter(nameFilterText.getText());
				
			}
			
		});
		
		nameFilterClearButton = new Button(nameFilterComposite, SWT.NONE);
		nameFilterClearButton.setText("Clear");
		nameFilterClearButton.setEnabled(false);
		nameFilterClearButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				nameFilterText.setText("");
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
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
	}
	
	public void setScoreFilter(Double min, Double max) {
		LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
		minValue = min;
		maxValue = max;
		maxLabel.setText(LIMIT_FORMAT.format((max)));
		//maxLabel.requestLayout();
		minLabel.setText(LIMIT_FORMAT.format((min)));
		//minLabel.requestLayout();
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
		enabledCheckButton.setSelection(true);
		lowerScoreLimitEnabled.invoke(true);
		manualText.setEnabled(true);
		manualButton.setEnabled(true);
		scale.setEnabled(true);
		contextSizeCheckBox.setEnabled(true);
		nameFilterText.setEnabled(true);
		nameFilterClearButton.setEnabled(true);
		limitFilterCombo.setEnabled(true);
		updateScoreFilterLimit(min);
	}
	
	private void updateScoreFilterLimit(double value) {
		scale.setSelection(toScale(value));
		String formattedValue = LIMIT_FORMAT.format(value);
		manualText.setText(formattedValue);
		enabledCheckButton.setText("Show scores");
		//enabledCheckButton.requestLayout();
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
	
	@Override
	public void setFocus() {
	}

}
