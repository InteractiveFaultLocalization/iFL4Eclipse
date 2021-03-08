package org.eclipse.sed.ifl.scorelib;

import java.util.Map;

public interface ITrcReader {
	public Map<Short, int[]> readTrc(String trcDir, Short[] methodIDs);
	public Map<Short[], int[]> readTrcChain(String trcDir, Short[][] chains);
}
