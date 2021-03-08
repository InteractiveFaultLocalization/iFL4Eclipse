package org.eclipse.sed.ifl.scorelib;

import java.util.Map;

import javax.script.ScriptException;

public interface IScoreCalculator {
	public Map<Short, Double> calculate(Map<Short, int[]> map, String method, String eval) throws ScoreException;
	public Map<Short[], Double> calculateChain(Map<Short[], int[]> map, String method, String eval);
}
