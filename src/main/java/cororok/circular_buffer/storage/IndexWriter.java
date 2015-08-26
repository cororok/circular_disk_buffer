package cororok.circular_buffer.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IndexWriter implements AutoCloseable {

	DiskWriter writer;

	byte[] bs = new byte[8 * 3]; // start point + size + end point
	private ByteBuffer bb = ByteBuffer.allocate(8);

	public IndexWriter(DiskWriter writer) throws FileNotFoundException {
		this.writer = writer;
	}

	/**
	 * @return 3 size of array which is {start_point, size, end_point}
	 * @throws IOException
	 */
	public long[] readAll() throws IOException {
		writer.seek(0);
		writer.read(bs);

		return new long[] { toLong(0), toLong(8), toLong(16) };
	}

	public void toByte(long n, int offset) {
		bb.rewind();
		bb.putLong(n).rewind();
		bb.get(bs, offset, 8);
	}

	long toLong(int offset) {
		bb.rewind();
		bb.put(bs, offset, 8);
		bb.rewind();
		return bb.getLong();
	}

	public void writeStartAndSize(long start, long size) throws IOException {
		writer.seek(0);
		toByte(start, 0);
		toByte(size, 8);
		writer.write(bs, 0, 16);
	}

	public void writeEndAndSize(long end, long size) throws IOException {
		writer.seek(8);
		toByte(size, 8);
		toByte(end, 16);
		writer.write(bs, 8, 16);
	}

	@Override
	public void close() throws Exception {
		writer.close();
	}

}
