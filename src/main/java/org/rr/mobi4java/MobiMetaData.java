package org.rr.mobi4java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rr.mobi4java.EXTHRecord.RECORD_TYPE;
import org.rr.mobi4java.exth.ASINRecordDelegate;
import org.rr.mobi4java.exth.DateRecordDelegate;
import org.rr.mobi4java.exth.ISBNRecordDelegate;
import org.rr.mobi4java.exth.LanguageRecordDelegate;
import org.rr.mobi4java.exth.RecordDelegate;
import org.rr.mobi4java.exth.StringRecordDelegate;

public class MobiMetaData {

	private MobiContentHeader mobiHeader;
	
	MobiMetaData(MobiContentHeader mobiHeader) {
		this.mobiHeader = mobiHeader;
	}
	
	/**
	 * Remove a record instance which is a member of the meta data of the mobi document.
	 * 
	 * @param record The record to be removed. This record must be a record instance which was previously fetched from this
	 *          {@link MobiMetaData} instance.
	 * @return <code>true</code> if the given element has been removed.
	 */
	public boolean removeEXTHRecord(EXTHRecord record ) {
		return mobiHeader.getEXTHRecords().remove(record);
	}
	
	/**
	 * Remove all record instances which are a member of the meta data of the mobi document.
	 * 
	 * @param records The records to be removed. This records must be an instance which was previously fetched from this
	 *          {@link MobiMetaData} instance.
	 */
	public void removeEXTHRecords(EXTHRecord ... records) {
		for (EXTHRecord record : records) {
			mobiHeader.getEXTHRecords().remove(record);			
		}
	}

	/**
	 * Remove all records from the meta data of the mobi document.
	 */
	public void removeAllEXTHRecords() {
		mobiHeader.getEXTHRecords().clear();
	}
	
	/**
	 * Adds a record to the meta data of the mobi document. Use {@link EXTHRecordFactory} to create new records.
	 * 
	 * @param record The record to be added.
	 * @throws IllegalArgumentException if the given record is <code>null</code>.
	 */
	public void addEXTHRecord(EXTHRecord record) {
		if(record == null) {
			throw new IllegalArgumentException("Adding null records is not allowed.");
		}
		mobiHeader.getEXTHRecords().add(record);
	}
	
	/**
	 * Adds a record to the meta data of the mobi document. Use {@link EXTHRecordFactory} to create new records.
	 * 
	 * @param record The record to be added.
	 * @throws IllegalArgumentException if the given record is <code>null</code>.
	 */
	public void addEXTHRecord(RecordDelegate record) {
		addEXTHRecord(record.getRecord());
	}
	
	/**
	 * Get a modifiable list which contains all meta data records from the mobi header. Records which gets modified will have a direct effect
	 * to this {@link MobiDocument} instance.
	 * 
	 * @return All available records from the mobi header. The result {@link List} is not modifiable. Use {@link #addEXTHRecord(EXTHRecord)}
	 *         and {@link #removeEXTHRecord(EXTHRecord)} to add or remove meta data.
	 */
	public List<EXTHRecord> getEXTHRecords() {
		return Collections.unmodifiableList(mobiHeader.getEXTHRecords());
	}
	
	/**
	 * Get the language record from the exth records. Changes to the returned record delegate will be applied to the real record which must be
	 * a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The language record delegate or <code>null</code> if the mobi header have no language record defined.
	 * @see #getEXTHRecords()
	 */
	public LanguageRecordDelegate getLanguageRecord() {
		List<EXTHRecord> records = MobiUtils.findRecordsByType(getEXTHRecords(), RECORD_TYPE.LANGUAGE);
		if(!records.isEmpty()) {
			return new LanguageRecordDelegate(records.get(0));
		}
		return null;
	}
	
	/**
	 * Get the 'subject' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'subject' record delegates or an empty list if the mobi header have no 'subject' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getSubjectRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.SUBJECT);
	}
	
	/**
	 * Get the 'author' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'author' record delegates or an empty list if the mobi header have no 'author' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getAuthorRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.AUTHOR);
	}
	
	/**
	 * Get the 'publisher' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'publisher' record delegates or an empty list if the mobi header have no 'publisher' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getPublisherRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.PUBLISHER);
	}
	
	/**
	 * Get the 'publishing date' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'publishing date' record delegates or an empty list if the mobi header have no 'publishing date' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<DateRecordDelegate> getPublishingDateRecords() {
		return MobiUtils.createDateRecords(getEXTHRecords(), RECORD_TYPE.PUBLISHING_DATE);
	}
	
	/**
	 * Get the 'review' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'review' record delegates or an empty list if the mobi header have no 'review' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getReviewRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.REVIEW);
	}

	/**
	 * Get the 'contributor' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'contributor' record delegates or an empty list if the mobi header have no 'contributor' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getContributorRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.CONTRIBUTOR);
	}
	
	/**
	 * Get the 'rights' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'rights' record delegates or an empty list if the mobi header have no 'rights' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getRightsRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.RIGHTS);
	}
	
	/**
	 * Get the 'source' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'source' record delegates or an empty list if the mobi header have no 'source' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getSourceRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.SOURCE);
	}
	
	/**
	 * Get the 'imprint' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'imprint' record delegates or an empty list if the mobi header have no 'imprint' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getImprintRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.IMPRINT);
	}
	
	/**
	 * Get the 'description' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'description' record delegates or an empty list if the mobi header have no 'description' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<StringRecordDelegate> getDescriptionRecords() {
		return MobiUtils.createStringRecords(getEXTHRecords(), RECORD_TYPE.DESCRIPTION);
	}
	
	/**
	 * Get the 'isbn' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'isbn' record delegates or an empty list if the mobi header have no 'isbn' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<ISBNRecordDelegate> getISBNRecords() {
		List<EXTHRecord> records = MobiUtils.findRecordsByType(getEXTHRecords(), RECORD_TYPE.ISBN);
		List<ISBNRecordDelegate> recordDelegates = new ArrayList<>(records.size());
		for (EXTHRecord record : records) {
			recordDelegates.add(new ISBNRecordDelegate(record));
		}
		return recordDelegates;
	}
	
	/**
	 * Get the 'asin' records from the exth records. Changes to the returned record delegates will be applied to the real record which must
	 * be a member from the records which can be fetched with the {@link #getEXTHRecords()} method.
	 * 
	 * @return The 'asin' record delegates or an empty list if the mobi header have no 'asin' records defined. Never returns
	 *         <code>null</code>.
	 * @see #getEXTHRecords()
	 */
	public List<ASINRecordDelegate> getASINRecords() {
		List<EXTHRecord> records = MobiUtils.findRecordsByType(getEXTHRecords(), RECORD_TYPE.ASIN);
		List<ASINRecordDelegate> recordDelegates = new ArrayList<>(records.size());
		for (EXTHRecord record : records) {
			recordDelegates.add(new ASINRecordDelegate(record));
		}
		return recordDelegates;
	}
}
	
