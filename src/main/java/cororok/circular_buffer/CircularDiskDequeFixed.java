package cororok.circular_buffer;

import java.io.IOException;

/**
 * If data is fixed length then it does not need header to keep the length because it already knows the length therefore
 * it is better than {@link CircularDiskQueueAndStack}
 * 
 * @author songduk.park cororok@gmail.com
 */
public class CircularDiskDequeFixed extends CircularDiskDeque {

	final long fixedSize;

	public CircularDiskDequeFixed(long capacity, String fileName, long fixedSize) throws IOException {
		super(capacity, fileName);
		this.fixedSize = fixedSize;
	}

	@Override
	void initSizeAndLength() {
		this.size = this.info.size(); // info counts data only
		this.length = this.info.length(); // the same
	}

	@Override
	protected void writeFirst(final byte[] bs) throws IOException {
		write(info.addFirst(bs.length), bs);
	}

	@Override
	protected void writeLast(final byte[] bs) throws IOException {
		write(info.addLast(bs.length), bs);
	}

	private void write(final long[] range, final byte[] bs) throws IOException {
		if (bs.length != fixedSize)
			throw new IOException("wrong input size " + bs.length + ", expected " + fixedSize);

		writer.writeStorage(range, bs);
	}

	@Override
	byte[] readFirst() throws IOException {
		long[] range = info.removeFirst(fixedSize);
		return writer.readStorage(range);
	}

	@Override
	byte[] readFirstToRemove() throws IOException {
		return readFirst();
	}

	@Override
	byte[] readLast() throws IOException {
		long[] range = info.removeLast(fixedSize);
		return writer.readStorage(range);
	}

	@Override
	byte[] readLastToRemove() throws IOException {
		return readLast();
	}

	@Override
	public long getAvailableSpace() {
		return info.getAvailableSpace();
	}

	@Override
	public long getHeaderSize() {
		return 0;
	}
}
