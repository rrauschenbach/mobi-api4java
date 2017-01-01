package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.startsWith;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.Range;
import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.MobiContent.CONTENT_TYPE;

class MobiContentRecordFactory {
	
	static MobiContent createContentRecord(byte[] mobiContent) {
		return new MobiContent(mobiContent, CONTENT_TYPE.CONTENT);
	}

	static MobiContent createCoverRecord(byte[] mobiContent) {
		return new MobiContent(mobiContent, CONTENT_TYPE.COVER);
	}
	
	static MobiContent createThumbnailRecord(byte[] mobiContent) {
		return new MobiContent(mobiContent, CONTENT_TYPE.THUMBNAIL);
	}
	
	static MobiContent createEndOfTextRecord() {
		return new MobiContent(new byte[] {0,0}, CONTENT_TYPE.END_OF_TEXT);
	}
	
	static MobiContent readContent(byte[] mobiData, CONTENT_TYPE type, long recordDataOffset, long recordDataLength) throws IOException {
		byte[] mobiContent = getBytes(mobiData, (int) recordDataOffset, (int) recordDataLength);
		if(type == CONTENT_TYPE.INDEX) {
			return new MobiContentIndex(mobiContent);
		}
		return create(mobiContent, type);
	}
	
	private static MobiContent create(byte[] mobiContent, CONTENT_TYPE type) {
		return new MobiContent(mobiContent, type);
	}
	
	static CONTENT_TYPE evaluateType(PDBHeader pdbHeader, MobiContentHeader mobiHeader, int index, byte[] mobiData, long recordDataOffset, long recordDataLength) {
		byte[] content = getBytes(mobiData, (int) recordDataOffset, (int) recordDataLength);
		if(isContentRecord(mobiHeader, index)) {
			return CONTENT_TYPE.CONTENT;
		} else if(isIndexRecord(content)) {
			return CONTENT_TYPE.INDEX;
		} else if(isEndOfTextRecord(mobiHeader, index, content)) {
			return CONTENT_TYPE.END_OF_TEXT;
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
		} else if(isCover(mobiHeader, index)) {
			return CONTENT_TYPE.COVER;
		} else if(isThumbnail(mobiHeader, index)) {
			return CONTENT_TYPE.THUMBNAIL;
		} else if(isImage(content)) {
			return CONTENT_TYPE.IMAGE;
		}
		return CONTENT_TYPE.UNKNOWN;
	}
	
	private static boolean isContentRecord(MobiContentHeader mobiHeader, int index) {
		return Range.<Integer>between(1, mobiHeader.getRecordCount()).contains(index);
	}

	/**
	 * In some cases, there are a 2-zero-byte record after the text records in a file.
	 */
	private static boolean isEndOfTextRecord(MobiContentHeader mobiHeader, int index, byte[] content) {
		return mobiHeader.getRecordCount() + 1 == index &&
				content.length == 2 && 
				startsWith(content, new byte[] {0x00, 0x00});
	}

	private static boolean isThumbnail(MobiContentHeader mobiHeader, int index) {
		return isOfTypeAtOffset(mobiHeader, RECORD_TYPE.THUMBNAIL_OFFSET, index);
	}

	private static boolean isCover(MobiContentHeader mobiHeader, int index) {
		return isOfTypeAtOffset(mobiHeader, RECORD_TYPE.COVER_OFFSET, index);
	}

	private static boolean isOfTypeAtOffset(MobiContentHeader mobiHeader, RECORD_TYPE type, int index) {
		List<EXTHRecord> exthRecords = mobiHeader.getEXTHRecords(type);
		for (EXTHRecord exthRecord : exthRecords) {
			if(mobiHeader.getFirstImageIndex() + exthRecord.getIntData() == index) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tries to guess what the image type (if any) of a file based on the file's "magic numbers," the first bytes of the file.
	 *
	 * @param data byte array to be tested for image data.
	 * @return <code>true</code> if an image was detected and <code>false</code> otherwise.
	 */
	private static boolean isImage(byte[] data) {
		if(data.length > 4) {
			int b1 = getInt(data, 0, 1);
			int b2 = getInt(data, 1, 1);
			int b3 = getInt(data, 2, 1);
			int b4 = getInt(data, 3, 1);
	
			if (b1 == 0x47 && b2 == 0x49) {
				return true; // image/gif
			} else if (b1 == 0x89 && b2 == 0x50) {
				return true; // image/png
			} else if (b1 == 0xFF && b2 == 0xD8) {
				return true; // image/jpeg
			} else if (b1 == 0xFF && b2 == 0xD9) {
				return true; // image/jpeg
			} else if (b1 == 0x42 && b2 == 0x4D) {
				return true; // image/bmp
			} else if (b1 == 0x4D && b2 == 0x4D) {
				return true; // Motorola byte order TIFF
			} else if (b1 == 0x49 && b2 == 0x49) {
				return true; // Intel byte order TIFF
			} else if (b1 == 0x38 && b2 == 0x42) {
				return true; // image/psd
			} else if (b1 == 0x50 && b2 == 0x31) {
				return true; // image/pbm
			} else if (b1 == 0x50 && b2 == 0x34) {
				return true; // image/pbm
			} else if (b1 == 0x50 && b2 == 0x32) {
				return true; // image/pgm
			} else if (b1 == 0x50 && b2 == 0x35) {
				return true; // image/pgm
			} else if (b1 == 0x50 && b2 == 0x33) {
				return true; // image/pgm
			} else if (b1 == 0x50 && b2 == 0x36) {
				return true; // image/pgm
			} else if (b1 == 0x97 && b2 == 0x4A && b3 == 0x42 && b4 == 0x32) {
				return true; // image/x-jbig2
			}
		}

		return false;
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
