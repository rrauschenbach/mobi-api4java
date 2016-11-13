package org.rr.mobi4java.exth;

import org.rr.mobi4java.EXTHRecord;

/**
 * The interface which must be implemented by all record delegates.
 */
public interface RecordDelegate {

	/**
	 * @return The underlying {@link EXTHRecord} instance which is covered by this {@link RecordDelegate}.
	 */
	public EXTHRecord getRecord();
	
}
