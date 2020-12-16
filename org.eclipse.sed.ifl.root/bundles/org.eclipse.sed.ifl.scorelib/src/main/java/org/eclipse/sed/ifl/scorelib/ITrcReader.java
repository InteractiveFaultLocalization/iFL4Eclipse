package org.eclipse.sed.ifl.scorelib;

import java.io.File;
import java.util.Map;

import main.java.hu.szte.sed.coverage.BinaryMethodCoverageData;
import main.java.hu.szte.sed.coverage.ChainCoverageData;
import main.java.hu.szte.sed.coverage.CoverageData;
import main.java.hu.szte.sed.coverage.NumericMethodCoverageData;
import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public interface ITrcReader {
	public Map<Short[], int[]> readTrcChain(String trcDir);
	public Map<Short, int[]> readTrc(String trcDir);
	public Map<Short, int[]> readBinary(File trc, Map<Short, int[]> map);
	public Map<Short, int[]> readCount(File trc, Map<Short, int[]> map);
	public Map<Short, int[]> readChainAsCount(File trc, Map<Short, int[]> map);
	public Map<Short[], int[]> readChainAsChain(File trc, Map<Short[], int[]> map);
}
