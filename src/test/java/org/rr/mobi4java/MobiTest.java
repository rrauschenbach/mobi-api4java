package org.rr.mobi4java;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.rr.mobi4java.exth.DateRecordDelegate;
import org.rr.mobi4java.exth.ISBNRecordDelegate;
import org.rr.mobi4java.exth.StringRecordDelegate;
import static org.apache.commons.lang.CharEncoding.UTF_8;
import static org.apache.commons.lang.CharEncoding.UTF_8;


public class MobiTest {

	@Test
	public void testReadWriteMobiFile() throws IOException {
		byte[] mobiData = getResourceData("/pg23393.mobi");
		MobiDocument doc = createReader(mobiData);
		
		verifyPg23393MobiContent(doc);
		
		byte[] newMobiData = writeDoc(doc);
		
		assertEquals(mobiData.length, newMobiData.length);

		MobiDocument newDoc = createReader(newMobiData);
		verifyPg23393MobiContent(newDoc);
	}

	/**
	 * Test the structure data of the known mobi file pg23393.mobi.
	 */
	protected void verifyPg23393MobiContent(MobiDocument doc) throws IOException {
		assertEquals("Japanische Märchen", doc.getFullName());
		
		String textContent = doc.getTextContent();
		assertTrue(textContent.startsWith("<html>"));
		assertTrue(textContent.endsWith("</html>"));

		assertEquals(2, doc.getImages().size());
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
		byte[] mobiData = getResourceData("/pg23393.mobi");
		MobiDocument doc = createReader(mobiData);
		
		// set new cover image
		byte[] cover = getResourceData("/test-cover.jpg");
		doc.setCover(cover);
		
		// write and reread doc
		byte[] newMobiData = writeDoc(doc);
		MobiDocument newDoc = createReader(newMobiData);
		
		assertTrue(Arrays.equals(cover, newDoc.getCover()));

		verifyPg23393MobiContent(newDoc);
	}
	
	@Test
	public void testChangeFullName() throws IOException {
		byte[] mobiData = IOUtils.toByteArray(MobiTest.class.getResourceAsStream("/pg23393.mobi"));
		MobiDocument doc = readDoc(mobiData);

		doc.setFullName("TEST");

		byte[] newMobiData = writeDoc(doc);
		MobiDocument newDoc = readDoc(newMobiData);
		
		assertEquals("TEST", newDoc.getFullName());
	}

	@Test
	public void testAddISBN() throws IOException {
		String isbn13 = "978-3-12-004811-4";
		byte[] mobiData = IOUtils.toByteArray(MobiTest.class.getResourceAsStream("/pg23393.mobi"));
		MobiDocument doc = readDoc(mobiData);

		ISBNRecordDelegate recordDelegate = EXTHRecordFactory.createISBNRecord(isbn13);
		doc.getMetaData().addEXTHRecord(recordDelegate);
		
		byte[] newMobiData = writeDoc(doc);
		MobiDocument newDoc = readDoc(newMobiData);
		
		List<ISBNRecordDelegate> isbnRecords = newDoc.getMetaData().getISBNRecords();
		assertTrue(isbnRecords.size() == 1);
		
		ISBNRecordDelegate isbnRecord = isbnRecords.get(0);
		
		assertEquals(isbn13, isbnRecord.getAsString(doc.getCharacterEncoding()));
		assertTrue(isbnRecord.isIsbn13());
		assertEquals("9783120048114", isbnRecord.getAsIsbn13());
		assertEquals("3120048119", isbnRecord.getAsIsbn10());
	}
	
	private MobiDocument createReader(byte[] mobiData) throws IOException {
		MobiDocument doc = readDoc(mobiData);
		return doc;
	}

	private byte[] getResourceData(String resource) throws IOException {
		return IOUtils.toByteArray(MobiTest.class.getResourceAsStream(resource));
	}

	private MobiDocument readDoc(byte[] newMobiData) throws IOException {
		return new MobiReader().read(new ByteArrayInputStream(newMobiData));
	}

	private byte[] writeDoc(MobiDocument doc) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new MobiWriter().write(doc, out);
		return out.toByteArray();
	}
	
}
