package org.eclipse.sed.ifl.ide.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class MonumentUI extends Composite {
	
	public MonumentUI(Composite parent, int style, Image icon, LocalDateTime creation) {
		super(parent, style);
		setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.center = true;
		setLayout(layout);
		
		Label iconLabel = new Label(this, SWT.CENTER);
		iconLabel.setAlignment(SWT.CENTER);
		iconLabel.setImage(icon);
		Label timeLabel = new Label(this, SWT.CENTER);
		timeLabel.setFont(SWTResourceManager.getFont("Segoe UI", 7, SWT.NORMAL));
		timeLabel.setAlignment(SWT.CENTER);
		timeLabel.setText(creation.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
	}
	
}
