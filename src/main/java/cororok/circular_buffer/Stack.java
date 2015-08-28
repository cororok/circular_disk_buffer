package cororok.circular_buffer;

import java.io.IOException;

public interface Stack extends AbstractQueueAndStack {

	/**
	 * @param bs
	 * @throws IOException
	 *             if it has not enough space it returns IOException
	 */
	void addFirst(byte[] bs) throws IOException;

}