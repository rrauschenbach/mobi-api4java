package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.write;
import static org.rr.mobi4java.ByteUtils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MobiIndex extends MobiContent {
	
	private String identifier;
	
	private int headerLength;
	
	private int indexType;
	
	private int unknown1;
	
	private int unknown2;
	
	private int idtxStart;
	
	private int indexCount;
	
	private int indexEncoding;
	
	private int indexLanguage;
	
	private int totalIndexCount;
	
	private int ordtStart;
	
	private int ligtStart;
	
	private byte[] restOfMobiIndex;

	MobiIndex(byte[] content) {
		super(content);
		readMobiIndex();
	}
	
	private void readMobiIndex() {
		identifier = getString(content, 0, 4);
		headerLength = getInt(content, 4, 4);
		indexType = getInt(content, 8, 4);
		unknown1 = getInt(content, 12, 4);
		unknown2 = getInt(content, 16, 4);
		idtxStart = getInt(content, 20, 4);
		indexCount = getInt(content, 24, 4);
		indexEncoding = getInt(content, 28, 4);
		indexLanguage = getInt(content, 32, 4);
		totalIndexCount = getInt(content, 36, 4);
		ordtStart = getInt(content, 40, 4);
		ligtStart = getInt(content, 44, 4);
		restOfMobiIndex = getBytes(content, 48);
	}
	
	@Override
	byte[] writeContent(OutputStream out) throws IOException {
		ByteArrayOutputStream branch = new ByteArrayOutputStream();
		TeeOutputStream tee = new TeeOutputStream(out, branch);
		writeString(identifier, 4, tee);
		writeInt(headerLength, 4, tee);
		writeInt(indexType, 4, tee);
		writeInt(unknown1, 4, tee);
		writeInt(unknown2, 4, tee);
		writeInt(idtxStart, 4, tee);
		writeInt(indexCount, 4, tee);
		writeInt(indexEncoding, 4, tee);
		writeInt(indexLanguage, 4, tee);
		writeInt(totalIndexCount, 4, tee);
		writeInt(ordtStart, 4, tee);
		writeInt(ligtStart, 4, tee);
		write(restOfMobiIndex, out);
		return branch.toByteArray();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("identifier", identifier)
				.append("headerLength", headerLength)
				.append("indexType", indexType)
				.append("unknown1", unknown1)
				.append("unknown2", unknown2)
				.append("idtxStart", idtxStart)
				.append("indexCount", indexCount)
				.append("indexEncoding", getCharacterEncoding())
				.append("indexLanguage", indexLanguage)
				.append("totalIndexCount", totalIndexCount)
				.append("ordtStart", ordtStart)
				.append("ligtStart", ligtStart)
		.toString();
	}

	public int getIndexType() {
		return indexType;
	}

	public int getIdtxStart() {
		return idtxStart;
	}

	public int getIndexCount() {
		return indexCount;
	}

	public String getCharacterEncoding() {
		return getCharacterEncoding(indexEncoding);
	}

	/**
	 * @return the language code of the index
	 */
	public int getIndexLanguage() {
		return indexLanguage;
	}

	/**
	 * @return the number of index entries
	 */
	public int getTotalIndexCount() {
		return totalIndexCount;
	}

	/**
	 * @return The offset to the ORDT section.
	 */
	public int getOrdtStart() {
		return ordtStart;
	}

	/**
	 * @return The offset to the LIGT section.
	 */
	public int getLigtStart() {
		return ligtStart;
	}

}
