package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.dumpByteArray;
import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.ByteUtils.write;
import static org.rr.mobi4java.ByteUtils.writeInt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class EXTHRecord {
	
	public static enum RECORD_TYPE {
		AUTHOR(100),
		PUBLISHER(101),
		IMPRINT(102),
		DESCRIPTION(103),
		ISBN(104),
		SUBJECT(105),
		PUBLISHING_DATE(106),
		REVIEW(107),
		CONTRIBUTOR(108),
		RIGHTS(109),
		SUBJECT_CODE(110),
		TYPE(111),
		SOURCE(112),
		/** Kindle Paperwhite labels books with "Personal" if they don't have this record. */
		ASIN(113),
		VERSION_NUMBER(114),
		/** 0x0001 if the book content is only a sample of the full book */
		SAMPLE(115),
		/** Position (4-byte offset) in file at which to open when first opened */
		STARTREADING(116),
		/** Mobipocket Creator adds this if Adult only is checked on its GUI; contents: "yes" */
		ADULT(117),
		/** As text, e.g. "4.99" */
		RETAIL_PRICE(118),
		/** As text, e.g. "USD" */
		RETAIL_PRICE_CURRENCY(119),
		TSC(120),
		KF8_BOUNDARY_OFFSET(121),
		FIXED_LAYOUT(122),
		BOOK_TYPE(123),
		ORIENTATION_LOCK(124),
		COUNT_OF_RESOURCES(125),
		ORIGINAL_RESOLUTION(126),
		ZERO_GUTTER(127),
		ZERO_MARGIN(128),
		KF8_COVER_URI(129),
		UNKNOWN_131(131),
		REGION_MAGNIFICATION(132),
		LENDING_ENABLED(150),
		/** As text*/
		DICTIONARY_SHORT_NAME(200),
		/** Add to first image field in Mobi Header to find PDB record containing the cover image */
		COVER_OFFSET(201),
		/** Add to first image field in Mobi Header to find PDB record containing the thumbnail cover image */
		THUMBNAIL_OFFSET(202),
		HAS_FAKE_COVER(203),
		/** Known Values: 1=mobigen, 2=Mobipocket Creator, 200=kindlegen (Windows), 201=kindlegen (Linux),
		 *  02=kindlegen (Mac).
		 * Warning: Calibre creates fake creator entries, pretending to be a Linux kindlegen 1.2 (201, 1, 2,
		 * 33307) for normal ebooks and a non-public Linux kindlegen 2.0 (201, 2, 0, 101) for periodicals.
		 */
		CREATOR_SOFTWARE(204),
		CREATOR_MAJOR_VERSION(205),
		CREATOR_MINOR_VERSION(206),
		CREATOR_BUILD_NUMBER(207),
		WATERMARK(208),
		/** Used by the Kindle (and Android app) for generating book-specific PIDs. */
		TAMPER_PROOF_KEYS(209),
		FONT_SIGNATURE(300),
		/** Integer percentage of the text allowed to be clipped. Usually 10. */
		CLIPPING_LIMIT(401),
		PUBLISHER_LIMIT(402),
		UNKNOWN_403(403),
		/** 1 - Text to Speech disabled; 0 - Text to Speech enabled */
		TTS_OFF(404),
		/** 1 in this field seems to indicate a rental book */
		BORROWED(405),
		/** If this field is removed from a rental, the book says it expired in 1969 */
		BORROWED_EXPIRATION(406),
		UNKNOWN_407(407),
		UNKNOWN_450(450),
		UNKNOWN_451(451),
		UNKNOWN_452(452),
		UNKNOWN_453(453),
		/** PDOC - Personal Doc; EBOK - ebook; EBSP - ebook sample; */
		CDE_TYPE(501),
		LAST_UPDATE_TIME(502),
		UPDATED_TITLE(503),
		/** There is sometimes a copy of the asin here. */
		ASIN_COPY(504),
		AMAZON_CONTENT_REFERENCE(505),
		TITLE_LANGUAGE(506),
		TITLE_DISPLAY_DIRECTION(507),
		TITLE_PRONUNCIATION(508),
		TITLE_COLLATION(509),
		SECONDARY_TITLE(510),
		SECONDARY_TITLE_LANGUAGE(511),
		SECONDARY_TITLE_DIRECTION(512),
		SECONDARY_TITLE_PRONUNCIATION(513),
		SECONDARY_TITLE_COLLATION(514),
		AUTHOR_LANGUAGE(515),
		AUTHOR_DISPLAY_DIRECTION(516),
		AUTHOR_PRONUNCIATION(517),
		AUTHOR_COLLATION(518),
		AUTHOR_TYPE(519),
		PUBLISHER_LANGUAGE(520),
		PUBLISHER_DISPLAY_DIRECTION(521),
		PUBLISHER_PRONUNCIATION(522),
		PUBLISHER_COLLATION(523),
		LANGUAGE(524),
		ALIGNMENT(525),
		NCX_INGESTED_BY_SOFTWARE(526),
		PAGE_PROGRESSION_DIRECTION(527),
		OVERRIDE_KINDLE_FONTS(528),
		COMPRESSION_UPGRADED(529),
		SOFT_HYPHENS_IN_CONTENT(530),
		DICTIONARY_IN_LANGAGUE(531),
		DICTIONARY_OUT_LANGUAGE(532),
		FONT_CONVERTED(533),
		AMAZON_CREATOR_INFO(534),
		/** found 1019-d6e4792 in this record, which is a build number of Kindlegen 2.7 */
		CREATOR_BUILD_NUMBER_COPY(535),
		/** CONT_Header is 0, Ends with CONTAINER_BOUNDARY (or Asset_Type?) */
		HD_MEDIA_CONTAINERS_INFO(536),
		RESOURCE_CONTAINER_FIDELITY(538),
		HD_CONTAINER_MIMETYPE(539),
		SAMPLE_FOR_SPECIAL_PURPOSE(540),
		KINDLETOOL_OPERATION_INFORMATION(541),
		CONTAINER_ID(542),
		ASSET_TYPE(543),
		UNKNOWN_544(544),
		/** String 'I\x00n\x00M\x00e\x00m\x00o\x00r\x00y\x00' found in this record, for KindleGen V2.9 build 1029-0897292 */
		IN_MEMORY(547);
		
		private final int type;
		
    private static Map<Integer, RECORD_TYPE> map = new HashMap<Integer, RECORD_TYPE>();
    
		static {
			for (RECORD_TYPE typeEnum : RECORD_TYPE.values()) {
				if(map.put(typeEnum.type, typeEnum) != null) {
					throw new IllegalArgumentException("Duplicate type " + typeEnum.type);
				}
			}
		}

		private RECORD_TYPE(final int type) {
			this.type = type;
		}
		
		public static RECORD_TYPE valueOf(int type) {
			return map.get(type);
		}

		public int getType() {
			return type;
		}
	}	
	
	private int recordType;

	private byte[] recordData;

	protected EXTHRecord(int recordType) {
		this.recordType = recordType;
	}
	
	EXTHRecord readEXTHRecord(byte[] mobiHeader, int offset) throws IOException {
		int length = getInt(mobiHeader, offset + 4, 4);
		if (length < 8) {
			throw new IOException("Invalid EXTH record length");
		}
		recordData = getBytes(mobiHeader, offset + 8, length - 8);
		return this;
	}
	
	void writeEXTHRecord(OutputStream out) throws IOException {
		writeInt(recordType, 4, out);
		writeInt(getRecordLength(), 4, out);
		write(recordData, getRecordDataLength(), out);
	}

	public EXTHRecord.RECORD_TYPE getRecordType() {
		return EXTHRecord.RECORD_TYPE.valueOf(recordType);
	}
	
	protected void setRecordType(EXTHRecord.RECORD_TYPE recordType) {
		this.recordType = recordType.type;
	}

	public byte[] getData() {
		return recordData;
	}
	
	public void setData(byte[] recordData) {
		this.recordData = recordData;
	}

	public int getIntData() {
		return getInt(recordData);
	}
	
	public void setIntData(int value) {
		recordData = getBytes(value, new byte[4]);
	}

	public int getRecordLength() {
		return getRecordDataLength() + 8;
	}

	private int getRecordDataLength() {
		return (recordData != null ? recordData.length : 0);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("recordType", recordType)
				.append("recordEnumType", RECORD_TYPE.valueOf(recordType))
				.append("recordData", dumpByteArray(recordData))
		.toString();
	}
}
