package org.eclipse.sed.ifl.scorelib;

import java.util.Map;

public interface ITrcReader {
	public Map<Short, ScoreVariables> readTrc(String trcDir);
	public Map<Short[], ScoreVariables> readTrcChain(String trcDir);
}
