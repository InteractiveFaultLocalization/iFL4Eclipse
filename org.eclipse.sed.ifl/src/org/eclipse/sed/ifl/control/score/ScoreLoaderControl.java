package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.view.ScoreLoaderView;
import org.eclipse.swt.SWT;

public class ScoreLoaderControl extends Control<ScoreListModel, ScoreLoaderView> {

	public ScoreLoaderControl(ScoreListModel model, ScoreLoaderView view) {
		super(model, view);
	}

	public void load() {
		getView().select();
	}
	
	private IListener<String> fileSelectedListener = new IListener<String>() {
		@Override
		public void invoke(String event) {
			File file = new File(event); 
			try (Scanner scanner = new Scanner(file)) {
				while (scanner.hasNextLine()) { 
			    	System.out.println(scanner.nextLine());
			    }
			} catch (FileNotFoundException e) {
				MessageDialog.open(MessageDialog.ERROR, null, "iFL score file can not found", "The file " + event + " is not aviable.", SWT.NONE);
			} 
		}
	};

	@Override
	public void init() {
		getView().eventFileSelected().add(fileSelectedListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		getView().eventFileSelected().remove(fileSelectedListener);
		super.teardown();
	}
}
