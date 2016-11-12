package org.rr.mobi4java;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class MobiReader {

	public MobiDocument read(File file) throws FileNotFoundException, IOException {
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			return read(in);
		}
	}

	public MobiDocument read(InputStream in) throws IOException {
		byte[] mobiData = IOUtils.toByteArray(in);
		PDBHeader pdbHeader = readPDBHeader(mobiData);
		MobiHeader mobiHeader = readMobiHeader(pdbHeader, mobiData);
		List<MobiContent> mobiContent = readMobiContent(pdbHeader, mobiHeader, mobiData);
		return new MobiDocument(pdbHeader, mobiHeader, mobiContent);
	}

	private PDBHeader readPDBHeader(byte[] mobiData) {
		return PDBHeader.readHeader(mobiData);
	}

	private MobiHeader readMobiHeader(PDBHeader pdbHeader, byte[] mobiData) throws IOException {
		return MobiHeader.readMobiHeader(mobiData, getRecordDataOffset(pdbHeader, 0), getRecordDataLength(pdbHeader, 0));
	}

	private List<MobiContent> readMobiContent(PDBHeader pdbHeader, MobiHeader mobiHeader, byte[] mobiData) throws IOException {
		List<MobiContent> mobiContents = new ArrayList<>();
		mobiContents.add(mobiHeader);
		int recordCount = pdbHeader.getRecordCount();
		for (int i = 1; i < recordCount; i++) {
			MobiContent mobiContent = MobiContent.readContent(mobiData, getRecordDataOffset(pdbHeader, i),
						getRecordDataLength(pdbHeader, i));
			mobiContents.add(mobiContent);
		}
		return mobiContents;
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
