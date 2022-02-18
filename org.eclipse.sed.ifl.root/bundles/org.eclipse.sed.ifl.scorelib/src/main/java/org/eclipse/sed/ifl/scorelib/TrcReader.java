package org.eclipse.sed.ifl.scorelib;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Arrays;

public class TrcReader implements ITrcReader {

	@Override
	public Map<Short, ScoreVariables> readTrc(String trcDir) {
		
		File[] trcs = new File(trcDir).listFiles();
		Map<Short, ScoreVariables> map = new HashMap<Short, ScoreVariables>();
		
		int fails = 0;
		
		for(File trc : trcs) {
			
			if(trc.getName().equals("trace.trc.names")) continue;
			
			try {
				FileInputStream fin = new FileInputStream(trc); 
				DataInputStream din = new DataInputStream(fin);
				
				//some trc files are too short to read properly
				
				short s = 1;
				din.readShort();
				din.readShort();
				if (din.available() <= 1) {
					fails++;
					System.out.println("Failed: " + trc.getName());
					continue;
				} else {
					din.readShort();
					din.readShort();
					s = din.readShort();
				}
				din.close();
				
				map.put(s, new ScoreVariables());
				switch (s) {
					case 1:{
						readBinary(trc, map);
					}
					case 2:{
						readCount(trc, map);
					}
					case 4:{
						readChainAsCount(trc, map);
					}
				}
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		System.out.println(fails +" file failed");
		return map;
	}
	
	@Override
	public Map<Short[], ScoreVariables> readTrcChain(String trcDir) {
		
		File[] trcs = new File(trcDir).listFiles();
		Map<Short[], ScoreVariables> map = new HashMap<Short[], ScoreVariables>();
		
		
		for(File trc : trcs) {
			readChainAsChain(trc, map);
		}
		
		return map;
	}

	public Map<Short, ScoreVariables> readBinary(File trc, Map<Short, ScoreVariables> map) {
		Set<Short> bin = new HashSet<Short>();
		try {
			FileInputStream fin = new FileInputStream(trc);
			DataInputStream din = new DataInputStream(fin);
			while(din.available()>0) {
				bin.add(din.readShort());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Boolean b = isTestSuccessful(trc);
		Set<Short> set = new HashSet(map.keySet());
		Iterator i = bin.iterator();
		while(i.hasNext()) {
			Short s = (short) i.next();
			if(map.containsKey(s)) {
				ScoreVariables count = map.get(s);
			
				if(b) {count.setExecutedPass(count.getExecutedPass() + 1);}
				else {count.setExecutedFail(count.getExecutedFail() + 1);}
				map.put(s, count);
				set.remove(s);
			}
		}
		
		Iterator<Short> it = set.iterator();
		while(it.hasNext()) {
			Short s = it.next();
			ScoreVariables count = map.get(s);
			
			if(b) {count.setNonExecutedPass(count.getNonExecutedPass() + 1);}
			else {count.setNonExecutedFail(count.getNonExecutedFail() + 1);}
			map.put(s, count);
		}
		
		return map;
	}

	public Map<Short, ScoreVariables> readCount(File trc, Map<Short, ScoreVariables> map) {
		Boolean b = isTestSuccessful(trc);
		Set<Short> set = new HashSet(map.keySet());
		try {
			FileInputStream fin = new FileInputStream(trc);
			DataInputStream din = new DataInputStream(fin);
			while(din.available()>0) {
				short s = din.readShort();
				if(map.containsKey(s)) {
					ScoreVariables count = map.get(s);
				
					if(b) {count.setExecutedPass(count.getExecutedPass() + din.read());}
					else {count.setExecutedFail(count.getExecutedFail() + din.read());}
					map.put(s, count);
					set.remove(s);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Iterator<Short> it = set.iterator();
		while(it.hasNext()) {
			Short s = it.next();
			ScoreVariables count = map.get(s);
			
			if(b) {count.setNonExecutedPass(count.getNonExecutedPass() + 1);}
			else {count.setNonExecutedFail(count.getNonExecutedFail() + 1);}
			map.put(s, count);
		}
		
		return map;
	}

	public Map<Short, ScoreVariables> readChainAsCount(File trc, Map<Short, ScoreVariables> map) {
		Boolean b = isTestSuccessful(trc);
		Set<Short> set = new HashSet(map.keySet());
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
					ScoreVariables count = map.get(d[i]);
					for(Entry<Short, ScoreVariables> entry : map.entrySet()){
						count = entry.getValue();
						if(map.containsKey(entry.getKey())) {
							if(b) {count.setExecutedPass(count.getExecutedPass() + s);}
							else {count.setExecutedFail(count.getExecutedFail() + s);}
							map.put(d[i], count);
						}
						set.remove(entry.getKey());
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Iterator<Short> it = set.iterator();
		while(it.hasNext()) {
			Short s = it.next();
			ScoreVariables count = map.get(s);
			
			if(b) {count.setNonExecutedPass(count.getNonExecutedPass() + 1);}
			else {count.setNonExecutedFail(count.getNonExecutedFail() + 1);}
			map.put(s, count);
		}
		
		return map;
	}
	
	public Map<Short[], ScoreVariables> readChainAsChain(File trc, Map<Short[], ScoreVariables> map) {
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
				map.put(d, new ScoreVariables()); //todo
				s = din.readShort();
				for(Entry<Short[], ScoreVariables> entry : map.entrySet()){
					ScoreVariables count = entry.getValue();
					if(d.equals( entry.getKey())) {
						if(b) {count.setExecutedPass(count.getExecutedPass() + s);}
						else {count.setExecutedFail(count.getExecutedFail() + s);}
						map.put(d, count);
					}else{
						if(b) {count.setExecutedPass(count.getExecutedPass() + s);}
						else {count.setExecutedFail(count.getExecutedFail() + s);}
						map.put(d, count);
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

	private Boolean isTestSuccessful(File trc) {
		if(trc.getName().contains("PASS")) {
			return true;
		}
		return false;
	}
}
