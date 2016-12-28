package org.rr.mobi4java;

import static org.rr.mobi4java.MobiContentRecordFactory.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.rr.mobi4java.MobiContent.CONTENT_TYPE;

public class MobiReader {

	/**
	 * Read and parse a mobi document from the given {@link File}.
	 * 
	 * @param file A {@link File} which points to a mobi document.
	 * @return A new {@link MobiDocument} instance. Never returns <code>null</code>.
	 * @throws IOException
	 */
	public MobiDocument read(File file) throws FileNotFoundException, IOException {
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			return read(in);
		}
	}

	/**
	 * Read and parse a mobi document from the given {@link InputStream}. The given {@link InputStream} will not be closed after reading.
	 * 
	 * @param in {@link InputStream} which provides the mobi data to be parsed.
	 * @return A new {@link MobiDocument} instance. Never returns <code>null</code>.
	 * @throws IOException
	 */
	public MobiDocument read(InputStream in) throws IOException {
		byte[] mobiData = IOUtils.toByteArray(in);
		PDBHeader pdbHeader = readPDBHeader(mobiData);
		MobiContentHeader mobiHeader = readMobiHeader(pdbHeader, mobiData);
		List<MobiContent> mobiContent = readMobiContent(pdbHeader, mobiHeader, mobiData);
		return new MobiDocument(pdbHeader, mobiHeader, mobiContent);
	}
	
	/**
	 * Creates an empty mobi document which can be used as a template for new mobi files.  
	 * 
	 * @return A new mobi document. Never returns <code>null</code>.
	 */
	public MobiDocument empty() {
		try {
			return read(getClass().getResourceAsStream("/template.mobi"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load template.", e);
		}
	}

	private PDBHeader readPDBHeader(byte[] mobiData) {
		return PDBHeader.readHeader(mobiData);
	}

	private MobiContentHeader readMobiHeader(PDBHeader pdbHeader, byte[] mobiData) throws IOException {
		return MobiContentHeader.readMobiHeader(mobiData, getRecordDataOffset(pdbHeader, 0), getRecordDataLength(pdbHeader, 0));
	}

	private List<MobiContent> readMobiContent(PDBHeader pdbHeader, MobiContentHeader mobiHeader, byte[] mobiData) throws IOException {
		List<MobiContent> mobiContents = new ArrayList<>();
		mobiContents.add(mobiHeader);
		int recordCount = pdbHeader.getRecordCount();
		for (int i = 1; i < recordCount; i++) {
			mobiContents.add(createMobiContent(pdbHeader, mobiHeader, mobiData, i));
		}
		return mobiContents;
	}

	private MobiContent createMobiContent(PDBHeader pdbHeader, MobiContentHeader mobiHeader, byte[] mobiData, int index) throws IOException {
		long recordDataOffset = getRecordDataOffset(pdbHeader, index);
		long recordDataLength = getRecordDataLength(pdbHeader, index);
		CONTENT_TYPE type = evaluateType(pdbHeader, mobiHeader, index, mobiData, recordDataOffset, recordDataLength);
		return readContent(mobiData, type, recordDataOffset, recordDataLength);
	}

	private long getRecordDataOffset(PDBHeader pdbHeader, int idx) {
		return pdbHeader.getRecord(idx).getRecordDataOffset();
	}

	private long getRecordDataLength(PDBHeader pdbHeader, int idx) {
		long start = pdbHeader.getRecord(idx).getRecordDataOffset();
		if (pdbHeader.hasRecord(idx + 1)) {
			return pdbHeader.getRecord(idx + 1).getRecordDataOffset() - start;
		}
		return 0;
	}
}
