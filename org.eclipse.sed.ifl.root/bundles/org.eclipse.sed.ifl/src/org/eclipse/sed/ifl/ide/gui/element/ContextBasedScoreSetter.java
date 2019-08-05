package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.ui.Setter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ContextBasedScoreSetter extends Composite {

	private Label title;
	private Text newScore;
	private Scale scale;
	
	private static final double SLIDER_PRECISION = 10000.0;
	private static final DecimalFormat FORMAT = new DecimalFormat("#0.00");
	
	private int toScale(double value) {
		return Double.valueOf((upperLimit - value) * SLIDER_PRECISION).intValue();
	}
	
	private double fromScale(int value) {
		return upperLimit - value / SLIDER_PRECISION;
	}
	
	private double lowerLimit = 0.0;
	private double upperLimit = 1.0;
	
	private double ratioOf(double ratio) {
		double temp = lowerLimit * (1.0 - ratio) + upperLimit * ratio;
		System.out.println(temp);
		return temp;
	}
	
	private List<Double> dataPoints;
	
	public void setDataPoints(List<Double> points) {
		dataPoints = new ArrayList<>(points);
	}
	
	private double getMin() {
		return Collections.min(dataPoints);
	}

	private double getMax() {
		return Collections.max(dataPoints);
	}

	public ContextBasedScoreSetter(Composite parent, int style) {
		super(parent, style);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.center = true;
		rowLayout.justify = true;
		rowLayout.fill = true;
		setLayout(rowLayout);
		
		Composite mainSector = new Composite(this, SWT.NONE);
		RowLayout rl_mainSector = new RowLayout(SWT.VERTICAL);
		rl_mainSector.center = true;
		rl_mainSector.justify = true;
		mainSector.setLayout(rl_mainSector);
		
		title = new Label(mainSector, SWT.NONE);
		title.setAlignment(SWT.CENTER);
		title.setText("(noname)");

		Button active = new Button(mainSector, SWT.CHECK);
		active.setText("active");
		active.addListener(SWT.Selection, event -> Setter.RecursiveEnable(settingSector, active.getSelection()));
		active.setSelection(true);
		
		settingSector = new Composite(mainSector, SWT.NONE);
		RowLayout rl_settingSector = new RowLayout(SWT.VERTICAL);
		rl_settingSector.center = true;
		rl_settingSector.justify = true;
		settingSector.setLayout(rl_settingSector);
		
		Button setUpper = new Button(settingSector, SWT.CENTER);
		setUpper.setText("Set to 1");
		setUpper.addListener(SWT.Selection, event -> { scale.setSelection(toScale(upperLimit)); updateDisplay(); });
		
		Button setMax = new Button(settingSector, SWT.NONE);
		setMax.setText("Set to max");
		setMax.addListener(SWT.Selection, event -> { scale.setSelection(toScale(getMax())); updateDisplay(); });
		
		Composite middleSection = new Composite(settingSector, SWT.NONE);
		middleSection.setLayout(new GridLayout(3, false));
		
		Composite valuesSection = new Composite(middleSection, SWT.NONE);
		valuesSection.setBounds(0, 0, 64, 64);
		valuesSection.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		newScore = new Text(valuesSection, SWT.BORDER);
		newScore.setLayoutData(new RowData(25, SWT.DEFAULT));
		
		scale = new Scale(middleSection, SWT.VERTICAL);
		scale.setSize(52, 200);
		scale.setMaximum(toScale(1.0 - upperLimit));
		scale.setMinimum(toScale(1.0 - lowerLimit));
		scale.addListener(SWT.Selection, event -> { valueChanged.invoke(fromScale(scale.getSelection())); updateDisplay(); });
		scale.setSelection(toScale((upperLimit + lowerLimit) / 2.0));
		updateDisplay();
		
		Composite presetSection = new Composite(middleSection, SWT.NONE);
		presetSection.setBounds(0, 0, 64, 64);
		presetSection.setLayout(new RowLayout(SWT.VERTICAL));
		
		Button upThird = new Button(presetSection, SWT.NONE);
		upThird.setText("Set to 2/3 %");
		upThird.addListener(SWT.Selection, event -> { scale.setSelection(toScale(ratioOf(2.0/3.0))); updateDisplay(); });
		
		Button lowThird = new Button(presetSection, SWT.NONE);
		lowThird.setText("Set to 1/3 %");
		lowThird.addListener(SWT.Selection, event -> { scale.setSelection(toScale(ratioOf(1.0/3.0))); updateDisplay(); });
		
		Button setMin = new Button(settingSector, SWT.NONE);
		setMin.setText("Set to min");
		setMin.addListener(SWT.Selection, event -> { scale.setSelection(toScale(getMin())); updateDisplay(); });
		
		Button setLower = new Button(settingSector, SWT.NONE);
		setLower.setText("Set to 0.0");
		setLower.addListener(SWT.Selection, event -> { scale.setSelection(toScale(lowerLimit)); updateDisplay(); });
	}

	private void updateDisplay() {
		newScore.setText(FORMAT.format(fromScale(scale.getSelection())));
	}
	
	public void setTitle(String title) {
		this.title.setText(title);
	}
	
	private NonGenericListenerCollection<Double> valueChanged = new NonGenericListenerCollection<>();
	private Composite settingSector;
	
	public INonGenericListenerCollection<Double> eventValueChanged() {
		return valueChanged;
	}
}
