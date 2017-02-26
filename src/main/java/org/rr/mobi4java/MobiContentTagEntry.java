package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.writeInt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MobiContentTagEntry extends MobiContent {
	
	public static enum TAG_ENTRY_TYPE {
		END(0),
		POS(1), // NCX | Position offset for the beginning of NCX record (filepos) Ex: Beginning of a chapter
		LEN(2), // NCX | Record lenght. Ex: Chapter lenght
		NAME_OFFSET(3), // NCX | Label text offset in CNCX
		DEPTH_LEVEL(4), // NCX | Depth/Level of CNCX
		KOFFS(5), // NCX | kind CNCX offset
		POS_FID(6), // NCX | pos:fid
		PARENT(21), // NCX | Parent
		CHILD_1(22), // NCX | First child
		CHILD_N(23), // NCX | Last child
		IMAGE_INDEX(69),
		DESC_OFFSET(70), // Description offset in cncx
		AUTHOR_OFFSET(71), // Author offset in cncx
		IMAGE_CAPTION_OFFSET(72), // Image caption offset in cncx
		IMAGE_ATTR_OFFSET(73); // Image attribution offset in cncx
		
		private final int type;
		
    private static Map<Integer, TAG_ENTRY_TYPE> map = new HashMap<Integer, TAG_ENTRY_TYPE>();
    
		static {
			for (TAG_ENTRY_TYPE typeEnum : TAG_ENTRY_TYPE.values()) {
				if(map.put(typeEnum.type, typeEnum) != null) {
					throw new IllegalArgumentException("Duplicate type " + typeEnum.type);
				}
			}
		}

		private TAG_ENTRY_TYPE(final int type) {
			this.type = type;
		}
		
		public static TAG_ENTRY_TYPE valueOf(int type) {
			return map.get(type);
		}

		public int getType() {
			return type;
		}
	}
	
	private int tag;
	
	private int valuesCount;
	
	private int bitmask;
	
	private int controlByte;

	MobiContentTagEntry(byte[] content) throws IOException {
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
				.append("tag", getTag())
				.append("valuesCount", valuesCount)
				.append("bitmask", bitmask)
				.append("controlByte", controlByte)
		.toString();
	}

	@Override
	public int getSize() {
		return 4;
	}

	public TAG_ENTRY_TYPE getTag() {
		return TAG_ENTRY_TYPE.valueOf(tag);
	}

	public void setTag(TAG_ENTRY_TYPE tag) {
		this.tag = tag.getType();
	}

	public int getValuesCount() {
		return valuesCount;
	}

	public void setValuesCount(int valuesCount) {
		this.valuesCount = valuesCount;
	}

	public int getBitmask() {
		return bitmask;
	}

	public void setBitmask(int bitmask) {
		this.bitmask = bitmask;
	}

	public int getControlByte() {
		return controlByte;
	}

	public void setControlByte(int controlByte) {
		this.controlByte = controlByte;
	}

}
