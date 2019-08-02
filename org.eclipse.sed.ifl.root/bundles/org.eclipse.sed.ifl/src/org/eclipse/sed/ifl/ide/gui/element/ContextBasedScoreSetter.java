package org.eclipse.sed.ifl.ide.gui.element;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
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

	public ContextBasedScoreSetter(Composite parent, int style) {
		super(parent, style);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.center = true;
		rowLayout.justify = true;
		rowLayout.fill = true;
		setLayout(rowLayout);
		
		Composite composite = new Composite(this, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.VERTICAL);
		rl_composite.center = true;
		rl_composite.justify = true;
		rl_composite.fill = true;
		composite.setLayout(rl_composite);
		
		title = new Label(composite, SWT.NONE);
		title.setAlignment(SWT.CENTER);
		title.setText("(noname)");
		
		Button setUpper = new Button(composite, SWT.CENTER);
		setUpper.setText("1");
		
				Button setMax = new Button(composite, SWT.NONE);
				setMax.setText("max");
				
				Composite middleSection = new Composite(composite, SWT.NONE);
				middleSection.setLayout(new GridLayout(3, false));
				
				Composite valuesSection = new Composite(middleSection, SWT.NONE);
				valuesSection.setBounds(0, 0, 64, 64);
				valuesSection.setLayout(new RowLayout(SWT.HORIZONTAL));
				
				newScore = new Text(valuesSection, SWT.BORDER);
				newScore.setLayoutData(new RowData(25, SWT.DEFAULT));
				
				Scale scale = new Scale(middleSection, SWT.VERTICAL);
				scale.setSize(52, 200);
				scale.setSelection(50);
				
				Composite presetSection = new Composite(middleSection, SWT.NONE);
				presetSection.setBounds(0, 0, 64, 64);
				presetSection.setLayout(new RowLayout(SWT.VERTICAL));
				
				Button upThird = new Button(presetSection, SWT.NONE);
				upThird.setText("2/3 %");
				
				Button active = new Button(presetSection, SWT.CHECK);
				active.setText("active");
				
				Button lowThird = new Button(presetSection, SWT.NONE);
				lowThird.setText("1/3 %");
				
				Button setMin = new Button(composite, SWT.NONE);
				setMin.setText("min");
				
				Button setLower = new Button(composite, SWT.NONE);
				setLower.setText("0");
	}
	
	public void setTitle(String title) {
		this.title.setText(title);
	}
}
