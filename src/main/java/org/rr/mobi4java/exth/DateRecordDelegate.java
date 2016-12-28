package org.rr.mobi4java.exth;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.rr.mobi4java.EXTHRecord;

public class DateRecordDelegate extends StringRecordDelegate implements RecordDelegate {
	
	private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	private static final String[] DATE_PATTERN = new String[] { DEFAULT_DATE_FORMAT_PATTERN, "yyyy-MM-dd", "dd.MM.yyyy", "MM/dd/yyyy",
			"yyyy/MM/dd", "yyyy.dd.MM", "yyyy-'W'ww-d", "yyyy-MM-dd hh:mm'Z'", "yyyy-MM-dd hh:mm:Z", "EEE, dd MMM yyyy HH:mm:ss z",
			"EEE, dd MMM yyyy HH:mm z", "dd MMM yyyy HH:mm:ss z", "EEE MMM dd HH:mm:ss z yyyy", "yyyyMMddhhmmssZ", "yyyyMMddhhmmZ",
			"yyyyMMddhhmmss", "yyyyMMddhhmm", "yyyyMMddhhmmZ", "'D:'yyyyMMddHHmmZ", "yyyy" };
	
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
		try {
			return DatatypeConverter.parseDate(getAsString(UTF_8)).getTime();
		} catch(Exception e) {
			return DateUtils.parseDate(getAsString(UTF_8), DATE_PATTERN);
		}
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
			setStringData(DateFormatUtils.format(date, DEFAULT_DATE_FORMAT_PATTERN), UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to set date " + date, e);
		}
	}
}
