package org.rr.mobi4java;

import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.rr.mobi4java.MobiTestUtils.createJpegCover;
import static org.rr.mobi4java.MobiTestUtils.createReader;
import static org.rr.mobi4java.MobiTestUtils.getResourceData;
import static org.rr.mobi4java.MobiTestUtils.reReadDocument;
import static org.rr.mobi4java.MobiTestUtils.readDoc;
import static org.rr.mobi4java.MobiTestUtils.writeDoc;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.rr.mobi4java.exth.DateRecordDelegate;
import org.rr.mobi4java.exth.ISBNRecordDelegate;
import org.rr.mobi4java.exth.StringRecordDelegate;


public class MobiTestJapaneseTairytalesBook {

	private static final String JAPANESE_FAIRYTALES_MOBI = "/japanese_fairytales.mobi";

	@Test
	public void testReadWriteMobiFile() throws IOException {
		byte[] mobiData = getResourceData(JAPANESE_FAIRYTALES_MOBI);
		MobiDocument doc = createReader(mobiData);

		verifyPg23393MobiContent(doc);
		
		byte[] newMobiData = writeDoc(doc);
		
		assertEquals(mobiData.length, newMobiData.length);

		MobiDocument newDoc = createReader(newMobiData);
		verifyPg23393MobiContent(newDoc);
	}

	/**
	 * Test the structure data of the known mobi file japanese_fairytales.mobi.
	 */
	protected void verifyPg23393MobiContent(MobiDocument doc) throws IOException {
		assertEquals("Japanische Märchen", doc.getFullName());
		
		String textContent = doc.getTextContent();
		assertTrue(textContent.startsWith("<html>"));
		assertTrue(textContent.endsWith("</html>"));

		assertEquals(3, doc.getImages().size());
		assertNotNull(doc.getCover());
		assertNotNull(doc.getThumbnail());
		
		assertEquals(150, doc.getPdbHeader().getRecordCount());
		assertEquals(22, doc.getMobiHeader().getEXTHRecords().size());
		
		List<StringRecordDelegate> subjectRecords = doc.getMetaData().getSubjectRecords();
		assertEquals("Fairy tales", subjectRecords.get(0).getAsString(UTF_8));
		assertEquals("Folklore -- Japan", subjectRecords.get(1).getAsString(UTF_8));
		
		DateRecordDelegate dateRecordDelegate = doc.getMetaData().getPublishingDateRecords().get(0);
		assertEquals("2007-11-07", dateRecordDelegate.getAsString(UTF_8));
		
		assertEquals("de", doc.getMetaData().getLanguageRecord().getLanguageCode());
	}
	
	@Test
	public void testChangeCover() throws IOException {
		byte[] mobiData = getResourceData(JAPANESE_FAIRYTALES_MOBI);
		MobiDocument doc = createReader(mobiData);
		
		byte[] cover = createJpegCover(100, 200);
		doc.setCover(cover);
		
		MobiDocument newDoc = reReadDocument(doc);
		
		assertTrue(Arrays.equals(cover, newDoc.getCover()));

		verifyPg23393MobiContent(newDoc);
	}
	
	@Test
	public void testChangeFullName() throws IOException {
		byte[] mobiData = IOUtils.toByteArray(getClass().getResourceAsStream(JAPANESE_FAIRYTALES_MOBI));
		MobiDocument doc = readDoc(mobiData);

		doc.setFullName("TEST");

		MobiDocument newDoc = reReadDocument(doc);
		
		assertEquals("TEST", newDoc.getFullName());
	}

	@Test
	public void testAddISBN() throws IOException {
		String isbn13 = "978-3-12-004811-4";
		byte[] mobiData = IOUtils.toByteArray(getClass().getResourceAsStream(JAPANESE_FAIRYTALES_MOBI));
		MobiDocument doc = readDoc(mobiData);

		ISBNRecordDelegate recordDelegate = EXTHRecordFactory.createISBNRecord(isbn13);
		doc.getMetaData().addEXTHRecord(recordDelegate);
		
		MobiDocument newDoc = reReadDocument(doc);
		
		List<ISBNRecordDelegate> isbnRecords = newDoc.getMetaData().getISBNRecords();
		assertTrue(isbnRecords.size() == 1);
		
		ISBNRecordDelegate isbnRecord = isbnRecords.get(0);
		
		assertEquals(isbn13, isbnRecord.getAsString(doc.getCharacterEncoding()));
		assertTrue(isbnRecord.isIsbn13());
		assertEquals("9783120048114", isbnRecord.getAsIsbn13());
		assertEquals("3120048119", isbnRecord.getAsIsbn10());
	}
	
}
