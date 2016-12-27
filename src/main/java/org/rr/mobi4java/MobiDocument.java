package org.rr.mobi4java;

import static org.rr.mobi4java.ByteUtils.chunk;
import static org.rr.mobi4java.ByteUtils.getInt;
import static org.rr.mobi4java.MobiUtils.isImage;
import static org.rr.mobi4java.MobiUtils.removeRandomBytes;
import static org.rr.mobi4java.MobiUtils.removeUtfReplacementCharacter;
import static org.rr.mobi4java.util.MobiLz77.lz77Decode;
import static org.rr.mobi4java.util.MobiLz77.lz77Encode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.MobiContent.TYPE;
import org.rr.mobi4java.MobiHeader.COMPRESSION_CODE;


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
	
	/**
	 * Get the image bytes for the cover of this {@link MobiDocument}.
	 * 
	 * @return The cover bytes or <code>null</code> if no cover was found.
	 */
	public byte[] getCover() {
		return getCoverByType(RECORD_TYPE.COVER_OFFSET);
	}
	
	/**
	 * Store the given image data as cover. If the document already have a cover, the old cover will be replaced with the given one.
	 * The thumbnail for this document is not affected by this method. The thumbnail must be set separately with the {@link #setThumbnail(byte[])} method.
	 * 
	 * @param image The image bytes to be used as cover. Take sure that the image format is supported by the target device.
	 * @throws IllegalArgumentException if the given image is null.
	 */
	public void setCover(byte[] image) throws IllegalArgumentException {
		if(image == null) {
			throw new IllegalArgumentException("No image bytes available.");
		} 
		
		List<EXTHRecord> exthRecord = mobiHeader.getEXTHRecords(RECORD_TYPE.COVER_OFFSET);
		if(!exthRecord.isEmpty()) {
			setCoverOrThumbnail(image, exthRecord);
		} else {
			createCoverOrThumbnailRecord(RECORD_TYPE.COVER_OFFSET, 1, image);
		}
	}

	/**
	 * Store the given image data as thumbnail. If the document already have a thumbnail, the old thumbnail will be replaced with the given one.
	 * 
	 * @param image The image bytes to be used as thumbnail. Take sure that the image format is supported by the target device.
	 * @throws IllegalArgumentException if the given image is null.
	 */
	public void setThumbnail(byte[] image) {
		if(image == null) {
			throw new IllegalArgumentException("No image bytes available.");
		} 
		
		List<EXTHRecord> exthRecord = mobiHeader.getEXTHRecords(RECORD_TYPE.THUMBNAIL_OFFSET);
		if(!exthRecord.isEmpty()) {
			setCoverOrThumbnail(image, exthRecord);
		} else {
			createCoverOrThumbnailRecord(RECORD_TYPE.THUMBNAIL_OFFSET, 2, image);
		}
	}

	private void setCoverOrThumbnail(byte[] image, List<EXTHRecord> coverRecords) {
		int index = getInt(coverRecords.get(0).getData()) + mobiHeader.getFirstImageIndex();
		mobiContents.get(index).setContent(image);
	}
	
	private void createCoverOrThumbnailRecord(RECORD_TYPE type, int contentRecordAfterFirstImage, byte[] image) {
		EXTHRecord exthRecord = new EXTHRecord(type.getType());
		exthRecord.setIntData(contentRecordAfterFirstImage);
		mobiHeader.addEXTHRecord(exthRecord);
		
		MobiContent content = new MobiContent(image);
		mobiContents.add(mobiHeader.getFirstImageIndex() + contentRecordAfterFirstImage, content);
	}
	
	/**
	 * Get the image bytes for the thumbnail of this {@link MobiDocument}.
	 * 
	 * @return The thumbnail bytes or <code>null</code> if no thumbnail was found.
	 */
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
	
	/**
	 * Get the mobi html formatted content.
	 *  
	 * @return The text part if the mobi document. Never returns <code>null</code>.
	 * @throws IOException
	 */
  public String getTextContent() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  	int firstContentIndex = MobiUtils.getTextContentStartIndex(mobiHeader);
  	int lastContentIndex = firstContentIndex + mobiHeader.getRecordCount();
  	
		for (int i = firstContentIndex; i <= lastContentIndex; i++) {
			MobiContent mobiContent = mobiContents.get(i);
			if(mobiContent.guessContentType() == TYPE.END_OF_TEXT) { // optional record
				break;
			}
			byte[] decoded = null;
			
			if (mobiHeader.getCompressionCode() == COMPRESSION_CODE.PALM_DOC) {
				decoded = lz77Decode(mobiContent.content);
			} else if (mobiHeader.getCompressionCode() == COMPRESSION_CODE.NONE) {
				decoded = mobiContent.content;
			} else if (mobiHeader.getCompressionCode() == COMPRESSION_CODE.HUFF_CDIC) {
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
  
  /**
   * Removes the old text content and applies the given text to the {@link MobiDocument}. If a book index exists it will be removed. 
   * 
   * @param mobiText The mobi html formatted text which should be set to the {@link MobiDocument}.
   * @throws IOException
   */
  public void setTextContent(String mobiText) throws IOException {
  	int firstContentIndex = MobiUtils.getTextContentStartIndex(mobiHeader);
  	int lastContentIndex = firstContentIndex + mobiHeader.getRecordCount();

  	// remove text content including a possible book index.
  	removeContent(firstContentIndex, Math.max(lastContentIndex, mobiHeader.getFirstImageIndex() - 1));
  	
  	byte[] encodedMobiText = lz77Encode(mobiText.getBytes(getCharacterEncoding()));
  	mobiHeader.setCompressionCode(COMPRESSION_CODE.PALM_DOC);
  	
  	Collection<byte[]> chunkedMobiText = chunk(encodedMobiText, MobiHeader.DEFAULT_RECORD_SIZE);
  	
  	mobiContents.addAll(toMobiContent(chunkedMobiText));
  	
  	mobiHeader.setTextLength(mobiText.length());
  	mobiHeader.setRecordCount(chunkedMobiText.size());
  	mobiHeader.setRecordSize(MobiHeader.DEFAULT_RECORD_SIZE);
  	
  	// set non book index and first image index to the same value because there is no book index at this point.
  	mobiHeader.setFirstNonBookIndex(firstContentIndex + chunkedMobiText.size() + 1);
  	mobiHeader.setFirstImageIndex(firstContentIndex + chunkedMobiText.size() + 1);
  }

	private Collection<MobiContent> toMobiContent(Collection<byte[]> chunkedMobiText) {
		return CollectionUtils.collect(chunkedMobiText, new Transformer<byte[], MobiContent>() {
			
			@Override
			public MobiContent transform(byte[] input) {
				return new MobiContent(input);
			}
		});
	}
  
  private void removeContent(int firstContentIndex, int lastContentIndex) {
  	mobiContents.removeAll(mobiContents.subList(firstContentIndex, lastContentIndex));
  }

}
