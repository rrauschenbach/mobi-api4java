package org.rr.mobi4java;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class ByteUtils {
	
	public static void write(byte[] data, OutputStream out) throws IOException {
		write(data, data.length, out);
	}
	
	public static void write(byte[] data, int length, OutputStream out) throws IOException {
		out.write(data != null ? data : new byte[length], 0, length);
	}

	public static void writeInt(int data, int length, OutputStream out) throws IOException {
		out.write(getBytes(data, new byte[length]), 0, length);
	}
	
	public static void writeLong(long data, int length, OutputStream out) throws IOException {
		out.write(getBytes(data, new byte[length]), 0, length);
	}

	public static void writeString(String data, int length, OutputStream out) throws IOException {
		byte[] s = getBytes(data);
		byte[] b = new byte[length];
		System.arraycopy(s, 0, b, 0, s.length);
		out.write(b, 0, length);
	}

	public static byte[] getBytes(byte[] buffer, int offset) {
		byte[] b = new byte[buffer.length - offset];
		System.arraycopy(buffer, offset, b, 0, buffer.length - offset);
		return b;
	}
	
	public static byte[] getBytes(byte[] buffer, int offset, int length) {
		byte[] b = new byte[length];
		System.arraycopy(buffer, offset, b, 0, length);
		return b;
	}

	public static int getInt(byte[] buffer, int offset, int length) {
		return getInt(getBytes(buffer, offset, length));
	}
	
	public static long getLong(byte[] buffer, int offset, int length) {
		return getLong(getBytes(buffer, offset, length));
	}

	public static String getString(byte[] buffer, int offset, int length) {
		return getString(getBytes(buffer, offset, length));
	}

	public static String getString(byte[] buffer) {
		return getString(buffer, null);
	}

	public static String getString(byte[] buffer, String encoding) {
		if(buffer == null || buffer.length == 0) {
			return EMPTY;
		}
		
		int len = buffer.length;
		int zeroIndex = -1;
		for (int i = 0; i < len; i++) {
			byte b = buffer[i];
			if (b == 0) {
				zeroIndex = i;
				break;
			}
		}

		if (encoding != null) {
			try {
				if (zeroIndex == -1) {
					return new String(buffer, encoding);
				} else {
					return new String(buffer, 0, zeroIndex, encoding);
				}
			} catch (java.io.UnsupportedEncodingException e) {
				// let it fall through and use the default encoding
			}
		}

		if (zeroIndex == -1) {
			return new String(buffer);
		} else {
			return new String(buffer, 0, zeroIndex);
		}
	}

	public static int getInt(byte[] buffer) {
		int total = 0;
		int len = buffer.length;
		for (int i = 0; i < len; i++) {
			total = (total << 8) + (buffer[i] & 0xff);
		}

		return total;
	}

	public static long getLong(byte[] buffer) {
		long total = 0;
		int len = buffer.length;
		for (int i = 0; i < len; i++) {
			total = (total << 8) + (buffer[i] & 0xff);
		}

		return total;
	}

	public static byte[] getBytes(int value, byte[] dest) {
		int lastIndex = dest.length - 1;
		for (int i = lastIndex; i >= 0; i--) {
			dest[i] = (byte) (value & 0xff);
			value = value >> 8;
		}
		return dest;
	}

	public static byte[] getBytes(long value, byte[] dest) {
		int lastIndex = dest.length - 1;
		for (int i = lastIndex; i >= 0; i--) {
			dest[i] = (byte) (value & 0xff);
			value = value >> 8;
		}
		return dest;
	}

	public static byte[] getBytes(String s) {
		return s.getBytes();
	}

	public static byte[] getBytes(String s, String encoding) throws UnsupportedEncodingException {
		if (encoding != null) {
			return s.getBytes(encoding);
		}

		return s.getBytes();
	}

	public static List<byte[]> chunk(byte[] source, int chunksize) {
		byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunksize)][chunksize];
		int start = 0;
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Arrays.copyOfRange(source, start, start + chunksize);
			start += chunksize;
		}
		return Arrays.asList(ret);
	}

	public static String dumpByteArray(byte[] buffer) {
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		int len = buffer.length;
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(buffer[i] & 0xff);
		}
		sb.append(" }");
		return sb.toString();
	}
	
	public static boolean startsWith(byte[] a, byte[] b) {
		if (a.length >= b.length) {
			for (int i = 0; i < b.length; i++) {
				if (a[i] != b[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
