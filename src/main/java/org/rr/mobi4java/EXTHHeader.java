package org.rr.mobi4java;

import static org.apache.commons.lang3.BooleanUtils.negate;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getString;
import static org.rr.mobi4java.ByteUtils.writeInt;
import static org.rr.mobi4java.ByteUtils.writeString;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class EXTHHeader {
	
	private final int exthHeaderOffset;

	private List<EXTHRecord> recordList;

	public EXTHHeader(int exthHeaderOffset) {
		this.exthHeaderOffset = exthHeaderOffset;
		this.recordList = new ArrayList<>();
	}

	EXTHHeader readEXTHHeader(byte[] mobiHeader) throws IOException {
		String identifier = getString(getBytes(mobiHeader, getOffset(0), 4));
		int recordCount = getInt(getBytes(mobiHeader, getOffset(8), 4));

		if (negate(StringUtils.equals(identifier, "EXTH"))) {
			throw new IOException("Expected to find EXTH header identifier EXTH but got '" + identifier + "' instead");
		}

		recordList = new ArrayList<>(recordCount);
		for (int i = 0; i < recordCount; i++) {
			EXTHRecord exthRecord = EXTHRecordFactory.readEXTHRecord(mobiHeader, getRecordOffset(i));
			recordList.add(exthRecord);
		}

		return this;
	}

	void writeEXTHHeader(OutputStream out) throws IOException {
		writeString("EXTH", 4, out);
		writeInt(size(), 4, out);
		writeInt(getRecordCount(), 4, out);

		for (EXTHRecord record : recordList) {
			record.writeEXTHRecord(out);
		}

		int padding = paddingSize(allRecordsLength());
		for (int i = 0; i < padding; i++) {
			out.write(0);
		}
	}

	private int getRecordOffset(int record) {
		int offset = getOffset(12);
		for (int i = 0; i < record; i++) {
			EXTHRecord exthRecord = recordList.get(i);
			offset += exthRecord.getRecordLength();
		}
		return offset;
	}

	private int getOffset(int offset) {
		return this.exthHeaderOffset + offset;
	}

	private int allRecordsLength() {
		int size = 0;
		for (EXTHRecord rec : recordList) {
			size += rec.getRecordLength();
		}
		return size;
	}

	private int paddingSize(int dataSize) {
		int paddingSize = dataSize % 4;
		return paddingSize == 0 ? 0 : 4 - paddingSize;
	}

	private int getRecordCount() {
		return recordList != null ? recordList.size() : 0;
	}

	public int size() {
		int recordsLength = allRecordsLength();
		// first three header fields with 4 bytes for each.
		return 12 + recordsLength + paddingSize(recordsLength);
	}

	public List<EXTHRecord> getRecordList() {
		return recordList;
	}
	
	public void addRecord(EXTHRecord record) {
		recordList.add(record);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("exthHeaderOffset", exthHeaderOffset)
				.append("recordList", recordList.toArray())
		.toString();
	}

}
