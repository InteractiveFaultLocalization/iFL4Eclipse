package main.java.hu.szte.sed.coverage;

import static main.java.hu.szte.sed.util.DataOutputStreamHelper.writeNumber;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NumericMethodCoverageData<T extends Number> implements CoverageData<T> {

	private final Map<T, Long> data = new HashMap<>();

	@Override
	public void enter(final T methodId) {
		if (data.containsKey(methodId)) {
			data.put(methodId, data.get(methodId) + 1);
		} else {
			data.put(methodId, 1L);
		}
	}

	@Override
	public void leave(final T methodId) {
	}

	@Override
	public void reset() {
		data.clear();
	}
	
	public Map<T, Long> getData(){
		return data;
	}

	@Override
	public void saveData(final File dataFile) {
		try {
			try (final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)))) {
				for (final T methodId : data.keySet()) {
					writeNumber(dos, methodId);
					writeNumber(dos, data.get(methodId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
