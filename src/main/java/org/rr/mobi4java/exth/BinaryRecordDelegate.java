package org.rr.mobi4java.exth;

import static org.rr.mobi4java.ByteUtils.dumpByteArray;

import org.rr.mobi4java.EXTHRecord;
import org.rr.mobi4java.MobiDocument;

public class BinaryRecordDelegate implements RecordDelegate {
	
	private EXTHRecord record;

	public BinaryRecordDelegate(EXTHRecord record) {
		this.record = record;
	}

	/**
	 * Get the data of this {@link EXTHRecord} instance as string .
	 * 
	 * @param encoding The character encoding for the result string. Use {@link MobiDocument#getCharacterEncoding()}.
	 * @return A new string instance from the data of this {@link EXTHRecord} instance.
	 */
	public String getAsString() {
		return dumpByteArray(record.getData());
	}

	@Override
	public EXTHRecord getRecord() {
		return record;
	}
}
