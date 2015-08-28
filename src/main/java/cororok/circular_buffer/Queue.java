package cororok.circular_buffer;

import java.io.IOException;

public interface Queue extends AbstractQueueAndStack {

	/**
	 * @param bs
	 * @throws IOException
	 *             if it has not enough space it returns IOException
	 */
	void addLast(byte[] bs) throws IOException;

}
