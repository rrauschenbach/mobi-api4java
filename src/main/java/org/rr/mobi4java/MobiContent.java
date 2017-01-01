package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.write;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MobiContent {
	
	public enum CONTENT_TYPE {
		HEADER, CONTENT, INDEX, TAGX, TAG, IDXT, FLIS, FCIS, FDST, DATP, SRCS, CMET, AUDI, VIDE, END_OF_TEXT, 
		COVER, THUMBNAIL, IMAGE, UNKNOWN
	};
	
	protected byte[] content;
	
	private CONTENT_TYPE type;
	
	MobiContent(byte[] content, CONTENT_TYPE type) {
		this.content = content;
		this.type = type;
	}
	
	byte[] writeContent(OutputStream out) throws IOException {
		write(content, out);
		return content;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public int getSize() {
		return content.length;
	}

	public CONTENT_TYPE getType() {
		return type;
	}
	
	protected String getCharacterEncoding(int textEncoding) {
		if (textEncoding == 1252) {
			return "Cp1252";
		} else if (textEncoding == 65001) {
			return "UTF-8";
		} else if (textEncoding == 65002) {
			return "UTF-16";
		}
		return null;
	}

	public String toString() {
		return new ToStringBuilder(this)
				.append("contentType", getType())
				.append("content", ByteUtils.dumpByteArray(content))
				.toString();
	}
}
