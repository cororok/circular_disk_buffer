package cororok.circular_buffer;

import java.util.ConcurrentModificationException;

/**
 * It is intentionally NOT a Iterator because user should close it manually or automatically.
 * 
 * @author songduk.park cororok@gmail.com
 */
public interface AutoCloseableIter extends AutoCloseable {

	boolean hasNext();

	/**
	 * doesn't throw NoSuchElementException
	 * 
	 * @return
	 * @throws ConcurrentModificationException
	 *             If add or remove happened.
	 * @throws Exception
	 *             empty or any other error
	 */
	byte[] next() throws ConcurrentModificationException, Exception;
}
