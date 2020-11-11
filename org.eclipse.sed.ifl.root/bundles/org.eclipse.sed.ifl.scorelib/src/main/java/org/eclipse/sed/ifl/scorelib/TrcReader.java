package org.eclipse.sed.ifl.scorelib;

import java.io.File;
import java.util.Map;

import main.java.hu.szte.sed.coverage.BinaryMethodCoverageData;
import main.java.hu.szte.sed.coverage.ChainCoverageData;
import main.java.hu.szte.sed.coverage.CoverageData;
import main.java.hu.szte.sed.coverage.NumericMethodCoverageData;
import main.java.org.eclipse.sed.ifl.commons.model.source.IMethodDescription;

public class TrcReader implements ITrcReader {

	@Override
	public Map<IMethodDescription, CoverageData> switcher(File trc, IMethodDescription method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BinaryMethodCoverageData readBinary(File trc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumericMethodCoverageData readCount(File trc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChainCoverageData readChain(File trc) {
		// TODO Auto-generated method stub
		return null;
	}

}
