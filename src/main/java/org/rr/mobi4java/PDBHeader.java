package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.*;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PDBHeader {

	/** The size of a PDB record */
	private static final int PDB_RECORD_SIZE = 8;

	/** The size of a PDB header without any records. */
	private static final int PDB_HEADER_SIZE = 80;
	
	private String name;
	private int attributes;
	private int version;
	private long creationDate;
	private long modificationDate;
	private long lastBackupDate;
	private long modificationNumber;
	private long appInfoID;
	private long sortInfoID;
	private long type;
	private long creator;
	private long uniqueIDSeed;
	private long nextRecordListID;
	private int recordCount;
	private List<PDBRecord> records;
	
	private PDBHeader() {}

	static PDBHeader readHeader(byte[] mobiData) {
		PDBHeader header = new PDBHeader();
		header.name = getString(mobiData, 0, 32);
		header.attributes = getInt(mobiData, 32, 2);
		header.version = getInt(mobiData, 34, 2);
		header.creationDate = getLong(mobiData, 36, 4);
		header.modificationDate = getLong(mobiData, 40, 4);
		header.lastBackupDate = getLong(mobiData, 44, 4);
		header.modificationNumber = getLong(mobiData, 48, 4);
		header.appInfoID = getLong(mobiData, 52, 4);
		header.sortInfoID = getLong(mobiData, 56, 4);
		header.type = getLong(mobiData, 60, 4);
		header.creator = getLong(mobiData, 64, 4);
		header.uniqueIDSeed = getLong(mobiData, 68, 4);
		header.nextRecordListID = getLong(mobiData, 72, 4);
		header.recordCount = getInt(mobiData, 76, 2);

		header.records = new ArrayList<PDBRecord>(header.recordCount);
		for (int i = 0; i < header.recordCount; i++) {
			header.records.add(new PDBRecord(mobiData, i));
		}

		return header;
	}
	
	void writeHeader(MobiContentHeader mobiHeader, List<MobiContent> mobiContents, OutputStream out) throws IOException {
		writeString(name, 32, out);
		writeInt(attributes, 2, out);
		writeInt(version, 2, out);
		writeLong(creationDate, 4, out);
		writeLong(modificationDate, 4, out);
		writeLong(lastBackupDate, 4, out);
		writeLong(modificationNumber, 4, out);
		writeLong(appInfoID, 4, out);
		writeLong(sortInfoID, 4, out);
		writeLong(type, 4, out);
		writeLong(creator, 4, out);
		writeLong(uniqueIDSeed, 4, out);
		writeLong(nextRecordListID, 4, out);
		write(getBytes(mobiContents.size(), new byte[2]), 2, out); // recordCount
		
		for (PDBRecord record : createPDBRecords(mobiHeader, mobiContents)) {
			record.writeRecord(out);
		}
		
		write(new byte[2], 2, out);
	}
	
	private List<PDBRecord> createPDBRecords(MobiContentHeader mobiHeader, List<MobiContent> mobiContents) {
		int recordCount = mobiContents.size();
		int offset = PDB_HEADER_SIZE + (recordCount * PDB_RECORD_SIZE);
		List<PDBRecord> records = new ArrayList<>(recordCount);
		
		
		records.add(createPDBRecord(offset, 0));
		int length = mobiHeader.getSize();
		for(int i = 1; i < mobiContents.size(); i++) {
			offset += length;
			length = mobiContents.get(i).getSize();
			records.add(createPDBRecord(offset, i));
		}
		
		return records;
	}
	
	private PDBRecord createPDBRecord(int recordOffset, int uniqueId) {
		return new PDBRecord(getBytes(recordOffset, new byte[4]), new byte[]{0}, 
				getBytes(uniqueId, new byte[3]));
	}
	
	public List<PDBRecord> getRecords() {
		return records;
	}
	
	public boolean hasRecord(int idx) {
		return records.size() > idx;
	}

	public PDBRecord getRecord(int idx) {
		return records.get(idx);
	}
	
	public int getRecordCount() {
		return recordCount;
	}

	public String getName() {
		return name;
	}

	public int getAttributes() {
		return attributes;
	}

	public int getVersion() {
		return version;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public long getModificationDate() {
		return modificationDate;
	}

	public long getLastBackupDate() {
		return lastBackupDate;
	}

	public long getModificationNumber() {
		return modificationNumber;
	}

	public long getAppInfoID() {
		return appInfoID;
	}

	public long getSortInfoID() {
		return sortInfoID;
	}

	public long getType() {
		return type;
	}

	public long getCreator() {
		return creator;
	}

	public long getUniqueIDSeed() {
		return uniqueIDSeed;
	}
	
	public long getNextRecordListID() {
		return nextRecordListID;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.append("attributes", attributes)
				.append("version", version)
				.append("creationDate", creationDate)
				.append("modificationDate", modificationDate)
				.append("lastBackupDate", lastBackupDate)
				.append("modificationNumber", modificationNumber)
				.append("appInfoID", appInfoID)
				.append("sortInfoID", sortInfoID)
				.append("type", type)
				.append("creator", creator)
				.append("uniqueIDSeed", uniqueIDSeed)
				.append("nextRecordListID", nextRecordListID)
				.append("numRecords", getRecordCount())
		.toString();
	}
}
