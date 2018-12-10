package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.view.ScoreLoaderView;

public class ScoreLoaderControl extends Control<ScoreListModel, ScoreLoaderView> {

	public ScoreLoaderControl(ScoreListModel model, ScoreLoaderView view) {
		super(model, view);
	}

	public void load() {
		getView().select();
	}
	
	private static final String UNIQUE_NAME_HEADER = "name";
	private static final String SCORE_HEADER = "tarantula";
	
	private IListener<String> fileSelectedListener = new IListener<String>() {
		@Override
		public void invoke(String event) {
			File file = new File(event); 
			try {
				CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT.withQuote('"').withDelimiter(';').withFirstRecordAsHeader());
				int updatedCount = 0;
				int recordCount = 0;
				for (CSVRecord record : parser) {
					recordCount++;
					String name = record.get(UNIQUE_NAME_HEADER);
					double score = Double.parseDouble(record.get(SCORE_HEADER));
					if (getModel().updateScore(name, score)) {
						updatedCount++;
					}
				}
				System.out.println(updatedCount + "/" + recordCount + " scores are loaded");
			} catch (IOException e) {
				e.printStackTrace();
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
