package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.ElementDeserializer;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
import org.eclipse.sed.ifl.view.ScoreLoaderView;
import org.eclipse.sed.ifl.util.ScoreLoaderEntry;
import org.eclipse.swt.SWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class ScoreLoaderControl extends Control<ScoreListModel, ScoreLoaderView> {

	public ScoreLoaderControl() {
	}

	public void load() {
		getView().select();
	}
	
	public void loadFromJson() {
		getView().selectJson();	
	}

	private static final String UNIQUE_NAME_HEADER = "name";
	private static final String SCORE_HEADER = "score";
	private static final String INTERACTIVITY_HEADER = "interactive";
	private static final String DETAILS_LINK_HEADER = "details";
	private static final CSVFormat CSVFORMAT = CSVFormat.DEFAULT.withQuote('"').withDelimiter(';').withFirstRecordAsHeader(); 
	
	private IListener<String> fileSelectedListener = new IListener<String>() {

		@Override
		public void invoke(String event) {
			NanoWatch watch = new NanoWatch("loading scores from csv");
			File file = new File(event); 
			try {
				CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFORMAT);
				int recordCount = 0;
				Map<ScoreLoaderEntry, Score> loadedScores = new HashMap<>(); 
				for (CSVRecord record : parser) {
					recordCount++;
					String name = record.get(UNIQUE_NAME_HEADER);
					double value = Double.parseDouble(record.get(SCORE_HEADER));
					if (value > 1 || value < 0) {
						MessageDialog.open(
							MessageDialog.ERROR, null,
							"Error during iFL score loading",
							"The value for '" + name + "' is invalid.\n"
							+ "All scores in the '" + SCORE_HEADER + "' column should not be less then 0 or greater then 1.",
							SWT.NONE);
						return;
					}
					
					boolean interactivity = !(record.isSet(INTERACTIVITY_HEADER) && record.get(INTERACTIVITY_HEADER).equals("no"));
					ScoreLoaderEntry entry = new ScoreLoaderEntry(name, record.isSet(DETAILS_LINK_HEADER)?record.get(DETAILS_LINK_HEADER):null, interactivity);
					Score score = new Score(value);
					loadedScores.put(entry, score);
				}
				int updatedCount = getModel().loadScore(loadedScores);
				System.out.println(updatedCount + "/" + recordCount + " scores are loaded");
				MessageDialog.open(
					MessageDialog.INFORMATION, null,
					"iFL score loading",
					updatedCount + " scores are loaded from the " + recordCount + " records of " + event,
					SWT.NONE);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				MessageDialog.open(
					MessageDialog.ERROR, null,
					"Error during iFL score loading",
					"The score value for some elements are invalid.\n"
					+ "All scores in the '" + SCORE_HEADER + "' column should be numbers between 0 and 1.",
				SWT.NONE);
			} catch (Exception e) {
				MessageDialog.open(MessageDialog.ERROR, null, "Error during iFL score loading", "The plug-in was unable to open the CSV file. Please check if the CSV file is corrupted or is not properly formatted.", SWT.NONE);
				e.printStackTrace();
			}
			System.out.println(watch);
		}
	};
	
	private IListener<String> jsonFileSelectedListener = path -> {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Map.class, new ElementDeserializer());
		mapper.registerModule(module);
		
		try {
			String jsonString = new String(Files.readAllBytes(Paths.get(path)));
			@SuppressWarnings("unchecked")
			Map<ScoreLoaderEntry, Score> entries = mapper.readValue(jsonString, Map.class);
			int updatedCount = getModel().loadScore(entries);
			MessageDialog.open(
					MessageDialog.INFORMATION, null,
					"iFL score loading",
					updatedCount + " scores are loaded from the file " + path,
					SWT.NONE);
		} catch (IOException e1) {
			MessageDialog.open(MessageDialog.ERROR, null, "Error during iFL score loading", "The plug-in was unable to open the JSON file. Please check if the JSON file is corrupted or is not properly formatted.", SWT.NONE);
			e1.printStackTrace();
		}
		
	};
	
	public static void saveSample(Map<IMethodDescription, Score> scores, File dump) {
		try (CSVPrinter printer = new CSVPrinter(new FileWriter(dump), CSVFORMAT)) {
			printer.printRecord(UNIQUE_NAME_HEADER, SCORE_HEADER, INTERACTIVITY_HEADER, DETAILS_LINK_HEADER);
			
			for (Map.Entry<IMethodDescription, Score> entry : scores.entrySet()) {
				printer.printRecord(
					entry.getKey().getId().getSignature(),
					entry.getValue().getValue(),
					entry.getKey().isInteractive()?"yes":"no",
					"https://www.google.hu/search?q=" + entry.getKey().getId().getSignature());
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
		getView().eventJsonFileSelected().add(jsonFileSelectedListener);
		super.init();
	}
	
	@Override
	public void teardown() {
		getView().eventFileSelected().remove(fileSelectedListener);
		getView().eventJsonFileSelected().remove(jsonFileSelectedListener);
		super.teardown();
	}
}
