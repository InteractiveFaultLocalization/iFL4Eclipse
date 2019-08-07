package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridData;

public class ContextBasedScoreSetter extends Composite {

	private Label title;
	private Text newScore;
	private Scale scale;
	
	private static final double SLIDER_PRECISION = 100.0;
	private static final DecimalFormat FORMAT = new DecimalFormat("#0%");
	
	private int toScale(double value) {
		return Double.valueOf((upperLimit - value) * SLIDER_PRECISION).intValue();
	}
	
	private double fromScale(int value) {
		return upperLimit - value / SLIDER_PRECISION;
	}
	
	private double lowerLimit = -1.0;
	private double upperLimit = 1.0;
	
	private double ratioOf(double ratio) {
		return lowerLimit * (1.0 - ratio) + upperLimit * ratio;
	}
	
	private List<Double> dataPoints;
	
	public void setDataPoints(List<Double> points) {
		dataPoints = new ArrayList<>(points);
		Random random = new Random();
		for (Double point : points) {
			Label label = new Label(distribution, SWT.NONE);
			label.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			label.setAlignment(SWT.CENTER);
			label.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
			label.setBounds(
				new Double(3 + 10 * random.nextDouble()).intValue(),
				new Double(250 - 250 * point).intValue(),
				4, 4);
			label.setText("");
		}
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
		
		Composite mainSection = new Composite(this, SWT.NONE);
		RowLayout rl_mainSection = new RowLayout(SWT.VERTICAL);
		rl_mainSection.center = true;
		rl_mainSection.justify = true;
		mainSection.setLayout(rl_mainSection);
		
		title = new Label(mainSection, SWT.NONE);
		title.setAlignment(SWT.CENTER);
		title.setText("(noname)");

		Button active = new Button(mainSection, SWT.CHECK);
		active.setText("active");
		active.addListener(SWT.Selection, event -> Setter.RecursiveEnable(settingSection, active.getSelection()));
		active.setSelection(true);
		
		settingSection = new Composite(mainSection, SWT.NONE);
		RowLayout rl_settingSection = new RowLayout(SWT.VERTICAL);
		rl_settingSection.center = true;
		rl_settingSection.justify = true;
		settingSection.setLayout(rl_settingSection);
		
		Button setUpper = new Button(settingSection, SWT.CENTER);
		setUpper.setText("Set to 1");
		setUpper.addListener(SWT.Selection, event -> { scale.setSelection(toScale(upperLimit)); updateDisplay(); });
		
		Button setMax = new Button(settingSection, SWT.NONE);
		setMax.setText("Set to max");
		setMax.addListener(SWT.Selection, event -> { scale.setSelection(toScale(getMax())); updateDisplay(); });
		
		Composite middleSection = new Composite(settingSection, SWT.NONE);
		middleSection.setLayout(new GridLayout(4, false));
		
		Composite valuesSection = new Composite(middleSection, SWT.NONE);
		valuesSection.setLayout(new RowLayout(SWT.HORIZONTAL));
		valuesSection.update();
		
		newScore = new Text(valuesSection, SWT.BORDER);
		newScore.setLayoutData(new RowData(25, SWT.DEFAULT));
		
		scale = new Scale(middleSection, SWT.VERTICAL);
		GridData gd_scale = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_scale.heightHint = 250;
		gd_scale.widthHint = 50;
		scale.setLayoutData(gd_scale);
		scale.setMaximum(200);
		scale.setMinimum(0);
		scale.addListener(SWT.Selection, event -> { valueChanged.invoke(fromScale(scale.getSelection())); updateDisplay(); });
		scale.setSelection(toScale((upperLimit + lowerLimit) / 2.0));
		updateDisplay();

		distribution = new Composite(middleSection, SWT.NONE);
		distribution.setLayout(null);
		GridData gd_distribution = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_distribution.heightHint = 250;
		gd_distribution.widthHint = 20;
		distribution.setLayoutData(gd_distribution);
		distribution.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		
		Composite presetSection = new Composite(middleSection, SWT.NONE);
		presetSection.setLayout(new RowLayout(SWT.VERTICAL));
		
		Button upFourth = new Button(presetSection, SWT.RADIO);
		upFourth.setText("Set to 3/4");
		upFourth.addListener(SWT.Selection, event -> { scale.setSelection(toScale(ratioOf(3.0/4.0))); updateDisplay(); });
		
		Button upThird = new Button(presetSection, SWT.RADIO);
		upThird.setText("Set to 2/3");
		upThird.addListener(SWT.Selection, event -> { scale.setSelection(toScale(ratioOf(2.0/3.0))); updateDisplay(); });
		
		Button middle = new Button(presetSection, SWT.RADIO);
		middle.setText("Set to 1/2");
		middle.addListener(SWT.Selection, event -> { scale.setSelection(toScale(ratioOf(1.0/2.0))); updateDisplay(); });
		
		Button lowThird = new Button(presetSection, SWT.RADIO);
		lowThird.setText("Set to 1/3");
		lowThird.addListener(SWT.Selection, event -> { scale.setSelection(toScale(ratioOf(1.0/3.0))); updateDisplay(); });

		Button lowFourth = new Button(presetSection, SWT.RADIO);
		lowFourth.setText("Set to 1/4");
		lowFourth.addListener(SWT.Selection, event -> { scale.setSelection(toScale(ratioOf(1.0/4.0))); updateDisplay(); });

		Button setMin = new Button(settingSection, SWT.NONE);
		setMin.setText("Set to min");
		setMin.addListener(SWT.Selection, event -> { scale.setSelection(toScale(getMin())); updateDisplay(); });
		
		Button setLower = new Button(settingSection, SWT.NONE);
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
	private Composite settingSection;
	private Composite distribution;
	
	public INonGenericListenerCollection<Double> eventValueChanged() {
		return valueChanged;
	}
}
