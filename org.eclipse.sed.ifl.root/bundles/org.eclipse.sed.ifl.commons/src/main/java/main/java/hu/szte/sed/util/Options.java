package main.java.hu.szte.sed.util;

import java.util.HashMap;
import java.util.Map;

public class Options {

	public static final String DESTFILE = "destfile";
	public static final String DEFAULT_DESTFILE = "trace.trc";

	public static final String DESTDIR = "destdir";
	public static final String DEFAULT_DESTDIR = "coverage";

	public static final String INCLUDES = "includes";
	public static final String DEFAULT_INCLUDES = ".*";

	public static final String EXCLUDES = "excludes";
	public static final String DEFAULT_EXCLUDES = "";

	public static final String PERTESTMODE = "pertestmode";
	public static final boolean DEFAULT_PERTESTMODE = true;

	public static final String GRANULARITY = "granularity";
	public static final Granularity DEFAULT_GRANULARITY = Granularity.CHAIN;

	private final Map<String, String> options = new HashMap<String, String>();

	public Options(final String optionStr) {
		processArgs(optionStr);
	}

	public String getDestfile() {
		return getOption(DESTFILE, DEFAULT_DESTFILE);
	}

	public String getDestdir() {
		return getOption(DESTDIR, DEFAULT_DESTDIR);
	}

	public String getIncludes() {
		return getOption(INCLUDES, DEFAULT_INCLUDES);
	}

	public String getExcludes() {
		return getOption(EXCLUDES, DEFAULT_EXCLUDES);
	}

	public boolean getPerTestMode() {
		return getOption(PERTESTMODE, DEFAULT_PERTESTMODE);
	}

	public Granularity getGranularity() {
		return Granularity.fromText(getOption(GRANULARITY, DEFAULT_GRANULARITY.getText()));
	}

	private String getOption(final String key, final String defaultValue) {
		final String val = options.get(key);

		if (val == null) {
			return defaultValue;
		}

		return val;
	}

	private boolean getOption(final String key, final boolean defaultValue) {
		if (options.containsKey(key)) {
			final String value = options.get(key);

			if (value == null) { // Flag without value
				return true;
			} else {
				return Boolean.parseBoolean(value);
			}
		} else {
			return defaultValue;
		}
	}

	private void processArgs(final String args) {
		if (args == null) {
			return;
		}

		final String[] splitted = args.split(",");

		for (final String argString : splitted) {
			final String[] argParts = argString.split("=");
			final String key = argParts[0];

			if (argParts.length == 1) {
				options.put(key, null);

				continue;
			}

			final StringBuilder value = new StringBuilder();

			for (int i = 1; i < argParts.length; i++) {
				value.append(argParts[i]);
			}

			options.put(key, value.toString());
		}
	}

	@Override
	public String toString() {
		return options.toString();
	}

}
