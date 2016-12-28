package org.rr.mobi4java.exth;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getString;

import java.io.UnsupportedEncodingException;

import org.rr.mobi4java.EXTHRecord;
import org.rr.mobi4java.MobiDocument;

public class StringRecordDelegate implements RecordDelegate {
	
	private EXTHRecord record;

	public StringRecordDelegate(EXTHRecord record) {
		this.record = record;
	}

	/**
	 * Get the data of this {@link EXTHRecord} instance as string.
	 * 
	 * @param encoding The character encoding for the result string. Use {@link MobiDocument#getCharacterEncoding()}.
	 * @return A new string instance from the data of this {@link EXTHRecord} instance.
	 */
	public String getAsString(String encoding) {
		return getString(record.getData(), encoding);
	}

	/**
	 * Set a string as value to this {@link EXTHRecord} instance.
	 * 
	 * @param str The string used as value for this {@link EXTHRecord} instance.
	 * @param encoding The character encoding for the given string. Use {@link MobiDocument#getCharacterEncoding()}.
	 * @throws UnsupportedEncodingException if the given encoding is not supported by the java virtual machine.
	 * @throws IllegalArgumentException if the given value is <code>null</code>.
	 */
	public void setStringData(String str, String encoding) throws UnsupportedEncodingException {
		if(str == null) {
			throw new IllegalArgumentException("Value must not be null.");
		}
		
		record.setData(getBytes(defaultString(str), encoding));
	}

	@Override
	public EXTHRecord getRecord() {
		return record;
	}
}
