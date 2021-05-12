package hu.szte.sed.util;

import java.io.DataOutputStream;
import java.io.IOException;

public class DataOutputStreamHelper {

	public static void writeNumber(final DataOutputStream dos, final Number n) throws IOException {
		if (n instanceof Short) {
			dos.writeShort(n.shortValue());
		} else if (n instanceof Integer) {
			dos.writeInt(n.intValue());
		} else if (n instanceof Long) {
			dos.writeLong(n.longValue());
		}
	}

}
