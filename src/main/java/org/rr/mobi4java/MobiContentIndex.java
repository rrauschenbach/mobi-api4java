package org.rr.mobi4java;

import static org.apache.commons.lang3.BooleanUtils.negate;
import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.getString;
import static org.rr.mobi4java.ByteUtils.write;
import static org.rr.mobi4java.ByteUtils.writeInt;
import static org.rr.mobi4java.ByteUtils.writeString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MobiContentIndex extends MobiContent {
	
	private static final String IDENTIFIER = "INDX";

	public static enum INDEX_TYPE {
		NORMAL(0), INFLECTION(2);
		private final int type;
		
    private static Map<Integer, INDEX_TYPE> map = new HashMap<Integer, INDEX_TYPE>();
    
		static {
			for (INDEX_TYPE typeEnum : INDEX_TYPE.values()) {
				if(map.put(typeEnum.type, typeEnum) != null) {
					throw new IllegalArgumentException("Duplicate type " + typeEnum.type);
				}
			}
		}

		private INDEX_TYPE(final int type) {
			this.type = type;
		}
		
		public static INDEX_TYPE valueOf(int type) {
			return map.get(type);
		}

		public int getType() {
			return type;
		}
	}
	
	private int headerLength;
	
	private int indexType;
	
	private int unknown1;
	
	private int unknown2;
	
	private int idxtIndex;
	
	private int indexCount;
	
	private int indexEncoding;
	
	private int indexLanguage;
	
	private int totalIndexCount;
	
	private int ordtIndex;
	
	private int ligtIndex;
	
	private int ordtLigtEntriesCount;
	
	private int cncxRecordCount;
	
	private byte[] unknownIndxHeaderPart;
	
	private byte[] rest;
	
	private MobiContentTagx tagx;
	
	MobiContentIndex(byte[] content) throws IOException {
		super(content, CONTENT_TYPE.INDEX);
		readMobiIndex();
	}
	
	private void readMobiIndex() throws IOException {
		String identifier = getString(content, 0, 4);
		if (negate(StringUtils.equals(identifier, IDENTIFIER))) {
			throw new IOException("Expected to find index header identifier INDX but got '" + identifier + "' instead");
		}
		headerLength = getInt(content, 4, 4);
		indexType = getInt(content, 8, 4);
		unknown1 = getInt(content, 12, 4);
		unknown2 = getInt(content, 16, 4);
		idxtIndex = getInt(content, 20, 4);
		indexCount = getInt(content, 24, 4); // entries count
		indexEncoding = getInt(content, 28, 4);
		indexLanguage = getInt(content, 32, 4);
		totalIndexCount = getInt(content, 36, 4); // total entries count
		ordtIndex = getInt(content, 40, 4);
		ligtIndex = getInt(content, 44, 4);
		ordtLigtEntriesCount = getInt(content, 48, 4);
		cncxRecordCount = getInt(content, 52, 4);
		/* 60-148: phonetizer */
		unknownIndxHeaderPart = getBytes(content, 56, headerLength - 56);
		
		int ordtType = getInt(content, 164, 4);
		int ordtEntriesCount = getInt(content, 168, 4);
		int ordt1Offset = getInt(content, 172, 4);
		int ordt2Offset = getInt(content, 176, 4);
		int entrySize = ordtType == 0 ? 1 : 2;
		
		int tagxIndex = getInt(content, 180, 4);
		int tagxNameLength = getInt(content, 184, 4);
		if(tagxIndex > 0) {
			tagx = new MobiContentTagx(getBytes(content, tagxIndex));
			List<MobiContentTagEntry> tags = tagx.getTags();
			for (MobiContentTagEntry tag : tags) {
				int value = tag.getControlByte() & tag.getBitmask();
			}
			
			
			MobiContentIdxt idxt = new MobiContentIdxt(getBytes(content, idxtIndex), indexCount);
			
			rest = getBytes(content, tagxIndex + tagx.getSize());
		} else {
			rest = getBytes(content, headerLength);
		}
	}
	
	@Override
	byte[] writeContent(OutputStream out) throws IOException {
		ByteArrayOutputStream branch = new ByteArrayOutputStream();
		TeeOutputStream tee = new TeeOutputStream(out, branch);
		writeString(IDENTIFIER, 4, tee);
		writeInt(headerLength, 4, tee);
		writeInt(indexType, 4, tee);
		writeInt(unknown1, 4, tee);
		writeInt(unknown2, 4, tee);
		writeInt(idxtIndex, 4, tee);
		writeInt(indexCount, 4, tee);
		writeInt(indexEncoding, 4, tee);
		writeInt(indexLanguage, 4, tee);
		writeInt(totalIndexCount, 4, tee);
		writeInt(ordtIndex, 4, tee);
		writeInt(ligtIndex, 4, tee);
		writeInt(ordtLigtEntriesCount, 4, tee);
		writeInt(cncxRecordCount, 4, tee);
		write(unknownIndxHeaderPart, out);
		
		if(tagx != null) {
			tagx.writeContent(tee);
		}
		write(rest, out);
		
		return branch.toByteArray();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("contentType", getType())
				.append("identifier", IDENTIFIER)
				.append("headerLength", headerLength)
				.append("indexType", getIndexType())
				.append("unknown1", unknown1)
				.append("unknown2", unknown2)
				.append("idxtStart", idxtIndex)
				.append("indexCount", indexCount)
				.append("indexEncoding", getCharacterEncoding())
				.append("indexLanguage", indexLanguage)
				.append("totalIndexCount", totalIndexCount)
				.append("ordtIndex", ordtIndex)
				.append("ligtIndex", ligtIndex)
				.append("ordtLigtEntriesCount", ordtLigtEntriesCount)
				.append("cncxRecordCount", cncxRecordCount)
				.append("tagx", tagx)
		.toString();
	}

	public INDEX_TYPE getIndexType() {
		return INDEX_TYPE.valueOf(indexType);
	}

	public int getIdxtStart() {
		return idxtIndex;
	}

	public int getIndexCount() {
		return indexCount;
	}

	public String getCharacterEncoding() {
		return getCharacterEncoding(indexEncoding);
	}

	/**
	 * @return the language code of the index
	 */
	public int getIndexLanguage() {
		return indexLanguage;
	}

	/**
	 * @return the number of index entries
	 */
	public int getTotalIndexCount() {
		return totalIndexCount;
	}

}
