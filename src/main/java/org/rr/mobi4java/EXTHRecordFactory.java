package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getInt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.exth.ASINRecordDelegate;
import org.rr.mobi4java.exth.DateRecordDelegate;
import org.rr.mobi4java.exth.ISBNRecordDelegate;
import org.rr.mobi4java.exth.LanguageRecordDelegate;
import org.rr.mobi4java.exth.RecordDelegate;
import org.rr.mobi4java.exth.StringRecordDelegate;

/**
 * Class providing factory methods to create new {@link EXTHRecord} or {@link RecordDelegate} derivation instances which can be added to a
 * document using {@link MobiMetaData#addEXTHRecord(org.rr.mobi4java.exth.RecordDelegate)}.
 */
public class EXTHRecordFactory {
	
	public static EXTHRecord createEXTHRecord(RECORD_TYPE recordType, byte[] recordData) {
		EXTHRecord record = createEXTHRecord(recordType);
		record.setRecordType(recordType);
		record.setData(recordData);
		return record;
	}
	
	public static EXTHRecord createEXTHRecord(RECORD_TYPE recordType) {
		return new EXTHRecord(recordType.getType());
	}
	
	/**
	 * Create a new asin record with the given asin as value.
	 * 
	 * @param asinCode The asin code which must be a member of the list returned with
	 *          {@link EXTHasinRecordDelegate#getasinCodes()}.
	 * @return A new asin record instance.
	 * @throws IllegalArgumentException if the given code is not valid.
	 */
	public static ASINRecordDelegate createASINRecord(String asin) {
		assertNotNull(RECORD_TYPE.ASIN, asin);
		
		EXTHRecord record = createEXTHRecord(RECORD_TYPE.ASIN);
		ASINRecordDelegate recordDelegate = new ASINRecordDelegate(record);
		recordDelegate.setASIN(asin);
		return recordDelegate;
	}
	
	/**
	 * Create a new isbn record with the given isbn as value.
	 * 
	 * @param isbnCode The isbn code which must be a member of the list returned with
	 *          {@link EXTHisbnRecordDelegate#getisbnCodes()}.
	 * @return A new isbn record instance.
	 * @throws IllegalArgumentException if the given code is not valid.
	 */
	public static ISBNRecordDelegate createISBNRecord(String isbn) {
		assertNotNull(RECORD_TYPE.ISBN, isbn);
		
		EXTHRecord record = createEXTHRecord(RECORD_TYPE.ISBN);
		ISBNRecordDelegate recordDelegate = new ISBNRecordDelegate(record);
		recordDelegate.setISBN(isbn);
		return recordDelegate;
	}
	
	/**
	 * Create a new language record with the given language as value.
	 * 
	 * @param languageCode The language code which must be a member of the list returned with
	 *          {@link LanguageRecordDelegate#getLanguageCodes()}.
	 * @return A new language record instance.
	 * @throws IllegalArgumentException if the given code is not valid.
	 */
	public static LanguageRecordDelegate createLanguageRecord(String languageCode) {
		assertNotNull(RECORD_TYPE.LANGUAGE, languageCode);

		EXTHRecord record = createEXTHRecord(RECORD_TYPE.LANGUAGE);
		LanguageRecordDelegate recordDelegate = new LanguageRecordDelegate(record);
		recordDelegate.setLanguageCode(languageCode);
		return recordDelegate;
	}
	
	/**
	 * Create a new string record for the given record type and the given string as value. Please note that only some record types fits to a string type.
	 * 
	 * @param recordType The record type for the result {@link StringRecordDelegate}.
	 * @param str The string value of the new record.
	 * @param encoding The encoding. Use {@link MobiDocument#getCharacterEncoding()} for the right document encoding.
	 * @return A new string record instance.
	 * @throws UnsupportedEncodingException if the given encoding is not supported by the java virtual machine.
	 */
	public static StringRecordDelegate createStringRecord(RECORD_TYPE recordType, String str, String encoding) throws UnsupportedEncodingException {
		assertNotNull(recordType, str);
		
		EXTHRecord record = createEXTHRecord(recordType);
		StringRecordDelegate recordDelegate = new StringRecordDelegate(record);
		recordDelegate.setStringData(str, encoding);
		return recordDelegate;
	}
	
	/**
	 * Create a new date record for the given record type. Please note that only some record types fits to a date type.
	 * 
	 * @param recordType The record type for the result {@link DateRecordDelegate}.
	 * @param date The date value of the new record.
	 * @return A new date record instance.
	 */
	public static DateRecordDelegate createDateRecord(RECORD_TYPE recordType, Date date) {
		assertNotNull(recordType, date);
		
		EXTHRecord record = createEXTHRecord(recordType);
		DateRecordDelegate recordDelegate = new DateRecordDelegate(record);
		recordDelegate.setDateData(date);
		return recordDelegate;
	}

	static EXTHRecord readEXTHRecord(byte[] mobiHeader, int offset) throws IOException {
		EXTHRecord record = createEXTHRecord(RECORD_TYPE.valueOf(getInt(mobiHeader, offset + 0, 4)));
		return record.readEXTHRecord(mobiHeader, offset);
	}
	
	private static void assertNotNull(RECORD_TYPE type, Object value) {
		if(value == null) {
			throw new IllegalArgumentException("The record type " + type + " must not be null.");
		}
	}
}
