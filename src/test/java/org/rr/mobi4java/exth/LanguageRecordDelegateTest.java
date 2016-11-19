package org.rr.mobi4java.exth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.rr.mobi4java.EXTHRecordFactory;

public class LanguageRecordDelegateTest {

	private static final String VALID_LANGUAGE_CODE = "de";
	private static final String INVALID_LANGUAGE_CODE = "abc";

	@Test
	public void testListLanguageCodes() {
		List<String> languageCodes = LanguageRecordDelegate.getLanguageCodes();
		assertTrue(!languageCodes.isEmpty());
	}
	
	@Test
	public void testCreateValidLanguageCode() {
		LanguageRecordDelegate deLanguageRecord = EXTHRecordFactory.createLanguageRecord(VALID_LANGUAGE_CODE);
		assertEquals(VALID_LANGUAGE_CODE, deLanguageRecord.getLanguageCode());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateInvalidLanguageCode() {
		EXTHRecordFactory.createLanguageRecord(INVALID_LANGUAGE_CODE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInvalidLanguageCode() {
		LanguageRecordDelegate record = EXTHRecordFactory.createLanguageRecord(VALID_LANGUAGE_CODE);
		record.setLanguageCode(INVALID_LANGUAGE_CODE);
	}
}
