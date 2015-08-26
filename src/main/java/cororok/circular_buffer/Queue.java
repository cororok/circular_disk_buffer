package cororok.circular_buffer;

import java.io.IOException;

public interface Queue extends AbstractQueueAndStack {

	/**
	 * @param bs
	 * @return
	 * @throws IOException
	 *             if it has not enough space it returns IOException
	 */
	boolean addLast(byte[] bs) throws IOException;

}
