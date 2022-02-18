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
	public Map<Short, scoreVariables> readTrc(String trcDir) {
		
		File[] trcs = new File(trcDir).listFiles();
		Map<Short, scoreVariables> map = new HashMap<Short, scoreVariables>();
		
		for(File trc : trcs) {
			
			if(trc.getName().equals("trace.trc.names"))break;
			
			try {
				FileInputStream fin = new FileInputStream(trc); 
				DataInputStream din = new DataInputStream(fin);
				din.readShort();
				din.readShort();
				din.readShort();
				din.readShort();
				short s = din.readShort();
				map.put(s, new scoreVariables());
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
		
		return map;
	}
	
	@Override
	public Map<Short[], scoreVariables> readTrcChain(String trcDir) {
		
		File[] trcs = new File(trcDir).listFiles();
		Map<Short[], scoreVariables> map = new HashMap<Short[], scoreVariables>();
		
		
		for(File trc : trcs) {
			readChainAsChain(trc, map);
		}
		
		return map;
	}

	public Map<Short, scoreVariables> readBinary(File trc, Map<Short, scoreVariables> map) {
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
				scoreVariables count = map.get(s);
			
				if(b) {count.setExecutedPass(count.getExecutedPass() + 1);}
				else {count.setExecutedFail(count.getExecutedFail() + 1);}
				map.put(s, count);
				set.remove(s);
			}
		}
		
		Iterator<Short> it = set.iterator();
		while(it.hasNext()) {
			Short s = it.next();
			scoreVariables count = map.get(s);
			
			if(b) {count.setNonExecutedPass(count.getNonExecutedPass() + 1);}
			else {count.setNonExecutedFail(count.getNonExecutedFail() + 1);}
			map.put(s, count);
		}
		
		return map;
	}

	public Map<Short, scoreVariables> readCount(File trc, Map<Short, scoreVariables> map) {
		Boolean b = isTestSuccessful(trc);
		Set<Short> set = new HashSet(map.keySet());
		try {
			FileInputStream fin = new FileInputStream(trc);
			DataInputStream din = new DataInputStream(fin);
			while(din.available()>0) {
				short s = din.readShort();
				if(map.containsKey(s)) {
					scoreVariables count = map.get(s);
				
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
			scoreVariables count = map.get(s);
			
			if(b) {count.setNonExecutedPass(count.getNonExecutedPass() + 1);}
			else {count.setNonExecutedFail(count.getNonExecutedFail() + 1);}
			map.put(s, count);
		}
		
		return map;
	}

	public Map<Short, scoreVariables> readChainAsCount(File trc, Map<Short, scoreVariables> map) {
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
					scoreVariables count = map.get(d[i]);
					for(Entry<Short, scoreVariables> entry : map.entrySet()){
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
			scoreVariables count = map.get(s);
			
			if(b) {count.setNonExecutedPass(count.getNonExecutedPass() + 1);}
			else {count.setNonExecutedFail(count.getNonExecutedFail() + 1);}
			map.put(s, count);
		}
		
		return map;
	}
	
	public Map<Short[], scoreVariables> readChainAsChain(File trc, Map<Short[], scoreVariables> map) {
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
				map.put(d, new scoreVariables()); //todo
				s = din.readShort();
				for(Entry<Short[], scoreVariables> entry : map.entrySet()){
					scoreVariables count = entry.getValue();
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
