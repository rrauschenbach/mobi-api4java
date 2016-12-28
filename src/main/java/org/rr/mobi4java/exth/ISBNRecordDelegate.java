package org.rr.mobi4java.exth;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.rr.mobi4java.EXTHRecord;

public class ISBNRecordDelegate extends StringRecordDelegate implements RecordDelegate {

	private static final String ISBN13_PREFIX = "978";
	
	private static final String CheckDigits = "0123456789X0";

	public ISBNRecordDelegate(EXTHRecord record) {
		super(record);
	}
	
	public void setISBN(String isbn) {
		try {
			if(isValidISBN(isbn)) {
				setStringData(isbn, UTF_8);
			} else {
				throw new IllegalArgumentException("The isbn " + isbn + " is not a valid isbn number.");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to set isbn " + isbn, e);
		}
	}
	
	public boolean isIsbn13() {
		return StringUtils.startsWith(getIsbn(), ISBN13_PREFIX) && removeHyphen(getIsbn()).length() == 13;
	}

	public String getIsbn() {
		return getAsString(UTF_8);
	}
	
	public String getAsIsbn13() {
		if(isIsbn13()) {
			return removeHyphen(getIsbn());
		}
		return isbn10To13(getIsbn());
	}
	
	public String getAsIsbn10() {
		if(!isIsbn13()) {
			return removeHyphen(getIsbn());
		}
		return isbn13To10(getIsbn());
	}

	private boolean isValidISBN(String isbn) {
		if(isbn == null) {
			return false;
		}
		
		return isValidISBN10(isbn) || isValidISBN13(isbn);
		
	}
	
	protected boolean isValidISBN13(String isbn) {
		return Pattern.compile("97(?:8|9)([ -]?)\\d{1,5}\\1\\d{1,7}\\1\\d{1,6}\\1\\d").matcher(isbn).matches();
	}
	
	protected boolean isValidISBN10(String isbn) {
		return Pattern.compile("\\d{1,5}([- ]?)\\d{1,7}\\1\\d{1,6}\\1(\\d|X)").matcher(isbn).matches();
	}

	private static String removeHyphen(String isbn) {
		return StringUtils.remove(isbn, "-");
	}

	/**
	 * Change the given character to its integer value.
	 * 
	 * @return the integer value for the given character.
	 */
	private static int charToInt(char c) {
		return Character.getNumericValue(c);
	}

	private static String isbn13To10(String isbn) {
		int n = 0;
		String s9 = removeHyphen(isbn).substring(3, 12);
		for (int i = 0; i < 9; i++) {
			int v = charToInt(s9.charAt(i));
			if (v == -1) {
				throw new IllegalArgumentException("Failed to convert isbn number " + isbn);
			} else {
				n = n + (10 - i) * v;
			}
		}
		n = 11 - (n % 11);
		return s9 + CheckDigits.substring(n, n + 1);
	}

	private static String isbn10To13(String isbn) {
		int n = 0;
		String s12 = ISBN13_PREFIX + removeHyphen(isbn).substring(0, 9);
		for (int i = 0; i < 12; i++) {
			int v = charToInt(s12.charAt(i));
			if (v == -1) {
				throw new IllegalArgumentException("Failed to convert isbn number " + isbn);
			} else {
				if ((i % 2) == 0) {
					n = n + v;
				} else {
					n = n + 3 * v;
				}
			}
		}
		n = n % 10;
		if (n != 0) {
			n = 10 - n;
		}
		return s12 + CheckDigits.substring(n, n + 1);
	}
}
