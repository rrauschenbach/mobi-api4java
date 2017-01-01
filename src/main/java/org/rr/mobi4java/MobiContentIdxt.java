package org.rr.mobi4java;

import static org.apache.commons.lang3.BooleanUtils.negate;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.getString;
import static org.rr.mobi4java.ByteUtils.writeInt;
import static org.rr.mobi4java.ByteUtils.writeString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MobiContentIdxt extends MobiContent {
	
	private static final String IDENTIFIER = "IDXT";

	private int indexEntriesIndex;
	
	private int indexEntriesCount;
	
	MobiContentIdxt(byte[] content) throws IOException {
		super(content, CONTENT_TYPE.IDXT);
		readMobiIdxt();
	}

	private void readMobiIdxt() throws IOException {
		String identifier = getString(content, 0, 4);
		if (negate(StringUtils.equals(identifier, IDENTIFIER))) {
			throw new IOException("Expected to find IDXT header identifier IDXT but got '" + identifier + "' instead");
		}
		indexEntriesIndex = getInt(content, 4, 2);
		indexEntriesCount = getInt(content, 6, 2);
	}
	
	@Override
	byte[] writeContent(OutputStream out) throws IOException {
		ByteArrayOutputStream branch = new ByteArrayOutputStream();
		TeeOutputStream tee = new TeeOutputStream(out, branch);
		writeString(IDENTIFIER, 4, tee);
		writeInt(indexEntriesIndex, 2, tee);
		writeInt(indexEntriesCount, 2, tee);
		return branch.toByteArray();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("contentType", getType())
				.append("identifier", IDENTIFIER)
				.append("indexEntriesIndex", indexEntriesIndex)
				.append("indexEntriesCount", indexEntriesCount)
		.toString();
	}

	@Override
	public int getSize() {
		return 8;
	}

	public int getIndexEntriesIndex() {
		return indexEntriesIndex;
	}

	public void setIndexEntriesIndex(int indexEntriesIndex) {
		this.indexEntriesIndex = indexEntriesIndex;
	}

	public int getIndexEntriesCount() {
		return indexEntriesCount;
	}

	public void setIndexEntriesCount(int indexEntriesCount) {
		this.indexEntriesCount = indexEntriesCount;
	}

}
