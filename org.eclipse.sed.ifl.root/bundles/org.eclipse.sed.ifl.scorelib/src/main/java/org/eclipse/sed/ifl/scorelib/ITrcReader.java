package org.eclipse.sed.ifl.scorelib;

import java.io.File;
import java.util.Map;

import main.java.hu.szte.sed.coverage.BinaryMethodCoverageData;
import main.java.hu.szte.sed.coverage.ChainCoverageData;
import main.java.hu.szte.sed.coverage.CoverageData;
import main.java.hu.szte.sed.coverage.NumericMethodCoverageData;
import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public interface ITrcReader {
	public Map<IMethodDescription, CoverageData> switcher(File trc, IMethodDescription method);
	public BinaryMethodCoverageData readBinary(File trc);
	public NumericMethodCoverageData readCount(File trc);
	public ChainCoverageData readChain(File trc);
}
