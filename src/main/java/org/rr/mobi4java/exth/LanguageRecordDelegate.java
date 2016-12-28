package org.rr.mobi4java.exth;

import static org.rr.mobi4java.ByteUtils.getBytes;
import static org.rr.mobi4java.ByteUtils.getString;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.rr.mobi4java.EXTHRecord;

public class LanguageRecordDelegate implements RecordDelegate {

	private static List<String> languageCodes;

	static {
		try {
			languageCodes = IOUtils.readLines(LanguageRecordDelegate.class.getResourceAsStream("/languagecodes.txt"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load language codes.", e);
		}
	}
	
	private EXTHRecord record;
	
	public LanguageRecordDelegate(EXTHRecord record) {
		this.record = record;
	}

	/**
	 * Get a list of all possible mobi language codes. One of this codes can be for example "de" for "german" or "de-at" for austrian german.
	 * 
	 * @return A list with mobi language codes.
	 * @see languagecodes.txt
	 */
	public static List<String> getLanguageCodes() {
		return languageCodes;
	}

	/**
	 * Set the language code. This code must be one of this also delivered with the {@link #getLanguageCodes()} method.
	 * 
	 * @param code The code to be set as data to this {@link LanguageRecordDelegate} instance.
	 * @throws IllegalArgumentException if the given code is not valid.
	 * @see languagecodes.txt
	 */
	public void setLanguageCode(String code) {
		if(code == null) {
			throw new IllegalArgumentException("Language code must no be null.");
		}
		
		for (String languageCode : languageCodes) {
			if (StringUtils.equals(code, languageCode)) {
				record.setData(getBytes(languageCode));
				return;
			}
		}
		throw new IllegalArgumentException("Invalid language code " + code);
	}

	/**
	 * @return The current language code value which is one of these returned with the codes from {@link #getLanguageCode()}.
	 * @see languagecodes.txt
	 */
	public String getLanguageCode() {
		return getString(record.getData());
	}

	@Override
	public EXTHRecord getRecord() {
		return record;
	}
}
