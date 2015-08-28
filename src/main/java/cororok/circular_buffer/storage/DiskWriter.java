package cororok.circular_buffer.storage;

import java.io.IOException;

public abstract class DiskWriter implements AutoCloseable {

	abstract public void seek(long pos) throws IOException;

	abstract public void write(byte[] b) throws IOException;

	abstract public void write(byte[] b, int off, int len) throws IOException;

	abstract public int read(byte[] b, int off, int len) throws IOException;

	abstract public int read(byte[] b) throws IOException;

	/**
	 * @param range {from, to} or {from1, to1, from2, to2}
	 * @return
	 * @throws IOException
	 */
	public byte[] readStorage(long[] range) throws IOException {
		byte[] result;
		seek(range[0]);

		int size1 = (int) (range[1] - range[0]);
		if (range.length == 2) {
			result = new byte[size1];
			read(result);
		} else {// two pieces
			int size2 = (int) (range[3] - range[2]);
			result = new byte[size1 + size2];
			read(result, 0, size1); // part 1

			seek(range[2]); // part 2
			read(result, size1, size2);
		}
		return result;
	}

	/**
	 * @param range {from, to} or {from1, to1, from2, to2}
	 * @param bs
	 * @throws IOException
	 */
	public void writeStorage(final long[] range, byte[] bs) throws IOException {
		seek(range[0]);
		int size1 = (int) (range[1] - range[0]);
		if (range.length == 2) {
			write(bs);
		} else {// two pieces
			int size2 = (int) (range[3] - range[2]);
			write(bs, 0, size1); // part 1

			seek(range[2]); // part 2
			write(bs, size1, size2);
		}
	}
}