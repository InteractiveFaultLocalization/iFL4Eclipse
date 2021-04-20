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
	public Map<Short, int[]> readTrc(String trcDir, Short[] methodIDs) {
		
		File[] trcs = new File(trcDir).listFiles();
		Map<Short, int[]> map = new HashMap<Short, int[]>();
		
		for(Short s : methodIDs) {
			map.put(s, new int[4]);
		}
		
		for(File trc : trcs) {
			
			if(trc.getName().equals("trace.trc.names"))break;
			
			try {
				FileInputStream fin = new FileInputStream(trc); 
				DataInputStream din = new DataInputStream(fin);
				/*
				din.readshort();
				din.readshort();
				din.readshort();
				din.readshort();
				short s = din.readShort();
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
				}*/
				readBinary(trc, map);
				for(Entry<Short, int[]> entry : map.entrySet()) {
					System.out.print(entry.getKey()+" ");
					System.out.println(Arrays.toString(entry.getValue()));
				}
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		return map;
	}
	
	@Override
	public Map<Short[], int[]> readTrcChain(String trcDir, Short[][] chains) {
		
		File[] trcs = new File(trcDir).listFiles();
		Map<Short[], int[]> map = new HashMap<Short[], int[]>();
		
		for(Short[] s : chains) {
			map.put(s, new int[4]);
		}
		
		for(File trc : trcs) {
			readChainAsChain(trc, map);
		}
		
		return map;
	}

	public Map<Short, int[]> readBinary(File trc, Map<Short, int[]> map) {
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
				int[] count = map.get(s);
			
				if(b) {count[0]++;}
				else {count[1]++;}
				map.put(s, count);
				set.remove(s);
			}
		}
		
		Iterator<Short> it = set.iterator();
		while(it.hasNext()) {
			Short s = it.next();
			int[] count = map.get(s);
			
			if(b) {count[2]++;}
			else {count[3]++;}
			map.put(s, count);
		}
		
		return map;
	}

	public Map<Short, int[]> readCount(File trc, Map<Short, int[]> map) {
		Boolean b = isTestSuccessful(trc);
		Set<Short> set = new HashSet(map.keySet());
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
			int[] count = map.get(s);
			
			if(b) {count[2]++;}
			else {count[3]++;}
			map.put(s, count);
		}
		
		return map;
	}

	public Map<Short, int[]> readChainAsCount(File trc, Map<Short, int[]> map) {
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
					int[] count = map.get(d[i]);
					for(Entry<Short, int[]> entry : map.entrySet()){
						count = entry.getValue();
						if(map.containsKey(entry.getKey())) {
							if(b) {count[0] += s;}
							else {count[1] += s;}
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
			int[] count = map.get(s);
			
			if(b) {count[2]++;}
			else {count[3]++;}
			map.put(s, count);
		}
		
		return map;
	}
	
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
				for(Entry<Short[], int[]> entry : map.entrySet()){
					int count[] = entry.getValue();
					if(d.equals( entry.getKey())) {
						if(b) {count[0] += s;}
						else {count[1] += s;}
						map.put(d, count);
					}else{
						if(b) {count[0] += s;}
						else {count[1] += s;}
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
