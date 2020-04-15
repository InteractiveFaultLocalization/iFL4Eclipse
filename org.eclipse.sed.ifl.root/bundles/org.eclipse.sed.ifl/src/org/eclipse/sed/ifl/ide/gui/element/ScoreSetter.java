package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.eclipse.sed.ifl.model.source.IMethodDescription;
import org.eclipse.sed.ifl.util.event.INonGenericListenerCollection;
import org.eclipse.sed.ifl.util.event.core.EmptyEvent;
import org.eclipse.sed.ifl.util.event.core.NonGenericListenerCollection;
import org.eclipse.sed.ifl.util.ui.Setter;
import org.eclipse.sed.ifl.util.wrapper.Defineable;
import org.eclipse.sed.ifl.util.wrapper.Projection;
import org.eclipse.sed.ifl.util.wrapper.Relativeable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridData;

public class ScoreSetter extends Composite {

	private Label title;
	private Label newScore;
	private Scale scale;
	private Button active;
	private Table table;
	private TableColumn nameColumn;
	
	private int toScale(int value) {
		return 100 - value;
	}
	
	private int fromScale(int value) {
		return 100 - value;
	}
	
	public void refreshView() {
		active.setSelection(true);
		Setter.RecursiveEnable(settingSection, active.getSelection());
		setUpper.setSelection(false);
		setLower.setSelection(false);
		scale.setSelection(100);
		newScore.setText("will change by 0%");
		for (Button item : presets.values()) {
			item.setSelection(false);
		}
		collectRelativeableValue.invoke(createRelativeableValue());
	}
	
	private double lowerLimit = -1.0;
	private double upperLimit = 1.0;
		
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	private List<Double> jitter = new ArrayList<>();
	
	public void displayCurrentScoreDistribution(Map<IMethodDescription, Projection<Double>> subjects) {
		for (Control control : distribution.getChildren()) {
			control.dispose();
		}
		
		ArrayList<Projection<Double>> points = new ArrayList<Projection<Double>>(subjects.values());
		
		random = new Random();
		while (jitter.size() < points.size()) {
			//System.out.println("new jitt generated");
			jitter.add(random.nextDouble());
		}
		
		
		
		Iterator<Double> index = jitter.iterator();
		for (Entry<IMethodDescription, Projection<Double>> entry : subjects.entrySet()) {
			double x = index.next();
			createGlyph(SWT.COLOR_BLACK, x, entry.getValue().getOriginal(), entry.getKey());
		}
		index = jitter.iterator();
		for (Entry<IMethodDescription, Projection<Double>> entry : subjects.entrySet()) {
			double x = index.next();
			if (entry.getValue().getProjected().isDefinit()) {
				createGlyph(SWT.COLOR_RED, x, entry.getValue().getProjected().getValue(), entry.getKey());
			}
		}
	}

	private void createGlyph(int color, double x, double y, IMethodDescription data) {
		Label glyph = new Label(distribution, SWT.NONE);
		glyph.setBackground(SWTResourceManager.getColor(color));
		glyph.setBounds(
			new Double((displayWidth - glyphSize) * x).intValue(),
			new Double(displayHeight - glyphSize / 2 - displayHeight * y).intValue(),
			glyphSize, glyphSize);
		glyph.setText("");
		glyph.setData("method", data);
		glyph.setData("color", color);
		glyph.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent e) {
				glyph.setBackground(SWTResourceManager.getColor(color));
				for (TableItem item : table.getItems()) {
					if(item.getData().toString().equals(glyph.getData("method").toString())){
						table.setSelection(item);
					}
				}
				for (Control control : distribution.getChildren()) {
					if(!control.getData("method").toString().equals(glyph.getData("method").toString())) {
						control.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
					} else {
						control.setBackground(SWTResourceManager.getColor(Integer.parseInt(control.getData("color").toString())));
					}
				}
			}

			@Override
			public void mouseExit(MouseEvent e) {
				table.deselectAll();
				for (Control control: distribution.getChildren()) {
					control.setBackground(SWTResourceManager.getColor(Integer.parseInt(control.getData("color").toString())));
				}
			}

			@Override
			public void mouseHover(MouseEvent e) {				
			}
			
		});
	}
	
	private Map<Integer, Button> presets = new HashMap<>();
	private Composite ruler; 
	
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

		//TODO move action logic to Control
		active = new Button(mainSection, SWT.CHECK);
		active.setText("active");
		active.addListener(SWT.Selection, event -> {	
			Setter.RecursiveEnable(settingSection, active.getSelection());
			collectRelativeableValue.invoke(createRelativeableValue());
		});
		active.setSelection(true);
		
		settingSection = new Composite(mainSection, SWT.NONE);
		RowLayout rl_settingSection = new RowLayout(SWT.VERTICAL);
		rl_settingSection.center = true;
		rl_settingSection.justify = true;
		settingSection.setLayout(rl_settingSection);
		
		setUpper = new Button(settingSection, SWT.TOGGLE);
		setUpper.setText("Set to 1");
		setUpper.addListener(SWT.Selection, event -> {
			absoluteScoreSetted.invoke(upperLimit);
			collectRelativeableValue.invoke(createRelativeableValue());
			if (!(setUpper.getSelection() || setLower.getSelection())) {
				absoluteScoreSettingDisabled.invoke(new EmptyEvent());
			}
			if (setUpper.getSelection()) {
				setLower.setSelection(false);
			}
		});
		
		middleSection = new Composite(settingSection, SWT.NONE);
		middleSection.setLayout(new GridLayout(4, false));
		
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
				collectRelativeableValue.invoke(createRelativeableValue());
			});
		}
		
		Composite scaleSection = new Composite(middleSection, SWT.NONE);
		RowLayout rl_scaleSection = new RowLayout(SWT.VERTICAL);
		rl_scaleSection.center = true;
		scaleSection.setLayout(rl_scaleSection);
		
		Label maxRelativePercentDisplayer = new Label(scaleSection, SWT.NONE);
		maxRelativePercentDisplayer.setAlignment(SWT.CENTER);
		maxRelativePercentDisplayer.setText("+100%");
		
		scale = new Scale(scaleSection, SWT.VERTICAL);
		scale.setMaximum(200);
		scale.setMinimum(0);
		scale.addListener(SWT.Selection, event -> {
			deltaPercentChanged.invoke(fromScale(scale.getSelection()));
			collectRelativeableValue.invoke(createRelativeableValue());
		});

		Label minRelativePercentDisplayer = new Label(scaleSection, SWT.NONE);
		minRelativePercentDisplayer.setAlignment(SWT.CENTER);
		minRelativePercentDisplayer.setText("-100%");

		displayHeight = 250;
		displayWidth = 50;
		rulerWidth = 20;
		glyphSize = 4;

		ruler = new Composite(middleSection, SWT.NONE);
		ruler.setLayout(null);
		GridData gd_ruler = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_ruler.heightHint = displayHeight;
		gd_ruler.widthHint = rulerWidth;
		ruler.setLayoutData(gd_ruler);
		
		int markCount = 10;
		for (int y = 0; y < markCount; y += 1) {
			Label label = new Label(ruler, SWT.NONE);
			Double mark = (1.0 / markCount) * y;
			label.setText(new DecimalFormat("#0.0").format(1.0 - mark));
			label.setBounds(0, new Double(displayHeight * mark).intValue(), rulerWidth, 15);
		}

		distribution = new Composite(middleSection, SWT.NONE);
		distribution.setLayout(null);
		GridData gd_distribution = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_distribution.heightHint = displayHeight;
		gd_distribution.widthHint = displayWidth;
		distribution.setLayoutData(gd_distribution);
		distribution.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));

		setLower = new Button(settingSection, SWT.TOGGLE);
		setLower.setText("Set to 0.0");
		setLower.addListener(SWT.Selection, event -> {
			absoluteScoreSetted.invoke(lowerLimit);
			collectRelativeableValue.invoke(createRelativeableValue());
			if (!(setUpper.getSelection() || setLower.getSelection())) {
				absoluteScoreSettingDisabled.invoke(new EmptyEvent());
			}
			if (setLower.getSelection()) {
				setUpper.setSelection(false);
			}
		});
		
		tableSection = new Composite(mainSection, SWT.NONE);
		tableSection.setLayout(new GridLayout());
		
		table = new Table(tableSection, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		
		GridData gd_table = new GridData(SWT.FILL);
		gd_table.grabExcessVerticalSpace = true;
		gd_table.grabExcessHorizontalSpace = true;
		gd_table.verticalAlignment = SWT.FILL;
		gd_table.horizontalAlignment = SWT.FILL;
		gd_table.heightHint = 100;
		gd_table.widthHint = 250;
		table.setLayoutData(gd_table);
		
		table.setLinesVisible(true);
		table.setHeaderVisible(false);
		table.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				table.deselectAll();
				for (Control control: distribution.getChildren()) {
					control.setBackground(SWTResourceManager.getColor(Integer.parseInt(control.getData("color").toString())));
				}
			}

		});
		table.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selected = table.getSelection();
				for (Control control : distribution.getChildren()) {
					assert selected[0].getData() instanceof IMethodDescription;
					IMethodDescription desc = (IMethodDescription)(selected[0].getData());
					if(!desc.toString().equals(control.getData("method").toString())) {
						control.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
					} else {
						control.setBackground(SWTResourceManager.getColor(Integer.parseInt(control.getData("color").toString())));
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		table.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent e) {	
			}

			@Override
			public void mouseExit(MouseEvent e) {
				table.deselectAll();
				for (Control control: distribution.getChildren()) {
					control.setBackground(SWTResourceManager.getColor(Integer.parseInt(control.getData("color").toString())));
				}
			}

			@Override
			public void mouseHover(MouseEvent e) {
				
			}
			
		});
	}

	public void setTableContents(Map<IMethodDescription, Projection<Double>> subjects) {
		for (TableItem item : table.getItems()) {
			item.dispose();
		}
		
		for (IMethodDescription method : subjects.keySet()) {
			
				TableItem item = new TableItem(table, SWT.NULL);
				item.setText(method.getId().getSignature());
				item.setData(method);
			
		}
		
		
	}

	public void setColumnTitle(String title) {
		this.nameColumn.setText(title);
	}
	
	public void setTitle(String title) {
		this.title.setText(title);
	}
	
	private Composite settingSection;
	private Composite distribution;
	private Composite middleSection;
	private Composite tableSection;
	private Button setLower;
	private Button setUpper;
	
	private NonGenericListenerCollection<Integer> deltaPercentChanged = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Integer> eventDeltaPercentChanged() {
		return deltaPercentChanged;
	}

	private NonGenericListenerCollection<Double> absoluteScoreSetted = new NonGenericListenerCollection<>();

	public INonGenericListenerCollection<Double> eventAbsoluteScoreSetted() {
		return absoluteScoreSetted;
	}
	
	private NonGenericListenerCollection<EmptyEvent> absoluteScoreSettingDisabled = new NonGenericListenerCollection<>();
	private int displayHeight;
	private int displayWidth;
	private int rulerWidth;
	private int glyphSize;
	private Random random;

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
	
	
	private Relativeable<Defineable<Double>> createRelativeableValue() {
		boolean isActive = active.getSelection();
		boolean isRelative = !(setUpper.getSelection() || setLower.getSelection());
		
		Defineable<Double> value;
		
		if(isActive) {
			if(setUpper.getSelection()) {
				value = new Defineable<Double>(1.0);
			} else if(setLower.getSelection()) {
				value = new Defineable<Double>(0.0);
			} else {
			double scaleValue = fromScale(scale.getSelection());
			value = new Defineable<Double>(scaleValue);
			}
		} else {
			value = new Defineable<Double>();
		}
		return new Relativeable<Defineable<Double>>(isRelative, value, title.getText());
	}
	
	private NonGenericListenerCollection<Relativeable<Defineable<Double>>> collectRelativeableValue = new NonGenericListenerCollection<>();
	
	public INonGenericListenerCollection<Relativeable<Defineable<Double>>> eventCollectRelativeableValue() {
		return collectRelativeableValue;
	}
	
	public void invokeRelativeableCollection() {
		collectRelativeableValue.invoke(createRelativeableValue());
	}
}
