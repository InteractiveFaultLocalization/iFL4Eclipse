package org.eclipse.sed.ifl.scorelib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.*;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ScoreCalculator implements IScoreCalculator {

	@Override
	public IResults calculate(Map<Short, ScoreVariables> map, IFormula f) throws ScoreException {
		Map<Short, Double> scoreMap = new HashMap<Short, Double>();
		/*if(method.equals("tarantula")) {
			for(Entry<Short, scoreVariables> entry : map.entrySet()) {
				scoreMap.put(entry.getKey(), tarantula(entry.getValue()));
			}
		}
		if(method.equals("ochiai")) {
			for(Entry<Short, scoreVariables> entry : map.entrySet()) {
				scoreMap.put(entry.getKey(), ochiai(entry.getValue()));
			}
		}
		if(method.equals("eval")) {
			for(Entry<Short, scoreVariables> entry : map.entrySet()) {
				scoreMap.put(entry.getKey(), eval(entry.getValue(), eval));
			}
		}*/
		
		IResults res = new Results();
		return res;
	}

	@Override
	public IResults calculateChain(Map<Short[], ScoreVariables> map, IFormula f) {
		Map<Short[], Double> scoreMap = new HashMap<Short[], Double>();
		
		IResults res = new Results();
		return res;
	}
	
	private double tarantula(ScoreVariables success) throws ScoreException {
		double ep = success.getExecutedPass();
		double ef = success.getExecutedFail();
		double np = success.getNonExecutedPass();
		double nf = success.getExecutedFail();
		
		try {
			return ef/(ef+nf)/(ef/(ef+nf)+ep/(ep+np));
		} catch(Exception e) {
			throw new ScoreException("Tarantula score error, ef+nf or ep+np is 0.");
		}
	}
	
	private double ochiai(ScoreVariables success) throws ScoreException {
		double ep = success.getExecutedPass();
		double ef = success.getExecutedFail();
		double np = success.getNonExecutedPass();
		double nf = success.getExecutedFail();
		
		try {
			return ef/((ef+nf)*(ef+ep));
		} catch(Exception e){
			throw new ScoreException("Ochiai score error, ef+nf or ef+ep is 0.");
		}
	}

	private double eval(ScoreVariables success, String eval) throws ScoreException {
		double ep = success.getExecutedPass();
		double ef = success.getExecutedFail();
		double np = success.getNonExecutedPass();
		double nf = success.getExecutedFail();
		
		//ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		
		eval = eval.replace("ep", String.valueOf(ep));
		eval = eval.replace("ef", String.valueOf(ef));
		eval = eval.replace("np", String.valueOf(np));
		eval = eval.replace("nf", String.valueOf(nf));
		
		Expression e;
		
		try {
			e = new ExpressionBuilder(eval).build();
		} catch (Exception ex) {
			throw new ScoreException("Eval score error, you probably tried to devide with 0.");
		}
		
		return e.evaluate();
	}
	
}
