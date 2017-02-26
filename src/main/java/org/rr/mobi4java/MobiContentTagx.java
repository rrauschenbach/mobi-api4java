package org.rr.mobi4java;

import static org.apache.commons.lang3.BooleanUtils.negate;
import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.getString;
import static org.rr.mobi4java.ByteUtils.writeInt;
import static org.rr.mobi4java.ByteUtils.writeString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MobiContentTagx extends MobiContent {
	
	private static final String IDENTIFIER = "TAGX";

	private int headerLength;
	
	private int controlByteCount;
	
	private byte[] tagTable;
	
	private List<MobiContentTagEntry> tags;

	MobiContentTagx(byte[] content) throws IOException {
		super(content, CONTENT_TYPE.TAGX);
		readMobiTagx();
	}

	private void readMobiTagx() throws IOException {
		String identifier = getString(content, 0, 4);
		if (negate(StringUtils.equals(identifier, IDENTIFIER))) {
			throw new IOException("Expected to find TAGX header identifier TAGX but got '" + identifier + "' instead");
		}
		headerLength = getInt(content, 4, 4);
		controlByteCount = getInt(content, 8, 4);
		int tagCount = (headerLength - 12) / 4;
		tagTable = getBytes(content, 12, headerLength - 12);
		
		tags = new ArrayList<>(tagCount);
		for (int i = 0; i < tagCount; i++) {
			MobiContentTagEntry mobiContentTag = new MobiContentTagEntry(getBytes(content, 12 + (4 * i), 4));
			tags.add(mobiContentTag);
		}
	}
	
	@Override
	byte[] writeContent(OutputStream out) throws IOException {
		ByteArrayOutputStream branch = new ByteArrayOutputStream();
		TeeOutputStream tee = new TeeOutputStream(out, branch);
		writeString(IDENTIFIER, 4, tee);
		writeInt(headerLength, 4, tee);
		writeInt(controlByteCount, 4, tee);
		for (MobiContentTagEntry tag : tags) {
			tag.writeContent(out);
		}
		return branch.toByteArray();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("contentType", getType())
				.append("identifier", IDENTIFIER)
				.append("headerLength", headerLength)
		.toString();
	}

	@Override
	public int getSize() {
		return 12 + tagTable.length;
	}

	public List<MobiContentTagEntry> getTags() {
		return tags;
	}

	public void setTags(List<MobiContentTagEntry> tags) {
		this.tags = tags;
	}

}
