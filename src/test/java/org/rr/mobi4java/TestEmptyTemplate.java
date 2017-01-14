package org.rr.mobi4java;

import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.rr.mobi4java.MobiTestUtils.createJpegCover;
import static org.rr.mobi4java.MobiTestUtils.reReadDocument;
import static org.rr.mobi4java.MobiTestUtils.verifyRecordIndices;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class TestEmptyTemplate {

	@Test
	public void testCreateNewMobiDocument() throws IOException {
		MobiDocument doc = new MobiReader().empty();
		assertNotNull(doc);
		assertEquals("<html><head><guide></guide></head><body><p></p></body></html>", doc.getTextContent());
		assertEquals(EMPTY, doc.getFullName());
		assertTrue(doc.getImageContents().isEmpty());
		assertEquals(UTF_8, doc.getCharacterEncoding());
		assertTrue(doc.getImages().isEmpty());
		assertNull(doc.getThumbnail());
		verifyRecordIndices(doc.getMobiHeader(), doc.getMobiContents());
		
		MobiDocument newDoc = reReadDocument(doc);
		
		verifyRecordIndices(newDoc.getMobiHeader(), newDoc.getMobiContents());
	}

	@Test
	public void testCreateNewMobiDocumentAndAddThumbnailAndCover() throws IOException {
		MobiDocument doc = new MobiReader().empty();

		byte[] newCover = createJpegCover(100, 200);
		byte[] newThumbnail = createJpegCover(50, 100);
		
		doc.setThumbnail(newThumbnail);
		doc.setCover(newCover);
		
		MobiDocument newDoc = reReadDocument(doc);

		assertTrue(Arrays.equals(newCover, newDoc.getCover()));
		
		assertTrue(Arrays.equals(newCover, newDoc.getCover()));
		assertTrue(Arrays.equals(newThumbnail, newDoc.getThumbnail()));
	}
	
	@Test
	public void testCreateNewMobiDocumentAndAddCoverAndThumbnail() throws IOException {
		MobiDocument doc = new MobiReader().empty();

		byte[] newCover = createJpegCover(100, 200);
		byte[] newThumbnail = createJpegCover(50, 100);
		
		doc.setCover(newCover);
		doc.setThumbnail(newThumbnail);
		
		MobiDocument newDoc = reReadDocument(doc);

		assertTrue(Arrays.equals(newCover, newDoc.getCover()));
		assertTrue(Arrays.equals(newThumbnail, newDoc.getThumbnail()));
	}
	
	@Test
	public void testCreateNewMobiDocumentAndAddCover() throws IOException {
		MobiDocument doc = new MobiReader().empty();
		byte[] newCover = createJpegCover(100, 200);
		doc.setCover(newCover);
		MobiDocument newDoc = reReadDocument(doc);
		assertTrue(Arrays.equals(newCover, newDoc.getCover()));
	}
	
	@Test
	public void testCreateNewMobiDocumentAndAddThumbnail() throws IOException {
		MobiDocument doc = new MobiReader().empty();
		byte[] newThumbnail = createJpegCover(100, 200);
		doc.setThumbnail(newThumbnail);
		MobiDocument newDoc = reReadDocument(doc);
		assertTrue(Arrays.equals(newThumbnail, newDoc.getThumbnail()));
	}
	
	@Test
	public void testCreateNewMobiDocumentAndChangeContent() throws IOException {
		MobiDocument doc = new MobiReader().empty();
		
		// make large random content which will cause many content chunks in the mobi doc.
		Random random = new Random();
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < 700; i++) {
			b.append(random.nextLong()).append(" ");
		}
		
		String newContent = "<html><head></head><body><p>" + b + "</p></body></html>";
		doc.setTextContent(newContent);
		
		MobiDocument newDoc = reReadDocument(doc);
		
		assertEquals(newContent, newDoc.getTextContent());
	}
}
