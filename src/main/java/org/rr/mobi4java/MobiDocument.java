package org.rr.mobi4java;

import static org.apache.commons.collections4.CollectionUtils.collect;
import static org.rr.mobi4java.ByteUtils.chunk;
import static org.rr.mobi4java.MobiUtils.removeRandomBytes;
import static org.rr.mobi4java.MobiUtils.removeUtfReplacementCharacter;
import static org.rr.mobi4java.util.MobiLz77.lz77Decode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.Transformer;
import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.MobiContent.CONTENT_TYPE;
import org.rr.mobi4java.MobiContentHeader.COMPRESSION_CODE;


public class MobiDocument {
	
	/** Max size of a text content record. If the text is longer than this size, the text must be chunked. */
	private static final int DEFAULT_TEXT_CONTENT_RECORD_SIZE = 4096;

	private PDBHeader pdbHeader;
	
	private MobiContentHeader mobiHeader;
	
	private List<MobiContent> mobiContents;
	
	private MobiMetaData mobiMetaData;
	
	MobiDocument(PDBHeader pdbHeader, MobiContentHeader mobiHeader, List<MobiContent> mobiContent) {
		this.pdbHeader = pdbHeader;
		this.mobiHeader = mobiHeader;
		this.mobiContents = mobiContent;
		this.mobiMetaData = new MobiMetaData(mobiHeader);
	}

	PDBHeader getPdbHeader() {
		return pdbHeader;
	}

	MobiContentHeader getMobiHeader() {
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
		MobiContent content = getCoverByType(CONTENT_TYPE.COVER);
		return content != null ? content.getContent() : null;
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
		
		MobiContent content = getCoverByType(CONTENT_TYPE.COVER);
		if(content != null) {
			content.setContent(image);
		} else {
			int indexAfterFirstImageRecord = 0;
			createCoverOrThumbnailRecord(RECORD_TYPE.COVER_OFFSET, indexAfterFirstImageRecord, image);
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
		
		MobiContent content = getCoverByType(CONTENT_TYPE.THUMBNAIL);
		if(content != null) {
			content.setContent(image);
		} else {
			int indexAfterFirstImageRecord = getCover() != null ? 1 : 0;
			createCoverOrThumbnailRecord(RECORD_TYPE.THUMBNAIL_OFFSET, indexAfterFirstImageRecord, image);
		}
	}
	
	private void createCoverOrThumbnailRecord(RECORD_TYPE type, int indexAfterFirstImageRecord, byte[] image) {
		EXTHRecord exthRecord = new EXTHRecord(type.getType());
		exthRecord.setIntData(indexAfterFirstImageRecord);
		mobiHeader.addEXTHRecord(exthRecord);
		
		MobiContent content;
		if(type == RECORD_TYPE.COVER_OFFSET) {
			content = MobiContentRecordFactory.createCoverRecord(image);
		} else if(type == RECORD_TYPE.THUMBNAIL_OFFSET) {
			content = MobiContentRecordFactory.createThumbnailRecord(image);
		} else {
			throw new IllegalArgumentException("Failed to create record type " + type);
		}
		mobiContents.add(mobiHeader.getFirstImageIndex() + indexAfterFirstImageRecord, content);
		
		// no need to adjust the indices before the cover but recalculate the others.
		adjustIndices(mobiHeader.getFirstNonBookIndex(), mobiHeader.getFirstImageIndex());
		adjustCoverAndThumbnailOffsets(mobiHeader.getFirstImageIndex());
	}
	
	/**
	 * Get the image bytes for the thumbnail of this {@link MobiDocument}.
	 * 
	 * @return The thumbnail bytes or <code>null</code> if no thumbnail was found.
	 */
	public byte[] getThumbnail() {
		MobiContent content = getCoverByType(CONTENT_TYPE.THUMBNAIL);
		return content != null ? content.getContent() : null; 
	}
	
	/**
	 * Get all images from the {@link MobiDocument} including the cover and the thumbnail
	 * which can be fetched with the methods {@link #getCover()} and {@link #getThumbnail()}.
	 * 
	 * @return A list of all available images. Never returns <code>null</code>.
	 */
	public List<byte[]> getImages() {
		List<MobiContent> imageContents = getImageContents();
		List<byte[]> images = new ArrayList<>(imageContents.size());
		for (MobiContent imageContent : imageContents) {
			images.add(imageContent.getContent());
		}
		return images;
	}
	
	List<MobiContent> getImageContents() {
		int firstImageIndex = mobiHeader.getFirstImageIndex();
		List<MobiContent> imageContents = new ArrayList<>(mobiContents.size() - firstImageIndex);
		for(int i = firstImageIndex; i < mobiContents.size(); i++) {
			MobiContent content = mobiContents.get(i);
			if(content.getType() == CONTENT_TYPE.IMAGE || content.getType() == CONTENT_TYPE.COVER || content.getType() == CONTENT_TYPE.THUMBNAIL) {
				imageContents.add(content);
			}
		}
		return imageContents;
	}
	
	private MobiContent getCoverByType(CONTENT_TYPE type) {
		for (MobiContent mobiContent : mobiContents) {
			if(mobiContent.getType() == type) {
				return mobiContent;
			}
		}
		return null;
	}
	
	/**
	 * Get the name of the {@link MobiDocument}. This is usually the book's title.
	 * 
	 * @return The name of the document. Never returns <code>null</code>.
	 */
	public String getFullName() {
		return getMobiHeader().getFullName();
	}
	
	/**
	 * Set the name of the {@link MobiDocument}. This is usually the book's title.
	 * 
	 * @param name The name of the book.
	 * @throws UnsupportedEncodingException Happens if the {@link MobiDocument} defines an erroneous character encoding. 
	 * 						Check {@link #getCharacterEncoding()}.
	 */
	public void setFullName(String name) throws UnsupportedEncodingException {
		getMobiHeader().setFullName(name);
	}
	
	/**
	 * Get the character encoding for the {@link MobiDocument}. The returned value must be a valid value like 'UTF-8', 'UTF-16' or 'Cp1252'
	 * 
	 * @return The character encoding. Never returns <code>null</code>.
	 * @throws UnsupportedEncodingException Happens if the {@link MobiDocument} defines an erroneous character encoding.
	 */
	public String getCharacterEncoding() throws UnsupportedEncodingException {
		String characterEncoding = mobiHeader.getCharacterEncoding();
		if(characterEncoding != null) {
			return characterEncoding;	
		}
		throw new UnsupportedEncodingException("Invalid character encoding " + mobiHeader.getTextEncoding());
	}
	
	/**
	 * Get the mobi html formatted content.
	 *  
	 * @return The text part if the mobi document. Never returns <code>null</code>.
	 * @throws IOException
	 */
  public String getTextContent() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  	List<MobiContent> contents = MobiUtils.findContentsByType(mobiContents, CONTENT_TYPE.CONTENT);
  	for (MobiContent mobiContent : contents) {
  		byte[] decoded = null;
			if (mobiHeader.getCompressionCode() == COMPRESSION_CODE.PALM_DOC) {
				decoded = lz77Decode(mobiContent.getContent());
			} else if (mobiHeader.getCompressionCode() == COMPRESSION_CODE.NONE) {
				decoded = mobiContent.getContent();
			} else if (mobiHeader.getCompressionCode() == COMPRESSION_CODE.HUFF_CDIC) {
				// TODO 
				throw new UnsupportedEncodingException("HUFF/CDIC encoding is not supported."); 
			} else {
				throw new IllegalArgumentException("Compression not supported " + mobiHeader.getCompressionCode());
			}

			decoded = removeRandomBytes(decoded);
			
			if (mobiContent.getType() != CONTENT_TYPE.INDEX) {
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

  	// remove text content including a possible book index and a EOT record.
  	removeContent(firstContentIndex, Math.max(lastContentIndex, mobiHeader.getFirstImageIndex()));
  	
  	byte[] encodedMobiText = mobiText.getBytes(getCharacterEncoding());
  	mobiHeader.setCompressionCode(COMPRESSION_CODE.NONE);
  	
  	Collection<byte[]> chunkedMobiText = chunk(encodedMobiText, DEFAULT_TEXT_CONTENT_RECORD_SIZE);
  	
  	mobiContents.addAll(firstContentIndex, wrapToMobiContent(chunkedMobiText));
  	mobiContents.add(firstContentIndex + chunkedMobiText.size(), MobiContentRecordFactory.createEndOfTextRecord());
  	
  	mobiHeader.setTextLength(mobiText.length());
  	mobiHeader.setRecordCount(chunkedMobiText.size());
  	mobiHeader.setRecordSize(DEFAULT_TEXT_CONTENT_RECORD_SIZE);
  	
  	// set non book index and first image index to the same value because there is no book index at this point,
  	// which is usually located between these two indices.
  	int mobiTextContentSize = chunkedMobiText.size() + 1;
  	adjustIndices(firstContentIndex + mobiTextContentSize, firstContentIndex + mobiTextContentSize);
  	adjustCoverAndThumbnailOffsets(mobiHeader.getFirstImageIndex());
  }

	private Collection<MobiContent> wrapToMobiContent(Collection<byte[]> chunkedMobiText) {
		return collect(chunkedMobiText, new Transformer<byte[], MobiContent>() {
			
			@Override
			public MobiContent transform(byte[] content) {
				return MobiContentRecordFactory.createContentRecord(content);
			}
		});
	}
  
  private void removeContent(int firstContentIndex, int lastContentIndex) {
  	mobiContents.removeAll(mobiContents.subList(firstContentIndex, lastContentIndex));
  }
  
  private void adjustIndices(int firstNonBookIndex, int firstImageIndex) {
  	mobiHeader.setFirstNonBookIndex(firstNonBookIndex);
  	mobiHeader.setFirstImageIndex(firstImageIndex);
  	
  	mobiHeader.setFcisRecordIndex(MobiUtils.findFirstContentsIndexByType(mobiContents, MobiContent.CONTENT_TYPE.FCIS));
  	mobiHeader.setFlisRecordIndex(MobiUtils.findFirstContentsIndexByType(mobiContents, MobiContent.CONTENT_TYPE.FLIS));
  	mobiHeader.setSrcsRecordIndex(MobiUtils.findFirstContentsIndexByType(mobiContents, MobiContent.CONTENT_TYPE.SRCS));
  	mobiHeader.setDatpRecordIndex(MobiUtils.findFirstContentsIndexByType(mobiContents, MobiContent.CONTENT_TYPE.DATP));
  	mobiHeader.setHuffmanRecordCount(0);
  	mobiHeader.setHuffmanRecordOffset(0);
  }
  
  private void adjustCoverAndThumbnailOffsets(int firstImageIndex) {
  	adjustExthOffsets(RECORD_TYPE.THUMBNAIL_OFFSET, CONTENT_TYPE.THUMBNAIL, firstImageIndex);
  	adjustExthOffsets(RECORD_TYPE.COVER_OFFSET, CONTENT_TYPE.COVER, firstImageIndex);  	
  }

	private void adjustExthOffsets(RECORD_TYPE recordType, CONTENT_TYPE contentType, int firstImageIndex) {
		List<EXTHRecord> exthRecords = mobiHeader.getEXTHRecords(recordType);
  	List<Integer> indices = MobiUtils.findAllContentsIndexByType(mobiContents, contentType);
  	if(exthRecords.size() != indices.size()) {
  		throw new IllegalArgumentException(String.format("Found %s thumbnail header but %s really exists.", exthRecords.size(), indices.size()));
  	}
  	for (int i = 0; i < exthRecords.size(); i++) {
  		exthRecords.get(i).setIntData(indices.get(i) - firstImageIndex);
		}
	}

}
