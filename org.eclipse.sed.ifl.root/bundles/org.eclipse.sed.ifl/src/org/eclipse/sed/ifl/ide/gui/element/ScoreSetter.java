package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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
import org.eclipse.swt.graphics.Rectangle;
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

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.ResourceManager;

public class ScoreSetter extends Composite {

	private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	private static final DecimalFormat LIMIT_FORMAT = new DecimalFormat("#0.0000", symbols);
	
	private Label selectionImage;
	private Label title;
	private Label newScore;
	private Scale scale;
	private Button active;
	private Table table;
	private TableColumn nameColumn;
	private TableColumn currentScoreColumn;
	private TableColumn updatedScoreColumn;
	
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
		refreshUpdatedScoresColumn(createRelativeableValue());
		setUpper.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_1_inactive_v2.png"));
		setLower.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_0_inactive_v2.png"));
	}
	
	private double lowerLimit = -1.0;
	private double upperLimit = 1.0;
		
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	private List<Integer> jitter = new ArrayList<>();
	
	public void displayCurrentScoreDistribution(Map<IMethodDescription, Projection<Double>> subjects) {
		for (Control control : distribution.getChildren()) {
			control.dispose();
		}
		
		ArrayList<Projection<Double>> points = new ArrayList<Projection<Double>>(subjects.values());
		
		int spacing = 1;
		while (jitter.size() < points.size()) {
			jitter.add(spacing);
			spacing += glyphSize+1;
		}
		
		
		
		Iterator<Integer> index = jitter.iterator();
		
		

		//ArrayList<Entry<IMethodDescription, Projection<Double>>> listToShuffle = new ArrayList<Entry<IMethodDescription, Projection<Double>>>(subjects.entrySet());
		//Collections.shuffle(listToShuffle);
		int counter = 0;
		for (Entry<IMethodDescription, Projection<Double>> entry : subjects.entrySet()) {
			if(counter >= displayWidth / (glyphSize+1)) {
				break;
			}
				int x = index.next();
				createGlyph(SWT.COLOR_BLACK, x, entry.getValue().getOriginal(), entry.getKey());
				counter++;
		}
		counter = 0;
		index = jitter.iterator();
		for (Entry<IMethodDescription, Projection<Double>> entry : subjects.entrySet()) {
			if(counter >= displayWidth / (glyphSize+1)) {
				break;
			}
			int x = index.next();
			if (entry.getValue().getProjected().isDefinit()) {
				createGlyph(SWT.COLOR_RED, x, entry.getValue().getProjected().getValue(), entry.getKey());
				counter++;
			}
		}
		
	}
	
	private void createGlyph(int color, int x, double y, IMethodDescription data) {
		Label glyph = new Label(distribution, SWT.NONE);
		glyph.setBackground(SWTResourceManager.getColor(color));
		glyph.setBounds(
			x,
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
		
		settingSection = new Composite(mainSection, SWT.NONE);
		RowLayout rl_settingSection = new RowLayout(SWT.VERTICAL);
		rl_settingSection.center = true;
		rl_settingSection.justify = true;
		settingSection.setLayout(rl_settingSection);
		
		middleSection = new Composite(settingSection, SWT.NONE);
		middleSection.setLayout(new GridLayout(4, false));
		new Label(middleSection, SWT.NONE);
		new Label(middleSection, SWT.NONE);
		new Label(middleSection, SWT.NONE);
		
		selectionImage = new Label(middleSection, SWT.NONE);
		selectionImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		new Label(middleSection, SWT.NONE);
		new Label(middleSection, SWT.NONE);
		new Label(middleSection, SWT.NONE);
		
		title = new Label(middleSection, SWT.NONE);
		title.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		title.setText("(no title)");
		title.setImage(null);
		title.setAlignment(SWT.CENTER);
		new Label(middleSection, SWT.NONE);
		new Label(middleSection, SWT.NONE);
		new Label(middleSection, SWT.NONE);
		
				//TODO move action logic to Control
				active = new Button(middleSection, SWT.CHECK);
				active.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
				active.setImage(null);
				active.setText("active");
				active.addListener(SWT.Selection, event -> {
					if(!active.getSelection()) {
						setUpper.setSelection(active.getSelection());
						setLower.setSelection(active.getSelection());
						setUpper.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_1_inactive_v2.png"));
						setLower.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_0_inactive_v2.png"));
					}
					setActivity(active.getSelection());
					collectRelativeableValue.invoke(createRelativeableValue());
					refreshUpdatedScoresColumn(createRelativeableValue());
				});
				active.setSelection(true);
		
		presetSection = new Composite(middleSection, SWT.NONE);
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
				refreshUpdatedScoresColumn(createRelativeableValue());
			});
		}
		
		scaleSection = new Composite(middleSection, SWT.NONE);
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
			refreshUpdatedScoresColumn(createRelativeableValue());
		});

		Label minRelativePercentDisplayer = new Label(scaleSection, SWT.NONE);
		minRelativePercentDisplayer.setAlignment(SWT.CENTER);
		minRelativePercentDisplayer.setText("-100%");

		displayHeight = 400;
		displayWidth = 240;
		rulerWidth = 20;
		glyphSize = 6;

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
						new Label(middleSection, SWT.NONE);
						new Label(middleSection, SWT.NONE);
						new Label(middleSection, SWT.NONE);
						
						absoluteScoreButtonSection = new Composite(middleSection, SWT.NONE);
						absoluteScoreButtonSection.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
						absoluteScoreButtonSection.setLayout(new GridLayout(3, false));
								
										setLower = new Button(absoluteScoreButtonSection, SWT.TOGGLE);
										setLower.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_0_inactive_v2.png"));
										
										composite = new Composite(absoluteScoreButtonSection, SWT.NONE);
										composite.setLayout(new GridLayout(1, false));
										
										newScore = new Label(composite, SWT.NONE);
										newScore.setSize(0, 15);
										
										setUpper = new Button(absoluteScoreButtonSection, SWT.TOGGLE);
										setUpper.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_1_inactive_v2.png"));
										
										setUpper.addListener(SWT.Selection, event -> {
											if (setUpper.getSelection()) {
												setLower.setSelection(false);
												setUpper.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_1_v2.png"));
												setLower.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_0_inactive_v2.png"));
											} else {
												setUpper.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_1_inactive_v2.png"));
											}
											absoluteScoreSetted.invoke(upperLimit);
											collectRelativeableValue.invoke(createRelativeableValue());
											refreshUpdatedScoresColumn(createRelativeableValue());
											if (!(setUpper.getSelection() || setLower.getSelection())) {
												absoluteScoreSettingDisabled.invoke(new EmptyEvent());
											}
										});
										
										setLower.addListener(SWT.Selection, event -> {
											if (setLower.getSelection()) {
												setUpper.setSelection(false);
												setUpper.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_1_inactive_v2.png"));
												setLower.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_0_v2.png"));
											} else {
												setLower.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/set_to_0_inactive_v2.png"));
											}
											absoluteScoreSetted.invoke(lowerLimit);
											collectRelativeableValue.invoke(createRelativeableValue());
											refreshUpdatedScoresColumn(createRelativeableValue());
											if (!(setUpper.getSelection() || setLower.getSelection())) {
												absoluteScoreSettingDisabled.invoke(new EmptyEvent());
											}
										});
		
		/*
		warningLabel = new Label(mainSection, SWT.NONE);
		warningLabel.setText("Warning: the above visualization does not represent all scores.\n To see all scores and changes, use the table below.");
		*/
		
		tableSection = new Composite(mainSection, SWT.NONE);
		tableSection.setLayout(new GridLayout());
		
		table = new Table(tableSection, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		
		GridData gd_table = new GridData(SWT.FILL);
		gd_table.widthHint = 387;
		gd_table.grabExcessHorizontalSpace = true;
		gd_table.grabExcessVerticalSpace = true;
		gd_table.verticalAlignment = SWT.FILL;
		gd_table.horizontalAlignment = SWT.FILL;
		gd_table.heightHint = 100;
		table.setLayoutData(gd_table);
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		
		currentScoreColumn = new TableColumn(table, SWT.LEFT);
		currentScoreColumn.setText("Current score");
		
		updatedScoreColumn = new TableColumn(table, SWT.LEFT);
		updatedScoreColumn.setText("Modified score");
				
		
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
		
		LIMIT_FORMAT.setRoundingMode(RoundingMode.DOWN);
		
		Rectangle rect = table.getClientArea();
		int columnWidth = rect.width/3;
		nameColumn.setWidth(columnWidth);
		currentScoreColumn.setWidth(columnWidth);
		updatedScoreColumn.setWidth(columnWidth);
		
		for (Entry<IMethodDescription, Projection<Double>> entry : subjects.entrySet()) {
			
				TableItem item = new TableItem(table, SWT.NULL);
				item.setText(table.indexOf(nameColumn), entry.getKey().getId().getSignature());
				item.setText(table.indexOf(currentScoreColumn),LIMIT_FORMAT.format(entry.getValue().getOriginal()));
				item.setData(entry.getKey());
			
		}
		
		
	}

	public void setColumnTitle(String title) {
		this.nameColumn.setText(title);
	}
	
	public void setTitle(String title) {
		this.title.setText(title);
		switch(title) {
		case "selected": selectionImage.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/selected_icon_32x32.png"));
			break;
		case "context": selectionImage.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/context_icon_32x32.png"));
			break;
		case "other": selectionImage.setImage(ResourceManager.getPluginImage("org.eclipse.sed.ifl", "icons/other_icon_32x32.png"));
			break;
		}
	}
	
	private Composite settingSection;
	private Composite distribution;
	private Composite middleSection;
	private Composite tableSection;
	private Composite presetSection;
	private Composite scaleSection;
	private Composite absoluteScoreButtonSection;
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
		setEnableRelativeSettings(false);
	}

	public void enableRelativeScoreSetting() {
		setEnableRelativeSettings(true);
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
		System.out.println(middleSection.getSize().x);
		return new Relativeable<Defineable<Double>>(isRelative, value, title.getText());
	}
	
	private NonGenericListenerCollection<Relativeable<Defineable<Double>>> collectRelativeableValue = new NonGenericListenerCollection<>();
	private Composite composite;
	
	public INonGenericListenerCollection<Relativeable<Defineable<Double>>> eventCollectRelativeableValue() {
		return collectRelativeableValue;
	}
	
	public void invokeRelativeableCollection() {
		collectRelativeableValue.invoke(createRelativeableValue());
	}
	
	private void refreshUpdatedScoresColumn(Relativeable<Defineable<Double>> value) {
		for (TableItem item : table.getItems()) {
			if(value.getValue().isDefinit()) {
				double currentScore = Double.parseDouble(item.getText(table.indexOf(currentScoreColumn)));
				double newScore = value.isRelative() ? currentScore + (currentScore * (value.getValue().getValue()/100)) : value.getValue().getValue();
				if(newScore > 1.0) {
					newScore = 1.0;
				}
				if(newScore < 0.0) {
					newScore = 0.0;
				}
				item.setText(table.indexOf(updatedScoreColumn), LIMIT_FORMAT.format(newScore));
			}
		}
	}
	
	private void setEnableRelativeSettings(Boolean enabled) {
		Setter.RecursiveEnable(presetSection, enabled);
		Setter.RecursiveEnable(scaleSection, enabled);
		Setter.RecursiveEnable(ruler, enabled);
		Setter.RecursiveEnable(distribution, enabled);
	}
	
	private void setActivity(Boolean enabled) {
		Setter.RecursiveEnable(presetSection, enabled);
		Setter.RecursiveEnable(scaleSection, enabled);
		Setter.RecursiveEnable(ruler, enabled);
		Setter.RecursiveEnable(distribution, enabled);
		Setter.RecursiveEnable(absoluteScoreButtonSection, enabled);
	}
}
