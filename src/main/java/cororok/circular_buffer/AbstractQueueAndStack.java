package cororok.circular_buffer;

import java.io.IOException;

/**
 * Queue and Stack have common methods, both read data from the first. Queue adds data at the last while Stack does at
 * the first.
 * 
 * @author spark
 *
 */
public interface AbstractQueueAndStack extends AutoCloseable {

	/**
	 * Read data and remove them.
	 * 
	 * @return
	 * @throws IOException,
	 *             NoSuchElementException if it is empty
	 */
	byte[] removeFirst() throws IOException;

	/**
	 * Read data only.
	 * 
	 * @return null if it is empty
	 * @throws IOException
	 */
	byte[] peekFirst() throws IOException;

	/**
	 * @return number of elements it has now.
	 */
	long size();

	/**
	 * @return total space it uses to keep the byte[] excluding header
	 */
	long length();

	/**
	 * @return total space it uses to keep the byte[] including header
	 */
	long lengthOfStorage();

	/**
	 * @return available free space without counting header size
	 */
	long getAvailableTotalSpace();

	/**
	 * same as getAvailableTotalSpace() - header size
	 * 
	 * @return available free space including header size
	 */
	long getAvailableSpace();
}
