package org.eclipse.sed.ifl.scorelib;

import java.io.File;

import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;


public class Testmain {
	public static void main(String[] args) {
		TrcReader r = new TrcReader();
		File trc = new File("C:\\Users\\Bence\\Downloads\\coverage\\org.joda.time.chrono.gj.MainTest.testChronology-PASS.1.trc");
		r.readTrc(trc, null);
	}
}
