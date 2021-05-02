package hu.szte.sed.coverage;

import java.io.File;

public interface CoverageData<T extends Number> {

	public void enter(final T methodId);

	public void leave(final T methodId);

	public void reset();

	public void saveData(final File dataFile);

}
