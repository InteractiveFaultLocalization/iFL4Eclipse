package org.eclipse.sed.ifl.scorelib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.*;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ScoreCalculator implements IScoreCalculator {

	@Override
	public Map<Short, Double> calculate(Map<Short, int[]> map, String method, String eval) throws ScoreException {
		Map<Short, Double> scoreMap = new HashMap<Short, Double>();
		if(method.equals("tarantula")) {
			for(Entry<Short, int[]> entry : map.entrySet()) {
				scoreMap.put(entry.getKey(), tarantula(entry.getValue()));
			}
		}
		if(method.equals("ochiai")) {
			for(Entry<Short, int[]> entry : map.entrySet()) {
				scoreMap.put(entry.getKey(), ochiai(entry.getValue()));
			}
		}
		if(method.equals("eval")) {
			for(Entry<Short, int[]> entry : map.entrySet()) {
				scoreMap.put(entry.getKey(), eval(entry.getValue(), eval));
			}
		}
		return scoreMap;
	}

	@Override
	public Map<Short[], Double> calculateChain(Map<Short[], int[]> map, String method, String eval) {
		Map<Short[], Double> scoreMap = new HashMap<Short[], Double>();
		
		return scoreMap;
	}
	
	private double tarantula(int[] success) throws ScoreException {
		double ep = success[0];
		double ef = success[1];
		double np = success[2];
		double nf = success[3];
		
		try {
			return ef/(ef+nf)/(ef/(ef+nf)+ep/(ep+np));
		} catch(Exception e) {
			throw new ScoreException("Tarantula score error, ef+nf or ep+np is 0.");
		}
	}
	
	private double ochiai(int[] success) throws ScoreException {
		double ep = success[0];
		double ef = success[1];
		double np = success[2];
		double nf = success[3];
		
		try {
			return ef/((ef+nf)*(ef+ep));
		} catch(Exception e){
			throw new ScoreException("Ochiai score error, ef+nf or ef+ep is 0.");
		}
	}

	private double eval(int[] success, String eval) throws ScoreException {
		double ep = success[0];
		double ef = success[1];
		double np = success[2];
		double nf = success[3];
		
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
