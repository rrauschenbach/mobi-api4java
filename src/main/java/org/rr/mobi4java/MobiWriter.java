package org.rr.mobi4java;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MobiWriter {
	
	private static final byte[] MOBI_EOF = new byte[] {(byte) 0xeE9, (byte) 0x8E, 0x0D, 0x0A};

	public void write(MobiDocument doc, File file) throws IOException {
		try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
			write(doc, out);
		}
	}
	
	public void write(MobiDocument doc, OutputStream out) throws IOException {
		doc.getPdbHeader().writeHeader(doc.getMobiHeader(), doc.getMobiContents(), out);
		byte[] written = null; 
		for (MobiContent mobiContent : doc.getMobiContents()) {
			written = mobiContent.writeContent(out);
		}

		// write EOF if not already done.
		writeEof(out, written);
	}

	private void writeEof(OutputStream out, byte[] written) throws IOException {
		if(!ByteUtils.startsWith(written, MOBI_EOF)) {
			out.write(MOBI_EOF);	
		}
	}

}
