package org.eclipse.sed.ifl.scorelib;

import java.util.Map;

public interface ITrcReader {
	public Map<Short, scoreVariables> readTrc(String trcDir);
	public Map<Short[], scoreVariables> readTrcChain(String trcDir);
}
