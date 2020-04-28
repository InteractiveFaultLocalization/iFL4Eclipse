package org.eclipse.sed.ifl.ide.gui;

import java.awt.BorderLayout;
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
import org.eclipse.sed.ifl.util.profile.NanoWatch;
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
import org.eclipse.swt.widgets.TableColumn;
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
	private Composite scaleComposite;
	private Composite contextSizeComposite;
	private Composite nameFilterComposite;
	private Composite sortComposite;
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
	private Combo sortCombo;
	private Button sortAscendingButton;
	private Button sortDescendingButton;
	
	private double minValue;
	private double maxValue;
	
	private static Boolean restoreStateNeeded = false;
	
	private static Double scaleValue;
	private static Boolean limitFilterEnabled;
	private static Boolean contextFilterEnabled;
	private static String limitFilterRelation;
	private static String contextFilterRelation;
	private static Integer contextFilterNumber;
	private static String nameFilterString;
	
	public FilterPart() {
		System.out.println("filter part ctr");
	}

	
	private void saveState() {
		limitFilterEnabled = enabledCheckButton.getSelection();
		contextFilterEnabled = contextSizeCheckBox.getSelection();
		scaleValue = fromScale(scale.getSelection());
		limitFilterRelation = limitFilterCombo.getText();
		contextFilterRelation = contextSizeCombo.getText();
		contextFilterNumber = contextSizeSpinner.getSelection();
		nameFilterString = nameFilterText.getSelectionText();
		
		restoreStateNeeded = true;
	}
	
	private void restoreState() {
		enabledCheckButton.setEnabled(limitFilterEnabled);
		enabledCheckButton.setSelection(limitFilterEnabled);
		lowerScoreLimitEnabled.invoke(enabledCheckButton.getSelection());
		scale.setEnabled(enabledCheckButton.getSelection());
		manualText.setEnabled(enabledCheckButton.getSelection());
		manualButton.setEnabled(enabledCheckButton.getSelection());
		limitFilterCombo.setEnabled(enabledCheckButton.getSelection());
		scale.setSelection(toScale(scaleValue));
		manualText.setText(scaleValue.toString());
		limitFilterCombo.setText(limitFilterRelation);
		updateScoreFilterLimit(fromScale(scale.getSelection()));
		updateLimitFilterRelation(limitFilterCombo.getText());
		
		contextSizeCheckBox.setEnabled(contextFilterEnabled);
		contextSizeCheckBox.setSelection(contextFilterEnabled);
		contextSizeSpinner.setEnabled(contextSizeCheckBox.getSelection());
		contextSizeCombo.setEnabled(contextSizeCheckBox.getSelection());
		contextSizeCombo.setText(contextFilterRelation);
		contextSizeSpinner.setSelection(contextFilterNumber);
		contextSizeLimitEnabled.invoke(contextSizeCheckBox.getSelection());
		
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
		composite.setLayoutData(BorderLayout.CENTER);
		composite.setLayout(new GridLayout(1, false));
		
		limitFilterComposite = new Composite(composite, SWT.NONE);
		limitFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		limitFilterComposite.setSize(0, 100);
		limitFilterComposite.setLayout(new GridLayout(4, false));
		
		scaleComposite = new Composite(composite, SWT.NONE);
		scaleComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		scaleComposite.setSize(0, 100);
		scaleComposite.setLayout(new GridLayout(3, false));
		
		contextSizeComposite = new Composite(composite, SWT.NONE);
		contextSizeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		contextSizeComposite.setSize(0, 100);
		contextSizeComposite.setLayout(new GridLayout(5, false));
		
		sortComposite = new Composite(composite, SWT.NONE);
		sortComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		sortComposite.setSize(0, 100);
		sortComposite.setLayout(new GridLayout(4, false));
		
		sortCheckButton = new Button(sortComposite, SWT.CHECK);
		sortCheckButton.setEnabled(false);
		sortCheckButton.setText("Sort by:");
		sortCheckButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				sortCombo.setEnabled(sortCheckButton.getSelection());
				sortDescendingButton.setEnabled(sortCheckButton.getSelection());
				sortAscendingButton.setEnabled(sortCheckButton.getSelection());
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			
		});
		sortCheckButton.addListener(SWT.Selection, sortListener);
		
		sortCombo = new Combo(sortComposite, SWT.READ_ONLY);
		sortCombo.setEnabled(false);
		sortCombo.add("Score");
		sortCombo.add("Name");
		sortCombo.add("Signature");
		sortCombo.add("Parent type");
		sortCombo.add("Path");
		sortCombo.add("Context size");
		sortCombo.add("Position");
		sortCombo.add("Interactivity");
		sortCombo.add("Last action");
		sortCombo.setText("Score");
		sortCombo.addListener(SWT.Selection, sortListener);
		
		sortAscendingButton = new Button(sortComposite, SWT.RADIO);
		sortAscendingButton.setEnabled(false);
		sortAscendingButton.setText("A -> Z");
		sortAscendingButton.addListener(SWT.Selection, sortListener);
		
		sortDescendingButton = new Button(sortComposite, SWT.RADIO);
		sortDescendingButton.setSelection(true);
		sortDescendingButton.setEnabled(false);
		sortDescendingButton.setText("Z -> A");
		sortDescendingButton.addListener(SWT.Selection, sortListener);
		
		enabledCheckButton = new Button(limitFilterComposite, SWT.CHECK);
		enabledCheckButton.setToolTipText("enable");
		enabledCheckButton.setEnabled(false);
		enabledCheckButton.setText("Show scores");
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
		
		minLabel = new Label(scaleComposite, SWT.NONE);
		GridData gd_minLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_minLabel.minimumWidth = 20;
		minLabel.setLayoutData(gd_minLabel);
		
		scale = new Scale(scaleComposite, SWT.NONE);
		GridData gd_scale = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_scale.widthHint = 405;
		scale.setLayoutData(gd_scale);
		scale.setEnabled(false);
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
		
		maxLabel = new Label(scaleComposite, SWT.NONE);
		GridData gd_maxLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_maxLabel.minimumWidth = 20;
		maxLabel.setLayoutData(gd_maxLabel);
		
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
		new Label(contextSizeComposite, SWT.NONE);
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
		sortCheckButton.setEnabled(true);
		updateScoreFilterLimit(min);
		saveState();
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
	private Button sortCheckButton;
	
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
		}
		
	};
	
	@Override
	public void setFocus() {
	}
	
	@Override
	public void dispose() {
		this.getSite().getPage().hideView(this);
	}
	
}
