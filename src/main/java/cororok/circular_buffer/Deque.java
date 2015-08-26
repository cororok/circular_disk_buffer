package cororok.circular_buffer;

import java.io.IOException;

public interface Deque extends Stack, Queue {

	/**
	 * Read data and remove them.
	 * 
	 * @return
	 * @throws IOException,
	 *             NoSuchElementException if it is empty
	 */
	byte[] removeLast() throws IOException;

	/**
	 * Read data only.
	 * 
	 * @return null if it is empty
	 * @throws IOException
	 */
	byte[] peekLast() throws IOException;

}