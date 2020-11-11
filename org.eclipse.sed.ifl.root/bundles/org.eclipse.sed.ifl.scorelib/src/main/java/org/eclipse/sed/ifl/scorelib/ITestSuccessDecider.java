package org.eclipse.sed.ifl.scorelib;

import java.io.File;

import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public interface ITestSuccessDecider {
	public Boolean decide(File folder, IMethodDescription[] methods);
}
