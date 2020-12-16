package main.java.hu.szte.sed.coverage;

import static main.java.hu.szte.sed.util.DataOutputStreamHelper.writeNumber;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BinaryMethodCoverageData<T extends Number> implements CoverageData<T> {

	private final Set<T> data = new HashSet<>();

	@Override
	public void enter(final T methodId) {
		data.add(methodId);
	}

	@Override
	public void leave(final T methodId) {
	}

	@Override
	public void reset() {
		data.clear();
	}
	
	public Set<T> getData(){
		return data;
	}

	@Override
	public void saveData(final File dataFile) {
		try {
			try (final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)))) {
				for (final T methodId : data) {
					writeNumber(dos, methodId);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void print() {
		System.out.println(data.toString());
	}

}
