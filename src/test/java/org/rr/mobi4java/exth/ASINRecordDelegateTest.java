package org.rr.mobi4java.exth;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.rr.mobi4java.EXTHRecordFactory;

public class ASINRecordDelegateTest {
	
	private static final String VALID_ASIN = "548445857X";
	
	@Test
	public void testFormattedASINInput() throws IOException {
		ASINRecordDelegate record = EXTHRecordFactory.createASINRecord(VALID_ASIN);
		assertEquals(VALID_ASIN, record.getASIN());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetToEmpty() throws IOException {
		ASINRecordDelegate record = EXTHRecordFactory.createASINRecord(VALID_ASIN);
		record.setASIN("");
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void testSetToNull() throws IOException {
		ASINRecordDelegate record = EXTHRecordFactory.createASINRecord(VALID_ASIN);
		record.setASIN(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testASINWithSpecialCaharacters() throws IOException {
		ASINRecordDelegate record = EXTHRecordFactory.createASINRecord(VALID_ASIN);
		record.setASIN("abc_fghjkl");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetToWronLength() throws IOException {
		ASINRecordDelegate record = EXTHRecordFactory.createASINRecord(VALID_ASIN);
		record.setASIN("123");
	}
}
