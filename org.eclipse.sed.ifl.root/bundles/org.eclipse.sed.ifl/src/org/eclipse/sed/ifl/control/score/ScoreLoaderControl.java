package org.eclipse.sed.ifl.control.score;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sed.ifl.control.Control;
import org.eclipse.sed.ifl.model.score.ScoreListModel;
import org.eclipse.sed.ifl.util.event.IListener;
import org.eclipse.sed.ifl.util.profile.NanoWatch;
import org.eclipse.sed.ifl.view.ScoreLoaderView;
import org.eclipse.swt.SWT;

import org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class ScoreLoaderControl extends Control<ScoreListModel, ScoreLoaderView> {

	public ScoreLoaderControl(boolean interactivity) {
		this.interactivity = interactivity;
	}

	public void load() {
		getView().select();
	}
	
	private boolean interactivity;
	private static final String UNIQUE_NAME_HEADER = "name";
	private static final String SCORE_HEADER = "ochiai";
	private static final String INTERACTIVITY_HEADER = "interactive";
	private static final String DETAILS_LINK_HEADER = "details";
	private static final CSVFormat CSVFORMAT = CSVFormat.DEFAULT.withQuote('"').withDelimiter(';').withFirstRecordAsHeader(); 
	
	public class Entry {
		private String name;
		private long lineNumber;
		private double score;
		private String detailsLink;
		private boolean interactivity;
		
		public Entry(String name, long lineNumber, double score, String detailsLink, boolean interactivity) {
			super();
			this.name = name;
			this.lineNumber = lineNumber;
			this.score = score;
			this.detailsLink = detailsLink;
			this.interactivity = interactivity;
		}

		public String getName() {
			return name;
		}
		
		public long getLineNumber() {
			return lineNumber;
		}

		public double getScore() {
			return score;
		}

		public String getDetailsLink() {
			return detailsLink;
		}
		
		public boolean isInteractive() {
			return interactivity;
		}
	}
	
	private IListener<String> fileSelectedListener = new IListener<String>() {

		@Override
		public void invoke(String event) {
			NanoWatch watch = new NanoWatch("loading scores from csv");
			File file = new File(event); 
			try {
				CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFORMAT);
				int recordCount = 0;
				List<Entry> loadedScores = new ArrayList<>();
				for (CSVRecord record : parser) {
					recordCount++;
					String name = record.get(UNIQUE_NAME_HEADER);
					// kötőjel választja el a metódus infót a sor számától
					String[] splitNameAndLineNumber = name.split("-");
					String methodInfoName = splitNameAndLineNumber[0];
					long lineNumber = Long.parseLong(splitNameAndLineNumber[1]);
					double score = Double.parseDouble(record.get(SCORE_HEADER));
					if (score > 1 || score < 0) {
						MessageDialog.open(
							MessageDialog.ERROR, null,
							"Error during iFL score loading",
							"The value for '" + name + "' is invalid.\n"
							+ "All scores in the '" + SCORE_HEADER + "' column should not be less then 0 or greater then 1.",
							SWT.NONE);
						return;
					}
					// az interactivity és details link most nincs a csv-ben
					boolean interactivity = !(record.isSet(INTERACTIVITY_HEADER) && record.get(INTERACTIVITY_HEADER).equals("no"));
					Entry entry = new Entry(methodInfoName, lineNumber, score, record.isSet(DETAILS_LINK_HEADER)?record.get(DETAILS_LINK_HEADER):null, interactivity);
					loadedScores.add(entry);
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
		super.init();
	}
	
	@Override
	public void teardown() {
		getView().eventFileSelected().remove(fileSelectedListener);
		super.teardown();
	}
}
