package org.rr.mobi4java.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class MobiLz77 {

	public static String lz77DecodeToString(byte[] input, String encoding) throws UnsupportedEncodingException {
		return new String(lz77Decode(input), encoding);
	}

	public static byte[] lz77Decode(byte[] input) {
		byte[] out = new byte[input.length * 8];
		int i = 0, o = 0;
		while (i < input.length) {
			int c = input[i++] & 0x00FF;
			if (c >= 0x01 && c <= 0x08) {
				for (int j = 0; j < c && i + j < input.length; j++) {
					out[o++] = input[i + j];
				}
				i += c;
			} else if (c <= 0x7f) {
				out[o++] = (byte) c;
			} else if (c >= 0xC0) {
				out[o++] = ' ';
				out[o++] = (byte) (c ^ 0x80);
			} else if (c <= 0xbf) {
				if (i < input.length) {
					c = c << 8 | input[i++] & 0xFF;
					int length = (c & 0x0007) + 3;
					int location = (c >> 3) & 0x7FF;

					if (location > 0 && location <= o) {
						for (int j = 0; j < length; j++) {
							int idx = o - location;
							out[o++] = out[idx];
						}
					} else {
						// invalid idx
					}
				}
			} else {
				// unknown input
			}
		}

		byte[] result = new byte[o];
		System.arraycopy(out, 0, result, 0, o);
		return result;
	}

	public static byte[] lz77Encode(byte[] b) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = 0, tempLen = 0;
		long compound;
		byte[] temp = new byte[8];

		outer: while (i < b.length) {
			byte c = b[i];

			if (i > 10 && b.length - i > 10) {
				for (int chunkLength = 10; chunkLength > 2; chunkLength--) {
					int j = find(b, i, chunkLength);
					int dist = i - j;
					if (j < i && dist <= 2047) {
						compound = ((dist << 3) + chunkLength - 3);
						out.write((char) (0x80 + (compound >> 8)));
						out.write((char) (compound & 0xFF));
						i += chunkLength;
						continue outer;
					}
				}
			}

			i++;
			if (c == 32 && i < b.length) {
				if (b[i] >= 0x40 && b[i] <= 0x7F) {
					out.write(b[i] ^ 0x80);
					i++;
					continue;
				}
			}
			if (c == 0 || (c > 8 && c < 0x80)) {
				out.write(c);
			} else {
				int j = i;
				temp[0] = c;
				tempLen = 1;
				while (j < b.length && tempLen < 8) {
					c = b[j];
					if (c == 0 || (c > 8 && c < 0x80)) {
						break;
					}
					temp[tempLen++] = c;
					j++;
				}
				i += tempLen - 1;
				out.write((char) tempLen);
				for (j = 0; j < tempLen; j++) {
					out.write((char) temp[j]);
				}
			}

		}
		return out.toByteArray();
	}

	private static int find(byte[] data, int pos, int chunkLength) {
		for (int i = pos - chunkLength; i > -1; i--) {
			if (compare(data, i, pos, chunkLength)) {
				return i;
			}
		}
		return pos;
	}

	private static boolean compare(byte[] data, int off1, int off2, int len) {
		for (int i = 0; i < len; i++) {
			if (data[off1 + i] != data[off2 + i]) {
				return false;
			}
		}
		return true;
	}

}
