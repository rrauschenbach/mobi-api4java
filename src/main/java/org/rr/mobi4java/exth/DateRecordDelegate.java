package org.rr.mobi4java.exth;

import static org.apache.commons.lang.CharEncoding.UTF_8;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.rr.mobi4java.EXTHRecord;

public class DateRecordDelegate extends StringRecordDelegate implements RecordDelegate {
	
	private static final String[] DATE_PATTERN = new String[] {"yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy"};
	
	public DateRecordDelegate(EXTHRecord record) {
		super(record);
	}

	/**
	 * Get the data of this {@link EXTHRecord} instance as date.
	 * 
	 * @return A new {@link Date} instance from the data of this {@link EXTHRecord} instance.
	 * @throws ParseException 
	 */
	public Date getAsDate() throws ParseException {
		return DateUtils.parseDate(getAsString(UTF_8), DATE_PATTERN);
	}

	/**
	 * Set a date as value to this {@link EXTHRecord} instance.
	 * 
	 * @param date The date used as value for this {@link EXTHRecord} instance.
	 * @throws IllegalArgumentException if the given date is <code>null</code>.
	 */
	public void setDateData(Date date) {
		if(date == null) {
			throw new IllegalArgumentException("Value must not be null.");
		}
		try {
			setStringData(DateFormatUtils.format(date, "yyyy-MM-dd'T'HH:mm:ssZ"), UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to set date " + date, e);
		}
	}
}
