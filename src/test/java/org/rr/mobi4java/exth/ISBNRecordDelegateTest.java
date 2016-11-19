package org.rr.mobi4java.exth;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.rr.mobi4java.EXTHRecordFactory;

public class ISBNRecordDelegateTest {
	
	private static final String ISBN13_WITH_HYPHEN = "978-3-12-004811-4";
	
	private static final String ISBN10_WITH_HYPEN = "3-12-004811-9";
	
	private static final String ISBN13_RAW = "9783120048114";
	
	private static final String ISBN10_RAW = "3120048119";

	@Test
	public void testFormattedISBN13Input() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN13_WITH_HYPHEN);
		assertEquals(ISBN13_WITH_HYPHEN, record.getIsbn());
		assertEquals(ISBN10_RAW, record.getAsIsbn10());
		assertEquals(ISBN13_RAW, record.getAsIsbn13());
	}
	
	@Test
	public void testNumericISBN13Input() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN13_RAW);
		assertEquals(ISBN13_RAW, record.getIsbn());
		assertEquals(ISBN10_RAW, record.getAsIsbn10());
		assertEquals(ISBN13_RAW, record.getAsIsbn13());
	}
	
	@Test
	public void testFormattedISBN10Input() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN10_WITH_HYPEN);
		assertEquals(ISBN10_WITH_HYPEN, record.getIsbn());
		assertEquals(ISBN10_RAW, record.getAsIsbn10());
		assertEquals(ISBN13_RAW, record.getAsIsbn13());
	}
	
	@Test
	public void testNumericISBN10Input() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN10_RAW);
		assertEquals(ISBN10_RAW, record.getIsbn());
		assertEquals(ISBN10_RAW, record.getAsIsbn10());
		assertEquals(ISBN13_RAW, record.getAsIsbn13());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateWithNull() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN10_RAW);
		record.setISBN(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetToEmpty() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN10_RAW);
		record.setISBN("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetToNull() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN10_RAW);
		record.setISBN(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetToNotNumeric() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN10_RAW);
		record.setISBN("abcgfghjkl");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetToWronLength() throws IOException {
		ISBNRecordDelegate record = EXTHRecordFactory.createISBNRecord(ISBN10_RAW);
		record.setISBN("123");
	}
}
