package org.eclipse.sed.ifl.scorelib;

import java.util.Map;

import javax.script.ScriptException;

public interface IScoreCalculator {
	public IResults calculate(Map<Short, ScoreVariables> map, IFormula f) throws ScoreException;
	public IResults calculateChain(Map<Short[], ScoreVariables> map, IFormula f);
}
