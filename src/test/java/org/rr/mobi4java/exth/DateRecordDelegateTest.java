package org.rr.mobi4java.exth;

import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.EXTHRecordFactory;

public class DateRecordDelegateTest {
	
	@Test
	public void testDateRecord() throws ParseException {
		Date date = DateUtils.parseDate("2001-11-11", new String[] {"yyyy-MM-dd"});
		DateRecordDelegate record = EXTHRecordFactory.createDateRecord(RECORD_TYPE.PUBLISHING_DATE, date);
		
		assertEquals(date, record.getAsDate());
	}
	
	@Test
	public void testChangeDateRecord() throws ParseException {
		Date date = DateUtils.parseDate("2001-11-11", new String[] {"yyyy-MM-dd"});
		DateRecordDelegate record = EXTHRecordFactory.createDateRecord(RECORD_TYPE.PUBLISHING_DATE, date);
		Date changed = DateUtils.parseDate("2004-04-28", new String[] {"yyyy-MM-dd"});
		record.setDateData(changed);
		
		assertEquals(changed, record.getAsDate());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullRecord() throws ParseException {
		EXTHRecordFactory.createDateRecord(RECORD_TYPE.PUBLISHING_DATE, null);
	}
	
	@Test(expected=ParseException.class)
	public void testInvalidDateRecord() throws ParseException, UnsupportedEncodingException {
		Date date = DateUtils.parseDate("2001-11-11", new String[] {"yyyy-MM-dd"});
		DateRecordDelegate record = EXTHRecordFactory.createDateRecord(RECORD_TYPE.PUBLISHING_DATE, date);
		record.setStringData("23+09+2016", UTF_8);
		record.getAsDate();
	}
}
