package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.MobiUtils.isImage;
import static org.rr.mobi4java.MobiUtils.removeRandomBytes;
import static org.rr.mobi4java.MobiUtils.removeUtfReplacementCharacter;
import static org.rr.mobi4java.util.MobiLz77.lz77Decode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.MobiContent.TYPE;


public class MobiDocument {

	private PDBHeader pdbHeader;
	
	private MobiHeader mobiHeader;
	
	private List<MobiContent> mobiContents;
	
	private MobiMetaData mobiMetaData;
	
	MobiDocument(PDBHeader pdbHeader, MobiHeader mobiHeader, List<MobiContent> mobiContent) {
		this.pdbHeader = pdbHeader;
		this.mobiHeader = mobiHeader;
		this.mobiContents = mobiContent;
		this.mobiMetaData = new MobiMetaData(mobiHeader);
	}

	PDBHeader getPdbHeader() {
		return pdbHeader;
	}

	MobiHeader getMobiHeader() {
		return mobiHeader;
	}
	
	List<MobiContent> getMobiContents() {
		return mobiContents;
	}
	
	public MobiMetaData getMetaData() {
		return mobiMetaData;
	}
	
	public byte[] getCover() {
		return getCoverByType(RECORD_TYPE.COVER_OFFSET);
	}
	
	/**
	 * Store the given image data as cover.
	 * @param image The image bytes to be used as cover.
	 * @throws IllegalArgumentException if the given image is null.
	 */
	public void setCover(byte[] image) throws IllegalArgumentException {
		if(image == null) {
			throw new IllegalArgumentException("No image bytes available.");
		} 
		
		List<EXTHRecord> exthRecord = mobiHeader.getEXTHRecords(RECORD_TYPE.COVER_OFFSET);
		if(!exthRecord.isEmpty()) {
			setCover(image, exthRecord);
		}
	}

	private void setCover(byte[] image, List<EXTHRecord> coverRecords) {
		int index = getInt(coverRecords.get(0).getData()) + mobiHeader.getFirstImageIndex();
		mobiContents.get(index).setContent(image);
	}
	
	public byte[] getThumbnail() {
		return getCoverByType(RECORD_TYPE.THUMBNAIL_OFFSET);
	}
	
	public List<byte[]> getImages() {
		List<MobiContent> imageContents = getImageContents();
		List<byte[]> images = new ArrayList<>(imageContents.size());
		for (MobiContent imageContent : imageContents) {
			images.add(imageContent.content);
		}
		return images;
	}
	
	public List<MobiContent> getImageContents() {
		int firstImageIndex = mobiHeader.getFirstImageIndex();
		List<MobiContent> imageContents = new ArrayList<>(mobiContents.size() - firstImageIndex);
		for(int i = firstImageIndex; i < mobiContents.size(); i++) {
			MobiContent content = mobiContents.get(i);
			if(isImage(content.content)) {
				imageContents.add(content);
			}
		}
		return imageContents;
	}
	
	private byte[] getCoverByType(RECORD_TYPE type) {
		List<EXTHRecord> coverRecords = mobiHeader.getEXTHRecords(type);
		if(!coverRecords.isEmpty()) {
			int index = getInt(coverRecords.get(0).getData()) + mobiHeader.getFirstImageIndex();
			return mobiContents.get(index).content;
		}
		return null;
	}
	
	public String getFullName() {
		return getMobiHeader().getFullName();
	}
	
	public void setFullName(String name) throws UnsupportedEncodingException {
		getMobiHeader().setFullName(name);
	}
	
	public String getCharacterEncoding() {
		return mobiHeader.getCharacterEncoding();
	}
	
  public String getTextContent() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  	int firstContentIndex = MobiUtils.getTextContentStartIndex(mobiHeader);
  	int lastContentIndex = MobiUtils.getTextContentEndIndex(mobiHeader, pdbHeader);
  	
		for (int i = firstContentIndex; i <= lastContentIndex; i++) {
			MobiContent mobiContent = mobiContents.get(i);
			if(mobiContent.guessContentType() == TYPE.END_OF_TEXT) { // optional record
				break;
			}
			byte[] decoded = null;
			
			if (mobiHeader.getCompressionCode() == 2) { // PalmDOC
				decoded = lz77Decode(mobiContent.content);
			} else if (mobiHeader.getCompressionCode() == 1) { // None
				decoded = mobiContent.content;
			} else if (mobiHeader.getCompressionCode() == 17480) { // HUFF/CDIC
				// TODO 
				throw new UnsupportedEncodingException("HUFF/CDIC encoding is not supported."); 
			} else {
				throw new IllegalArgumentException("Compression not supported " + mobiHeader.getCompressionCode());
			}

			decoded = removeRandomBytes(decoded);
			
			if (mobiContent.guessContentType() != TYPE.INDEX) {
				outputStream.write(decoded);
			}
		}
		return removeUtfReplacementCharacter(outputStream.toString(getCharacterEncoding()));
  }

}
