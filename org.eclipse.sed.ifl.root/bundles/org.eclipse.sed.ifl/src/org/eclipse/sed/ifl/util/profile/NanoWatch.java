package org.eclipse.sed.ifl.util.profile;

public class NanoWatch {
	
	private long past;
	private String name;
	
	public NanoWatch(String name) {
		this.name = name;
		start();
	}
	
	/**
	 * Start or restart the measurement.
	 */
	public void start() {
		System.out.printf("Measurement of task '%s' started.\n", name);
		past = System.nanoTime();
	}
	
	/**
	 * Stops the measurement.
	 * @return The elapsed time in seconds.
	 */
	public double stop() {
		long duration = System.nanoTime() - past;
		return duration / 1_000_000_000.0;
	}
	
	@Override
	public String toString() {
		return String.format("Task '%s' took %f seconds.", name, stop());
	}
}
