package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.writeInt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MobiContentTag extends MobiContent {
	
	private int tag;
	
	private int valuesCount;
	
	private int bitmask;
	
	private int controlByte;

	MobiContentTag(byte[] content) throws IOException {
		super(content, CONTENT_TYPE.TAG);
		readMobiTag();
	}

	private void readMobiTag() throws IOException {
		tag = getInt(content, 0, 1);
		valuesCount = getInt(content, 1, 1);
		bitmask = getInt(content, 2, 1);
		controlByte = getInt(content, 3, 1);
	}
	
	@Override
	byte[] writeContent(OutputStream out) throws IOException {
		ByteArrayOutputStream branch = new ByteArrayOutputStream();
		TeeOutputStream tee = new TeeOutputStream(out, branch);
		writeInt(tag, 1, tee);
		writeInt(valuesCount, 1, tee);
		writeInt(bitmask, 1, tee);
		writeInt(controlByte, 1, tee);
		return branch.toByteArray();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("tag", tag)
				.append("valuesCount", valuesCount)
				.append("bitmask", bitmask)
				.append("controlByte", controlByte)
		.toString();
	}

	@Override
	public int getSize() {
		return 4;
	}

}
