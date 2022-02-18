package org.eclipse.sed.ifl.scorelib;

/*import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestScoreCalculator {
	
	ScoreCalculator sc;
	
	@Before public void createScoreCalculator() {
		sc = new ScoreCalculator();
	}
	
	@Test public void testTarantula1() throws ScoreException {
		Map<Short, int[]> input = new HashMap<Short, int[]>();
		Short s = new Short((short) 1);
		int[] i = {2,1,3,5};
		input.put(s, i);
		s = (short) 2;
		i = new int[] {1,1,1,1};
		input.put(s, i);
		Map<Short, Double> actual = new HashMap<Short, Double>();
		actual = sc.calculate(input, "tarantula", "");
		Map<Short, Double> expected = new HashMap<Short, Double>();
		Double d = new Double(5d/17d);
		s =(short) 1;
		expected.put(s, d);
		s =(short) 2;
		d = new Double(0.5);
		expected.put(s, d);
		assertEquals(expected, actual);
	}
	
	@Test public void testOchiai1() throws ScoreException {
		Map<Short, int[]> input = new HashMap<Short, int[]>();
		Short s = new Short((short) 1);
		int[] i = {2,1,3,5};
		input.put(s, i);
		s = (short) 2;
		i = new int[] {1,1,1,1};
		input.put(s, i);
		Map<Short, Double> actual = new HashMap<Short, Double>();
		actual = sc.calculate(input, "ochiai", "");
		Map<Short, Double> expected = new HashMap<Short, Double>();
		Double d = new Double(1d/18d);
		s =(short) 1;
		expected.put(s, d);
		s =(short) 2;
		d = new Double(0.25);
		expected.put(s, d);
		assertEquals(expected, actual);
	}
	
	@Test public void testevalT() throws ScoreException {
		Map<Short, int[]> input = new HashMap<Short, int[]>();
		Short s = new Short((short) 1);
		int[] i = {2,1,3,5};
		input.put(s, i);
		s = (short) 2;
		i = new int[] {1,1,1,1};
		input.put(s, i);
		Map<Short, Double> actual = new HashMap<Short, Double>();
		actual = sc.calculate(input, "eval", "ef/(ef+nf)/(ef/(ef+nf)+ep/(ep+np))");
		Map<Short, Double> expected = new HashMap<Short, Double>();
		Double d = new Double(5d/17d);
		s =(short) 1;
		expected.put(s, d);
		s =(short) 2;
		d = new Double(0.5);
		expected.put(s, d);
		assertEquals(expected, actual);
	}
	
	@Test public void testEvalO() throws ScoreException {
		Map<Short, int[]> input = new HashMap<Short, int[]>();
		Short s = new Short((short) 1);
		int[] i = {2,1,3,5};
		input.put(s, i);
		s = (short) 2;
		i = new int[] {1,1,1,1};
		input.put(s, i);
		Map<Short, Double> actual = new HashMap<Short, Double>();
		actual = sc.calculate(input, "eval", "ef/((ef+nf)*(ef+ep))");
		Map<Short, Double> expected = new HashMap<Short, Double>();
		Double d = new Double(1d/18d);
		s =(short) 1;
		expected.put(s, d);
		s =(short) 2;
		d = new Double(0.25);
		expected.put(s, d);
		assertEquals(expected, actual);
	}
}*/
