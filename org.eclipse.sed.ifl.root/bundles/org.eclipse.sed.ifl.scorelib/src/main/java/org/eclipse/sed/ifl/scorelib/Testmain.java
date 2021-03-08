package org.eclipse.sed.ifl.scorelib;

import java.util.Map;
import java.util.Arrays;


public class Testmain {
	public static void main(String[] args) {
		Short[] s = {3, 4, 5, 6, 7, 66, 320, 420, 512, 1024};
		
		TrcReader r = new TrcReader();
		ScoreCalculator sc = new ScoreCalculator();
		Short s2 = 3;
		
		Map<Short, int[]> m = r.readTrc("C:\\Users\\Bence\\Downloads\\coverage", s);
		System.out.println(Arrays.toString(m.get(s2)));
		Map<Short, Double> ms = null;
		try {
			ms = sc.calculate(m, "ochiai", "");
		} catch (ScoreException e) {
			e.printStackTrace();
		}
		System.out.println(ms.get(s2));
	}
}
