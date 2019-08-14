package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.ui.Setter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridData;

public class ScoreSetter extends Composite {

	private Label title;
	private Label newScore;
	private Scale scale;
	
	private int toScale(int value) {
		return 100 - value;
	}
	
	private int fromScale(int value) {
		return 100 - value;
	}
	
	private double lowerLimit = -1.0;
	private double upperLimit = 1.0;
		
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
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
	
	private Map<Integer, Button> presets = new HashMap<>(); 
	
	public ScoreSetter() {
		this(new Shell());
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public ScoreSetter(Composite parent) {
		super(parent, SWT.NONE);
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
		
		newScore = new Label(mainSection, SWT.NONE);

		Button active = new Button(mainSection, SWT.CHECK);
		active.setText("active");
		active.addListener(SWT.Selection, event -> Setter.RecursiveEnable(settingSection, active.getSelection()));
		active.setSelection(true);
		
		settingSection = new Composite(mainSection, SWT.NONE);
		RowLayout rl_settingSection = new RowLayout(SWT.VERTICAL);
		rl_settingSection.center = true;
		rl_settingSection.justify = true;
		settingSection.setLayout(rl_settingSection);
		
		Button setUpper = new Button(settingSection, SWT.TOGGLE);
		setUpper.setText("Set to 1");
		setUpper.addListener(SWT.Selection, event -> {
			absoluteScoreSetted.invoke(upperLimit);
			if (!(setUpper.getSelection() || setLower.getSelection())) {
				absoluteScoreSettingDisabled.invoke(new EmptyEvent());
			}
			if (setUpper.getSelection()) {
				setLower.setSelection(false);
			}
		});
		
		middleSection = new Composite(settingSection, SWT.NONE);
		middleSection.setLayout(new GridLayout(3, false));
		
		scale = new Scale(middleSection, SWT.VERTICAL);
		GridData gd_scale = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_scale.heightHint = 250;
		gd_scale.widthHint = 50;
		scale.setLayoutData(gd_scale);
		scale.setMaximum(200);
		scale.setMinimum(0);
		scale.addListener(SWT.Selection, event -> {
			deltaPercentChanged.invoke(fromScale(scale.getSelection()));
		});

		distribution = new Composite(middleSection, SWT.NONE);
		distribution.setLayout(null);
		GridData gd_distribution = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_distribution.heightHint = 250;
		gd_distribution.widthHint = 20;
		distribution.setLayoutData(gd_distribution);
		distribution.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		
		Composite presetSection = new Composite(middleSection, SWT.NONE);
		presetSection.setLayout(new RowLayout(SWT.VERTICAL));
		
		presets.put(66, new Button(presetSection, SWT.RADIO));
		presets.put(50, new Button(presetSection, SWT.RADIO));
		presets.put(33, new Button(presetSection, SWT.RADIO));
		presets.put(0 , new Button(presetSection, SWT.RADIO));
		presets.put(-33, new Button(presetSection, SWT.RADIO));
		presets.put(-50, new Button(presetSection, SWT.RADIO));
		presets.put(-66, new Button(presetSection, SWT.RADIO));
		
		for (Entry<Integer, Button> entry : presets.entrySet()) {
			presets.get(entry.getKey()).setText(entry.getKey() + "%");
			presets.get(entry.getKey()).addListener(SWT.Selection, event -> {
				deltaPercentChanged.invoke(entry.getKey());
			});
		}
		
		setLower = new Button(settingSection, SWT.TOGGLE);
		setLower.setText("Set to 0.0");
		setLower.addListener(SWT.Selection, event -> {
			absoluteScoreSetted.invoke(lowerLimit);
			if (!(setUpper.getSelection() || setLower.getSelection())) {
				absoluteScoreSettingDisabled.invoke(new EmptyEvent());
			}
			if (setLower.getSelection()) {
				setUpper.setSelection(false);
			}
		});
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}
	
	private Composite settingSection;
	private Composite distribution;
	private Composite middleSection;
	private Button setLower;
	
	private NonGenericListenerCollection<Integer> deltaPercentChanged = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventDeltaPercentChanged() {
		return deltaPercentChanged;
	}

	private NonGenericListenerCollection<Double> absoluteScoreSetted = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Double> eventAbsoluteScoreSetted() {
		return absoluteScoreSetted;
	}
	
	private NonGenericListenerCollection<EmptyEvent> absoluteScoreSettingDisabled = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<EmptyEvent> eventAbsoluteScoreSettingDisabled() {
		return absoluteScoreSettingDisabled;
	}
	
	/**
	 * 100 is 100%, while 0 is 0% 
	 * @param delta
	 */
	public void displayDeltaPercent(Integer delta) {
		scale.setSelection(toScale(delta));
		newScore.setText("will change by " + delta.toString() + "%");
		newScore.requestLayout();
	}
	
	public void activatePreset(int presetValue) {
		for (Button item : presets.values()) {
			item.setSelection(false);
		}
		if (presets.containsKey(presetValue)) {
			presets.get(presetValue).setSelection(true);
		}
	}
	
	public void disableRelativeScoreSetting() {
		Setter.RecursiveEnable(middleSection, false);
	}

	public void enableRelativeScoreSetting() {
		Setter.RecursiveEnable(middleSection, true);
	}
	
}
