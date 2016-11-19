package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.startsWith;
import static org.rr.mobi4java.ByteUtils.write;

import java.io.IOException;
import java.io.OutputStream;

public class MobiContent {
	
	public enum TYPE {
		CONTENT, INDEX, TAGX, FLIS, FCIS, FDST, DATP, SRCS, CMET, AUDI, VIDE, END_OF_TEXT, UNKNOWN
	};
	
	byte[] content;
	
	MobiContent(byte[] content) {
		this.content = content;
	}
	
	static MobiContent create(byte[] mobiContent) {
		return new MobiContent(mobiContent);
	}
	
	static MobiContent readContent(byte[] mobiData, long recordDataOffset, long recordDataLength) throws IOException {
		byte[] mobiContent = getBytes(mobiData, (int) recordDataOffset, (int) recordDataLength);
		return create(mobiContent);
	}

	byte[] writeContent(OutputStream out) throws IOException {
		write(content, content.length, out);
		return content;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public int getSize() {
		return content.length;
	}

	public TYPE guessDataType() {
		if(isIndexRecord(content)) {
			return TYPE.INDEX;
		} else if(isEndOfTextRecord(content)) {
			return TYPE.END_OF_TEXT;
		} else if(isTagxRecord(content)) {
			return TYPE.TAGX;
		} else if(isFlisRecord(content)) {
			return TYPE.FLIS;
		} else if(isFcisRecord(content)) {
			return TYPE.FCIS;
		} else if(isFdstRecord(content)) {
			return TYPE.FDST;
		} else if(isDatpRecord(content)) {
			return TYPE.DATP;
		} else if(isSrcsRecord(content)) {
			return TYPE.SRCS;
		} else if(isCmetRecord(content)) {
			return TYPE.CMET;
		} else if(isAudiRecord(content)) {
			return TYPE.AUDI;
		} else if(isVideRecord(content)) {
			return TYPE.VIDE;
		}
		return TYPE.UNKNOWN;
	}

	public TYPE guessContentType() {
		if(isIndexRecord(content)) {
			return TYPE.INDEX;
		}
		return TYPE.CONTENT;
	}

	/**
	 * In some cases, there are a 2-zero-byte record after the text records in a file.
	 */
	private static boolean isEndOfTextRecord(byte[] content) {
		return content.length == 2 && startsWith(content, new byte[] {0x00, 0x00});
	}

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
