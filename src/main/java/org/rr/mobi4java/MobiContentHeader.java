package org.rr.mobi4java;

import static org.apache.commons.lang3.BooleanUtils.negate;
import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.getString;
import static org.rr.mobi4java.ByteUtils.write;
import static org.rr.mobi4java.ByteUtils.writeInt;
import static org.rr.mobi4java.ByteUtils.writeString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MobiContentHeader extends MobiContent {
	
	private static final int DEFAULT_HEADER_LENGTH = 264;
	
	/** Size of the extra bytes for the rest of mobi header*/
	private static final int MOBI_HEADER_REST = 16;

	public static enum COMPRESSION_CODE {
		NONE(1), PALM_DOC(2), HUFF_CDIC(17480);
		private final int type;
		
    private static Map<Integer, COMPRESSION_CODE> map = new HashMap<Integer, COMPRESSION_CODE>();
    
		static {
			for (COMPRESSION_CODE typeEnum : COMPRESSION_CODE.values()) {
				if(map.put(typeEnum.type, typeEnum) != null) {
					throw new IllegalArgumentException("Duplicate type " + typeEnum.type);
				}
			}
		}

		private COMPRESSION_CODE(final int type) {
			this.type = type;
		}
		
		public static COMPRESSION_CODE valueOf(int type) {
			return map.get(type);
		}

		public int getType() {
			return type;
		}
	}
	
	private int recordDataOffset;
	private int recordDataLength;
	
	private int compression;
	private int unused0;
	private int textLength;
	private int recordCount;
	private int recordSize;
	private int encryptionType;
	private int unused1;
	private int mobiType;
	private int textEncoding;
	private int uniqueID;
	private int fileVersion;
	private int orthographicIndex;
	private int inflectionIndex;
	private int indexNames;
	private int indexKeys;
	private int extraIndex0;
	private int extraIndex1;
	private int extraIndex2;
	private int extraIndex3;
	private int extraIndex4;
	private int extraIndex5;
	private int firstNonBookIndex;
	private int fullNameOffset;
	private int fullNameLength;
	private int locale;
	private int inputLanguage;
	private int outputLanguage;
	private int minVersion;
	private int firstImageIndex;
	private int huffmanRecordOffset;
	private int huffmanRecordCount;
	private int huffmanTableOffset;
	private int huffmanTableLength;
	private int exthFlags;
	private int firstContentRecordIndex = 1;
	private int lastContentRecordIndex = -1;
	private int fcisRecordIndex;
	private int fcisRecordCount;
	private int flisRecordIndex;
	private int srcsRecordIndex = -1;
	private int srcsRecordCount;
	private int flisRecordCount;
	private int extraRecordDataFlags;
	private int indxRecordIndex = -1;
	private int fragmentRecordIndex = -1;
	private int skeletonRecordIndex = -1;
	private int datpRecordIndex = -1;
	private int guideIndex = -1;
	
	private EXTHHeader exthHeader;
	private byte[] remainder;
	// end of useful data
	
	private MobiContentHeader(byte[] content, long recordDataOffset, long recordDataLength) {
		super(getBytes(content, (int) recordDataOffset, (int) recordDataLength), CONTENT_TYPE.HEADER);
		this.recordDataOffset = (int) recordDataOffset;
		this.recordDataLength = (int) recordDataLength;
	}
	
	static MobiContentHeader readMobiHeader(byte[] content, long recordDataOffset, long recordDataLength) throws IOException {
		return new MobiContentHeader(content, recordDataOffset, recordDataLength).readMobiHeader();
	}
	
	private MobiContentHeader readMobiHeader() throws IOException {
		// first 16 bytes of the PalmDOC Header followed by the MOBI header
		compression = getInt(content, 0, 2);
		unused0 = getInt(content, 2, 2);
		textLength = getInt(content, 4, 4);
		recordCount = getInt(content, 8, 2);
		recordSize = getInt(content, 10, 2);
		encryptionType = getInt(content, 12, 2);
		unused1 = getInt(content, 14, 2);
		String identifier = getString(content, 16, 4);
		if (negate(StringUtils.equals(identifier, "MOBI"))) {
			throw new IOException("Expected to find EXTH header identifier EXTH but got '" + identifier + "' instead");
		}
		int headerLength = getInt(content, 20, 4);
		mobiType = getInt(content, 24, 4);
		textEncoding = getInt(content, 28, 4);
		uniqueID = getInt(content, 32, 4);
		fileVersion = getInt(content, 36, 4);
		orthographicIndex = getInt(content, 40, 4);
		inflectionIndex = getInt(content, 44, 4);
		indexNames = getInt(content, 48, 4);
		indexKeys = getInt(content, 52, 4);
		extraIndex0 = getInt(content, 56, 4);
		extraIndex1 = getInt(content, 60, 4);
		extraIndex2 = getInt(content, 64, 4);
		extraIndex3 = getInt(content, 68, 4);
		extraIndex4 = getInt(content, 72, 4);
		extraIndex5 = getInt(content, 76, 4);
		firstNonBookIndex = getInt(content, 80, 4);
		fullNameOffset = getInt(content, 84, 4);
		fullNameLength = getInt(content, 88, 4);
		locale = getInt(content, 92, 4);
		inputLanguage = getInt(content, 96, 4);
		outputLanguage = getInt(content, 100, 4);
		minVersion = getInt(content, 104, 4);
		firstImageIndex = getInt(content, 108, 4);
		huffmanRecordOffset = getInt(content, 112, 4);
		huffmanRecordCount = getInt(content, 116, 4);
		huffmanTableOffset = getInt(content, 120, 4);
		huffmanTableLength = getInt(content, 124, 4);
		exthFlags = getInt(content, 128, 4);
		
		// optional contents
		if(headerLength >= 194) {
			firstContentRecordIndex = getInt(content, 192, 2);
		}
		if(headerLength >= 196) {
			lastContentRecordIndex = getInt(content, 194, 2);
		}
		if(headerLength >= 204) {
			fcisRecordIndex = getInt(content, 200, 4);
		}
		if(headerLength >= 208) {
			fcisRecordCount = getInt(content, 204, 4);
		}
		if(headerLength >= 212) {
			flisRecordIndex = getInt(content, 208, 4);
		}
		if(headerLength >= 216) {
			flisRecordCount = getInt(content, 212, 4);
		}
		if(headerLength >= 228) {
			srcsRecordIndex = getInt(content, 224, 4);
		}
		if(headerLength >= 232) {
			srcsRecordCount = getInt(content, 228, 4);
		}
		if(headerLength >= 244) {
			extraRecordDataFlags = getInt(content, 240, 4);
		}
		if(headerLength >= 248) {
			indxRecordIndex = getInt(content, 244, 4);
		}
		if(headerLength >= 256) {
			fragmentRecordIndex = getInt(content, 252, 4);
		}
		if(headerLength >= 264) {
			skeletonRecordIndex = getInt(content, 260, 4);
		}
		if(headerLength >= 268) {
			datpRecordIndex = getInt(content, 264, 4);
		}
		if(headerLength >= 276) {
			datpRecordIndex = getInt(content, 272, 4);
		}

		if(exthExists()) {
			exthHeader = new EXTHHeader(headerLength + MOBI_HEADER_REST).readEXTHHeader(content);
		}
		
		int remainderOffsetStart = DEFAULT_HEADER_LENGTH + MOBI_HEADER_REST + exthHeaderSize();
		int remainderOffsetLength = (int) recordDataLength - remainderOffsetStart;
		remainder = getBytes(content, remainderOffsetStart, remainderOffsetLength);
		
		return this;
	}
	
	@Override
	byte[] writeContent(OutputStream out) throws IOException {
		ByteArrayOutputStream branch = new ByteArrayOutputStream();
		TeeOutputStream tee = new TeeOutputStream(out, branch);
		
		writeInt(compression, 2, tee);
		writeInt(unused0, 2, tee);
		writeInt(textLength, 4, tee);
		writeInt(recordCount, 2, tee);
		writeInt(recordSize, 2, tee);
		writeInt(encryptionType, 2, tee);
		writeInt(unused1, 2, tee);
		writeString("MOBI", 4, tee);
		writeInt(DEFAULT_HEADER_LENGTH, 4, tee);
		writeInt(mobiType, 4, tee);
		writeInt(textEncoding, 4, tee);
		writeInt(uniqueID, 4, tee);
		writeInt(fileVersion, 4, tee);
		writeInt(orthographicIndex, 4, tee);
		writeInt(inflectionIndex, 4, tee);
		writeInt(indexNames, 4, tee);
		writeInt(indexKeys, 4, tee);
		writeInt(extraIndex0, 4, tee);
		writeInt(extraIndex1, 4, tee);
		writeInt(extraIndex2, 4, tee);
		writeInt(extraIndex3, 4, tee);
		writeInt(extraIndex4, 4, tee);
		writeInt(extraIndex5, 4, tee);
		writeInt(firstNonBookIndex, 4, tee);
		writeInt(fullNameOffset, 4, tee);
		writeInt(fullNameLength, 4, tee);
		writeInt(locale, 4, tee);
		writeInt(inputLanguage, 4, tee);
		writeInt(outputLanguage, 4, tee);
		writeInt(minVersion, 4, tee);
		writeInt(firstImageIndex, 4, tee);
		writeInt(huffmanRecordOffset, 4, tee);
		writeInt(huffmanRecordCount, 4, tee);
		writeInt(huffmanTableOffset, 4, tee);
		writeInt(huffmanTableLength, 4, tee);
		writeInt(exthFlags, 4, tee);
		
		// optional header part with 148 bytes
		write(new byte[32], tee); // Unknown 32 bytes
		writeInt(-1, 4, tee); // Unknown Use 0xFFFFFFFF
		writeInt(-1, 4, tee); // DRM-Offset: No DRM
		writeInt(0, 4, tee); // DRM-Count: No DRM
		writeInt(0, 4, tee); // DRM-Size: No DRM
		writeInt(0, 4, tee); // DRM-Flags: No DRM
		write(new byte[8], tee); // Unknown
		writeInt(firstContentRecordIndex, 2, tee);
		writeInt(lastContentRecordIndex, 2, tee);
		writeInt(1, 4, tee); // Unknown 0x00000001
		writeInt(fcisRecordIndex, 4, tee);
		writeInt(fcisRecordCount, 4, tee);
		writeInt(flisRecordIndex, 4, tee);
		writeInt(flisRecordCount, 4, tee);
		write(new byte[8], tee); // Unknown 8 bytes
		writeInt(srcsRecordIndex, 4, tee);
		writeInt(srcsRecordCount, 4, tee);
		writeInt(-1, 4, tee); // Unknown Use 0xFFFFFFFF
		writeInt(-1, 4, tee); // Unknown Use 0xFFFFFFFF
		writeInt(extraRecordDataFlags, 4, tee);
		writeInt(indxRecordIndex, 4, tee);
		writeInt(-1, 4, tee); // unknown
		writeInt(fragmentRecordIndex, 4, tee);
		writeInt(-1, 4, tee); // unknown
		writeInt(skeletonRecordIndex, 4, tee);
		writeInt(datpRecordIndex, 4, tee);
		writeInt(0, 4, tee); // Unknown
		writeInt(guideIndex, 4, tee); // Unknown
		writeInt(0, 4, tee); // Unknown

		if(exthExists()) {
			exthHeader.writeEXTHHeader(tee);
		}
		
		write(remainder, remainder.length, tee);
		
		return branch.toByteArray();
	}

	private int getHeaderLength() {
		return DEFAULT_HEADER_LENGTH + MOBI_HEADER_REST;
	}
	
	public int getSize() {
		return getHeaderLength() + exthHeaderSize() + remainder.length;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("recordDataOffset", recordDataOffset)
				.append("compression", getCompressionCode())
				.append("unused0", unused0)
				.append("textLength", textLength)
				.append("recordCount", recordCount)
				.append("recordSize", recordSize)
				.append("encryptionType", encryptionType)
				.append("unused1", unused1)
				.append("mobiType", mobiType)
				.append("textEncoding", getCharacterEncoding())
				.append("uniqueID", uniqueID)
				.append("fileVersion", fileVersion)
				.append("orthographicIndex", orthographicIndex)
				.append("inflectionIndex", inflectionIndex)
				.append("indexNames", indexNames)
				.append("indexKeys", indexKeys)
				.append("extraIndex0", extraIndex0)
				.append("extraIndex1", extraIndex1)
				.append("extraIndex2", extraIndex2)
				.append("extraIndex3", extraIndex3)
				.append("extraIndex4", extraIndex4)
				.append("extraIndex5", extraIndex5)
				.append("firstNonBookIndex", firstNonBookIndex)
				.append("fullNameOffset", fullNameOffset)
				.append("fullNameLength", fullNameLength)
				.append("locale", locale)
				.append("inputLanguage", inputLanguage)
				.append("outputLanguage", outputLanguage)
				.append("minVersion", minVersion)
				.append("firstImageIndex", firstImageIndex)
				.append("huffmanRecordOffset", huffmanRecordOffset)
				.append("huffmanRecordCount", huffmanRecordCount)
				.append("huffmanTableOffset", huffmanTableOffset)
				.append("huffmanTableLength", huffmanTableLength)
				.append("exthFlags", exthFlags)
		.toString();
	}

	private boolean exthExists() {
		return (exthFlags & 0x40) != 0;
	}

	
	public List<EXTHRecord> getEXTHRecords() {
		return (exthHeader == null) ? (new LinkedList<EXTHRecord>()) : exthHeader.getRecordList();
	}

	public List<EXTHRecord> getEXTHRecords(EXTHRecord.RECORD_TYPE type) {
		List<EXTHRecord> resultRecords = new ArrayList<>();
		for (EXTHRecord record : getEXTHRecords()) {
			if(record.getRecordType() == type) {
				resultRecords.add(record);
			}
		}
		return resultRecords;
	}

	public void addEXTHRecord(EXTHRecord exthRecord) {
		if(exthHeader == null) {
			exthHeader = new EXTHHeader(getHeaderLength());
		}
		exthHeader.addRecord(exthRecord);
	}

	public int getTextEncoding() {
		return textEncoding;
	}

	public void setTextEncoding(int textEncoding) {
		this.textEncoding = textEncoding;
	}

	String getCharacterEncoding() {
		return getCharacterEncoding(textEncoding);
	}

	public String getFullName() {
		int offset = fullNameOffset;
		int length = fullNameLength;
		byte[] fullName = getBytes(content, offset, length);
		return getString(fullName, getCharacterEncoding());
	}

	public void setFullName(String name) throws UnsupportedEncodingException {
		byte[] fullNameBytes = getBytes(name, getCharacterEncoding());
		fullNameLength = fullNameBytes.length;

		// the string must be terminated by 2 null bytes
		// then this must end in a 4-byte boundary
		int padding = (fullNameLength + 2) % 4;
		if (padding != 0) {
			padding = 4 - padding;
		}
		padding += 2;

		byte[] buffer = new byte[fullNameLength + padding];
		System.arraycopy(fullNameBytes, 0, buffer, 0, fullNameLength);
		for (int i = fullNameLength; i < buffer.length; i++) {
			buffer[i] = 0;
		}

		remainder = buffer;
		// adjust fullNameOffset
		fullNameOffset = DEFAULT_HEADER_LENGTH + MOBI_HEADER_REST + exthHeaderSize(); 
	}
	
	private int exthHeaderSize() {
		return (exthHeader == null) ? 0 : exthHeader.size();
	}

	public int getLocale() {
		return locale;
	}

	public void setLocale(int locale) {
		this.locale = locale;
	}

	public int getInputLanguage() {
		return inputLanguage;
	}

	public void setInputLanguage(int inputLanguage) {
		this.inputLanguage = inputLanguage;
	}

	public int getOutputLanguage() {
		return outputLanguage;
	}

	public void setOutputLanguage(int outputLanguage) {
		this.outputLanguage = outputLanguage;
	}

	public int getFirstImageIndex() {
		return firstImageIndex;
	}
	
	public void setFirstImageIndex(int firstImageIndex) {
		this.firstImageIndex = firstImageIndex;
	}
	
	public COMPRESSION_CODE getCompressionCode() {
		return COMPRESSION_CODE.valueOf(compression);
	}

	public void setCompressionCode(COMPRESSION_CODE compression) {
		this.compression = compression.getType();
	}

	/**
	 * @return First record number (starting with 0) that's not the book's text.
	 */
	public int getFirstNonBookIndex() {
		return firstNonBookIndex;
	}
	
	/**
	 * @param firstNonBookIndex First record number (starting with 0) that's not the book's text. Also excluding the book's index.
	 */
	public void setFirstNonBookIndex(int firstNonBookIndex) {
		this.firstNonBookIndex = firstNonBookIndex;
	}

	/**
	 * Number of the first text record which is usually 1.
	 * @return The first content record.
	 */
	public int getFirstContentRecordIndex() {
		return firstContentRecordIndex;
	}
	
	/**
	 * Number of the first text record or -1 if no one is defined.
	 * @return The last content record.
	 */
	public int getLastContentRecordIndex() {
		return lastContentRecordIndex;		
	}

	/**
	 * @return The uncompressed length of the entire text of the book. 
	 */
	public int getTextLength() {
		return textLength;
	}

	/**
	 * @param textLength The uncompressed length of the entire text of the book.
	 */
	public void setTextLength(int textLength) {
		this.textLength = textLength;
	}

	/**
	 * @return The number of PDB records used for the text of the book. This is necessarily the same number of mobi content entries containing text.
	 */
	public int getRecordCount() {
		return recordCount;
	}

	/**
	 * @param recordCount The number of PDB records used for the text of the book. This is necessarily the same number of mobi content entries
	 *          containing text.
	 */
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	/**
	 * @return Maximum size of each record containing text. This is usually 4096.
	 */
	public int getRecordSize() {
		return recordSize;
	}

	/**
	 * @param recordSize Maximum size of each record containing text. This is usually 4096.
	 */
	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}

	public int getFcisRecordIndex() {
		return fcisRecordIndex;
	}

	public void setFcisRecordIndex(int fcisRecordNumber) {
		this.fcisRecordIndex = fcisRecordNumber;
	}

	public int getFlisRecordIndex() {
		return flisRecordIndex;
	}

	public void setFlisRecordIndex(int flisRecordNumber) {
		this.flisRecordIndex = flisRecordNumber;
	}

	public int getSrcsRecordIndex() {
		return srcsRecordIndex;
	}

	public void setSrcsRecordIndex(int srcsRecordNumber) {
		this.srcsRecordIndex = srcsRecordNumber;
	}

	public int getFragmentRecordIndex() {
		return fragmentRecordIndex;
	}

	public void setFragmentRecordIndex(int fragmentIndex) {
		this.fragmentRecordIndex = fragmentIndex;
	}

	public int getSceletonRecordIndex() {
		return skeletonRecordIndex;
	}

	public void setSceletonRecordIndex(int sceletonIndex) {
		this.skeletonRecordIndex = sceletonIndex;
	}

	public int getDatpRecordIndex() {
		return datpRecordIndex;
	}

	public void setDatpRecordIndex(int datpIndex) {
		this.datpRecordIndex = datpIndex;
	}

	public int getHuffmanRecordOffset() {
		return huffmanRecordOffset;
	}

	public void setHuffmanRecordOffset(int huffmanRecordOffset) {
		this.huffmanRecordOffset = huffmanRecordOffset;
	}

	public int getHuffmanRecordCount() {
		return huffmanRecordCount;
	}

	public void setHuffmanRecordCount(int huffmanRecordCount) {
		this.huffmanRecordCount = huffmanRecordCount;
	}

}
