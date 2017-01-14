package org.rr.mobi4java;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.MobiContent.CONTENT_TYPE;
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

	static int getTextContentStartIndex(MobiContentHeader mobiHeader) {
		int firstContentIndex = mobiHeader.getFirstContentRecordIndex();
  	if(firstContentIndex <= 0) {
  		firstContentIndex = 1; // text starts usually with at index 1
  	}
		return firstContentIndex;
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
	
	static List<MobiContent> findContentsByType(List<MobiContent> contents, CONTENT_TYPE type) {
		List<MobiContent> result = new ArrayList<>();
		for (MobiContent content : contents) {
			if(content.getType() == type) {
				result.add(content);
			}
		}
		return result;
	}

	static int findFirstContentsIndexByType(List<MobiContent> contents, CONTENT_TYPE type) {
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).getType() == type) {
				return i;
			}
		}
		return -1;
	}
	
	static List<Integer> findAllContentsIndexByType(List<MobiContent> contents, CONTENT_TYPE type) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i).getType() == type) {
				result.add(i);
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
