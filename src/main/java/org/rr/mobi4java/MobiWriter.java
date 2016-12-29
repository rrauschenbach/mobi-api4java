package org.rr.mobi4java;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MobiWriter {
	
	private static final byte[] MOBI_EOF = new byte[] {(byte) 0xeE9, (byte) 0x8E, 0x0D, 0x0A};
	
	private MobiDocument doc;
	
	public MobiWriter(MobiDocument doc) {
		this.doc = doc;
	}

	/**
	 * Write the mobipocket document to the given {@link File}. 
	 * 
	 * @param doc The {@link MobiDocument} instance which should be written to the given {@link File}.
	 * @param file The {@link File} where the mobipocket data will be written to.
	 * @throws IOException
	 */
	public void write(File file) throws IOException {
		try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
			write(out);
		}
	}
	
	/**
	 * Write the mobipocket document to the given {@link OutputStream}. Note that the given {@link OutputStream} won't be closed here. You
	 * have to close it by your own.
	 * 
	 * @param doc The {@link MobiDocument} instance which should be written to the given {@link OutputStream}.
	 * @param out The {@link OutputStream} where the mobipocket data will be written to.
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException {
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
