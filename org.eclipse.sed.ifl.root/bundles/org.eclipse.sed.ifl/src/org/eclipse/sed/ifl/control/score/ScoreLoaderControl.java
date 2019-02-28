package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
import org.eclipse.sed.ifl.view.ScoreLoaderView;

public class ScoreLoaderControl extends Control<ScoreListModel, ScoreLoaderView> {

	public ScoreLoaderControl(ScoreListModel model, ScoreLoaderView view) {
		super(model, view);
	}

	public void load() {
		getView().select();
	}
	
	private static final String UNIQUE_NAME_HEADER = "name";
	private static final String SCORE_HEADER = "score";
	private static final String INTERACTIVITY_HEADER = "interactive";
	private static final CSVFormat CSVFORMAT = CSVFormat.DEFAULT.withQuote('"').withDelimiter(';').withFirstRecordAsHeader(); 
	
	private IListener<String> fileSelectedListener = new IListener<String>() {

		@Override
		public void invoke(String event) {
			NanoWatch watch = new NanoWatch("loading scores from csv");
			File file = new File(event); 
			try {
				CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFORMAT);
				int recordCount = 0;
				Map<String, Score> loadedScores = new HashMap<>(); 
				for (CSVRecord record : parser) {
					recordCount++;
					String name = record.get(UNIQUE_NAME_HEADER);
					double value = Double.parseDouble(record.get(SCORE_HEADER));
					boolean interactivity = !(record.isSet(INTERACTIVITY_HEADER) && record.get(INTERACTIVITY_HEADER).equals("no"));
					Score score = new Score(value, interactivity);
					loadedScores.put(name, score);
				}
				int updatedCount = getModel().loadScore(loadedScores);
				System.out.println(updatedCount + "/" + recordCount + " scores are loaded");
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(watch);
		}
	};
	
	public static void saveSample(Map<String, Score> scores, File dump) {
		try (CSVPrinter printer = new CSVPrinter(new FileWriter(dump), CSVFORMAT)) {
			printer.printRecord(UNIQUE_NAME_HEADER, SCORE_HEADER, INTERACTIVITY_HEADER);
			
			for (Entry<String, Score> entry : scores.entrySet()) {
				printer.printRecord(entry.getKey(), entry.getValue().getValue(), entry.getValue().isInteractive()?"yes":"no");
			}
			printer.flush();
			System.out.println("Sample CSV was saved to " + dump.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
