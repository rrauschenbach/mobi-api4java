package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.startsWith;

import java.io.IOException;

import org.apache.commons.lang3.Range;
import org.rr.mobi4java.MobiContent.CONTENT_TYPE;

public class MobiContentFactory {
	
	public static MobiContent createContentRecord(byte[] mobiContent) {
		return new MobiContent(mobiContent, CONTENT_TYPE.CONTENT);
	}

	public static MobiContent createCoverRecord(byte[] mobiContent) {
		return new MobiContent(mobiContent, CONTENT_TYPE.UNKNOWN);
	}
	
	public static MobiContent createEndOfTextRecord() {
		return new MobiContent(new byte[] {0,0}, CONTENT_TYPE.END_OF_TEXT);
	}
	
	static MobiContent create(byte[] mobiContent, CONTENT_TYPE type) {
		return new MobiContent(mobiContent, type);
	}
	
	public static MobiContent readContent(byte[] mobiData, CONTENT_TYPE type, long recordDataOffset, long recordDataLength) throws IOException {
		byte[] mobiContent = getBytes(mobiData, (int) recordDataOffset, (int) recordDataLength);
		return create(mobiContent, type);
	}
	
	public static CONTENT_TYPE evaluateType(PDBHeader pdbHeader, MobiHeader mobiHeader, int index, byte[] mobiData, long recordDataOffset, long recordDataLength) {
		byte[] content = getBytes(mobiData, (int) recordDataOffset, (int) recordDataLength);
		if(Range.<Integer>between(1, mobiHeader.getRecordCount()).contains(index)) {
			return CONTENT_TYPE.CONTENT;
		} else if(isEndOfTextRecord(mobiHeader, index, content)) {
			return CONTENT_TYPE.END_OF_TEXT;
		} else if(isIndexRecord(content)) {
			return CONTENT_TYPE.INDEX;
		} else if(isTagxRecord(content)) {
			return CONTENT_TYPE.TAGX;
		} else if(isFlisRecord(content)) {
			return CONTENT_TYPE.FLIS;
		} else if(isFcisRecord(content)) {
			return CONTENT_TYPE.FCIS;
		} else if(isFdstRecord(content)) {
			return CONTENT_TYPE.FDST;
		} else if(isDatpRecord(content)) {
			return CONTENT_TYPE.DATP;
		} else if(isSrcsRecord(content)) {
			return CONTENT_TYPE.SRCS;
		} else if(isCmetRecord(content)) {
			return CONTENT_TYPE.CMET;
		} else if(isAudiRecord(content)) {
			return CONTENT_TYPE.AUDI;
		} else if(isVideRecord(content)) {
			return CONTENT_TYPE.VIDE;
		}
		return CONTENT_TYPE.UNKNOWN;
	}

	/**
	 * In some cases, there are a 2-zero-byte record after the text records in a file.
	 */
	private static boolean isEndOfTextRecord(MobiHeader mobiHeader, int index, byte[] content) {
		return mobiHeader.getRecordCount() + 1 == index &&
				content.length == 2 && 
				startsWith(content, new byte[] {0x00, 0x00});
	}

	/**
	 * Book index record which contains the meta data of the index.
	 */
	private static boolean isIndexRecord(byte[] content) {
		return startsWith(content, "INDX".getBytes());
	}
	
	private static boolean isTagxRecord(byte[] content) {
		return startsWith(content, "TAGX".getBytes());
	}
	
	private static boolean isFlisRecord(byte[] content) {
		return startsWith(content, "FLIS".getBytes());
	}
	
	private static boolean isFcisRecord(byte[] content) {
		return startsWith(content, "FCIS".getBytes());
	}
	
	private static boolean isFdstRecord(byte[] content) {
		return startsWith(content, "FDST".getBytes());
	}
	
	private static boolean isDatpRecord(byte[] content) {
		return startsWith(content, "DATP".getBytes());
	}
	
	/**
	 * A SRCS record is a record whose content is a zip archive of all source files (i.e., .opf, .ncx, .htm, .jpg, ...)
	 */
	private static boolean isSrcsRecord(byte[] content) {
		return startsWith(content, "SRCS".getBytes());
	}
	
	/**
	 * A CMET record is a record whose content is the output of the compilation operation, and perhaps extra info.
	 */
	private static boolean isCmetRecord(byte[] content) {
		return startsWith(content, "CMET".getBytes());
	}
	
	/**
	 * kindlegen supports embedded audio and video for some Kindle platforms. Each media file is stored in a separate AUDI (audio) or VIDE
	 * (video) record
	 */
	private static boolean isAudiRecord(byte[] content) {
		return startsWith(content, "AUDI".getBytes());
	}
	
	/**
	 * kindlegen supports embedded audio and video for some Kindle platforms. Each media file is stored in a separate AUDI (audio) or VIDE
	 * (video) record
	 */
	private static boolean isVideRecord(byte[] content) {
		return startsWith(content, "VIDE".getBytes());
	}
}
