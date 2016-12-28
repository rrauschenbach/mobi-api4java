package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getInt;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.exth.DateRecordDelegate;
import org.rr.mobi4java.exth.StringRecordDelegate;

class MobiUtils {
	
	/**
	 * Remove the replacement character which is used by utf-8 to display an unknown character (often a black diamond with a white question
	 * mark or an empty square box).
	 * 
	 * @param str The input string which should be cleaned.
	 * @return The cleaned input string.
	 */
	static String removeUtfReplacementCharacter(String str) {
		return StringUtils.remove(str, "\uFFFD");
	}
	
	/**
	 * Remove those bytes which are sometimes found in mobi html markup but did not fit into a string.
	 * 
	 * @param bytes The bytes which should be searched for bytes which did not belong to a string.
	 * @return A new byte array which was cleaned from non string fitting bytes.
	 */
	static byte[] removeRandomBytes(byte[] bytes) {
		byte[] searchBytes = new byte[] { 0x00, 0x14, 0x15, 0x19, 0x1c, 0x1d, 0x12, 0x13, 0x08, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
		ByteArrayOutputStream out = new ByteArrayOutputStream(searchBytes.length);
		for (byte b : bytes) {
			if(Arrays.binarySearch(searchBytes, b) < 0) {
				out.write(b);
			}
		}
		return out.toByteArray();
	}
	
	/**
	 * Tries to guess what the image type (if any) of a file based on the file's "magic numbers," the first bytes of the file.
	 *
	 * @param data byte array to be tested for image data.
	 * @return <code>true</code> if an image was detected and <code>false</code> otherwise.
	 */
	static boolean isImage(byte[] data) {
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

	private static int getLastContentIndex(MobiHeader mobiHeader, PDBHeader pdbHeader) {
		int lastContentIndex = mobiHeader.getLastContentRecordNumber();
  	if(lastContentIndex <= 0) {
  		lastContentIndex = pdbHeader.getRecordCount();
  	}
		return lastContentIndex;
	}

	static int getTextContentStartIndex(MobiHeader mobiHeader) {
		int firstContentIndex = mobiHeader.getFirstContentRecordNumber();
  	if(firstContentIndex <= 0) {
  		firstContentIndex = 1; // text starts usually with at index 1
  	}
		return firstContentIndex;
	}
	
  static int guessTextContentEndIndex(MobiHeader mobiHeader, PDBHeader pdbHeader) {
  	int end = MobiUtils.getLastContentIndex(mobiHeader, pdbHeader);
  	if(mobiHeader.getFirstImageIndex() - 1 > 0) {
  		end = Math.min(end, mobiHeader.getFirstImageIndex() - 1);
  	}
  	if(mobiHeader.getFirstNonBookIndex() -1 > 0 && mobiHeader.getFirstNonBookIndex() -1 < end) {
  		end = mobiHeader.getFirstNonBookIndex() - 1;
  	}
  	return end;
  }
  
	static List<EXTHRecord> findRecordsByType(List<EXTHRecord> records, RECORD_TYPE type) {
		List<EXTHRecord> found = new ArrayList<>();
		for (EXTHRecord exthRecord : records) {
			if(exthRecord.getRecordType() == type) {
				found.add(exthRecord);
			}
		}
		return found;
	}
	
	static List<MobiContent> findIndexRecords(List<MobiContent> contents) {
		List<MobiContent> result = new ArrayList<>();
		for (MobiContent content : contents) {
			if(content.guessContentType() == MobiContent.TYPE.INDEX) {
				result.add(content);
			}
		}
		return result;
	}
	
	static List<StringRecordDelegate> createStringRecords(List<EXTHRecord> records, RECORD_TYPE type) {
		List<EXTHRecord> stringRecords = MobiUtils.findRecordsByType(records, type);
		List<StringRecordDelegate> stringRecordDelegates = new ArrayList<>(stringRecords.size());
		for (EXTHRecord exthRecord : stringRecords) {
			stringRecordDelegates.add(new StringRecordDelegate(exthRecord));
		}
		return stringRecordDelegates;
	}
	
	static List<DateRecordDelegate> createDateRecords(List<EXTHRecord> records, RECORD_TYPE type) {
		List<EXTHRecord> stringRecords = MobiUtils.findRecordsByType(records, type);
		List<DateRecordDelegate> dateRecordDelegates = new ArrayList<>(stringRecords.size());
		for (EXTHRecord exthRecord : stringRecords) {
			dateRecordDelegates.add(new DateRecordDelegate(exthRecord));
		}
		return dateRecordDelegates;
	}
	
}
