package org.eclipse.sed.ifl.scorelib;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import main.java.hu.szte.sed.coverage.BinaryMethodCoverageData;
import main.java.hu.szte.sed.coverage.ChainCoverageData;
import main.java.hu.szte.sed.coverage.CoverageData;
import main.java.hu.szte.sed.coverage.NumericMethodCoverageData;

public class TrcReader implements ITrcReader {

	@Override
	public Map<Short, int[]> readTrc(String trcDir) {
		
		File[] trcs = new File(trcDir).listFiles();
		Map<Short, int[]> map = new HashMap<Short, int[]>();
		
		for(File trc : trcs) {
			try {
				FileInputStream fin = new FileInputStream(trc);
				DataInputStream din = new DataInputStream(fin);
				short s = din.readShort();
				switch (s) {
					case 0:{
						readBinary(trc, map);
					}
					case 1:{
						readCount(trc, map);
					}
					case 2:{
						readChainAsCount(trc, map);
					}
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return map;
	}
	
	@Override
	public Map<Short[], int[]> readTrcChain(String trcDir) {
		
		File[] trcs = new File(trcDir).listFiles();
		Map<Short[], int[]> map = new HashMap<Short[], int[]>();
		
		for(File trc : trcs) {
			readChainAsChain(trc, map);
		}
		
		return map;
	}

	@Override
	public Map<Short, int[]> readBinary(File trc, Map<Short, int[]> map) {
		BinaryMethodCoverageData<Short> bin = new BinaryMethodCoverageData<Short>();
		try {
			FileInputStream fin = new FileInputStream(trc);
			DataInputStream din = new DataInputStream(fin);
			while(din.available()>0) {
				bin.enter(din.readShort());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boolean b = isTestSuccessful(trc);
		Iterator i = bin.getData().iterator();
		while(i.hasNext()) {
			short s = (short) i.next();
			int[] count = map.get(s);
			if(b) {count[0]++;}
			else {count[1]++;}
			map.put((Short)s, count);
		}
		return map;
	}

	@Override
	public Map<Short, int[]> readCount(File trc, Map<Short, int[]> map) {
		Boolean b = isTestSuccessful(trc);
		try {
			FileInputStream fin = new FileInputStream(trc);
			DataInputStream din = new DataInputStream(fin);
			while(din.available()>0) {
				short s = din.readShort();
				if(map.containsKey(s)) {
					int[] count = map.get(s);
					if(b) {count[0] += din.read();}
					else {count[1] += din.read();}
					map.put(s, count);
				} else {
					int[] count = new int[2];
					if(b) {count[0] += din.read();}
					else {count[1] += din.read();}
					map.put(s, count);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public Map<Short, int[]> readChainAsCount(File trc, Map<Short, int[]> map) {
		Boolean b = isTestSuccessful(trc);
		try {
			FileInputStream fin = new FileInputStream(trc);
			DataInputStream din = new DataInputStream(fin);
			while(din.available()>0) {
				short s = din.readShort();
				short[] d = new short[s];
				for(int i=0; i<s; i++) {
					d[i] = din.readShort();
				}
				s = din.readShort();
				for(int i=0; i<d.length; i++) {
					if(map.containsKey(d[i])) {
						int[] count = map.get(d[i]);
						if(b) {count[0] += s;}
						else {count[1] += s;}
						map.put(d[i], count);
					} else {
						int[] count = new int[2];
						if(b) {count[0] += s;}
						else {count[1] += s;}
						map.put(d[i], count);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public Map<Short[], int[]> readChainAsChain(File trc, Map<Short[], int[]> map) {
		Boolean b = isTestSuccessful(trc);
		try {
			FileInputStream fin = new FileInputStream(trc);
			DataInputStream din = new DataInputStream(fin);
			din.readShort();
			while(din.available()>0) {
				short s = din.readShort();
				Short[] d = new Short[s];
				for(int i=0; i<s; i++) {
					d[i] = din.readShort();
				}
				s = din.readShort();
				if(map.containsKey(d)) {
					int[] count = map.get(d);
					if(b) {count[0] += s;}
					else {count[1] += s;}
					map.put(d, count);
				} else {
					int[] count = new int[2];
					if(b) {count[0] += s;}
					else {count[1] += s;}
					map.put(d, count);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Boolean isTestSuccessful(File trc) {
		if(trc.getName().contains("PASS")) {
			return true;
		}
		return false;
	}
}
