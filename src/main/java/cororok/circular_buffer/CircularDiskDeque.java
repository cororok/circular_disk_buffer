package cororok.circular_buffer;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Because it has to read data from both left(first) and right(last) side it should keep duplicated size header in both
 * left and right of data so the data format is [length][binary data][the same length] and repeats them.
 * 
 * {@link CircularDiskQueueAndStack} is better unless it needs additional read from the last.
 * 
 * @author songduk.park cororok@gmail.com
 */
public class CircularDiskDeque extends CircularDiskQueueAndStack implements Deque {

	private static long headerSize = CircularDiskQueueAndStack.HEADER_SIZE * 2;

	public CircularDiskDeque(long capacity, String fileName) throws IOException {
		super(capacity, fileName);
	}

	protected CircularDiskDeque() {
	}

	@Override
	protected void writeFirst(final byte[] bs) throws IOException { // header, data, header
		writeHeader(info.addFirst(HEADER_SIZE), bs.length); // one more header
		super.writeFirst(bs); // to left, header+data
	}

	@Override
	protected void writeLast(final byte[] bs) throws IOException { // header, data, header
		super.writeLast(bs); // header+data
		writeHeader(info.addLast(HEADER_SIZE), bs.length); // one more header
	}

	byte[] readFirstToRemove() throws IOException {
		byte[] result = super.readFirst();
		info.removeFirst(HEADER_SIZE); // rid one more header
		return result;
	}

	byte[] readLast() throws IOException {
		long length = readHeader(info.removeLast(HEADER_SIZE));
		long[] range = info.removeLast(length);
		return writer.readStorage(range);
	}

	byte[] readLastToRemove() throws IOException {
		byte[] result = readLast();
		info.removeLast(HEADER_SIZE); // rid one more header
		return result;
	}

	@Override
	public byte[] peekLast() throws IOException {
		if (info.size() == 0)
			return null;

		info.backupStatus();
		try {
			return readLast();
		} catch (Throwable e) {
			throw e;
		} finally {
			info.rollback(); // because read only
		}
	}

	@Override
	public byte[] removeLast() throws IOException {
		if (info.size() == 0)
			throw new NoSuchElementException();

		info.backupStatus();
		try {
			byte[] result = readLastToRemove();
			writeIndexEnd();
			decreaseLengthSize(result.length);
			return result;
		} catch (Throwable e) {
			info.rollback();
			throw e;
		}
	}

	@Override
	public long getAvailableSpace() {
		return info.getAvailableSpace() - headerSize;
	}

	@Override
	public long getHeaderSize() {
		return headerSize;
	}

	@Override
	protected CircularDiskQueueAndStack createDummy() {
		return new CircularDiskDeque();
	}

	/**
	 * it reads data from last to first.
	 * 
	 * @return an instance of {@link AutoCloseableIter}
	 */
	public AutoCloseableIter iterBackward() {
		return new CircularDiskIteratorBackward(createMirror());
	}

	class CircularDiskIteratorBackward extends CircularDiskIterator {

		CircularDiskDeque dequeMirror;

		CircularDiskIteratorBackward(CircularDiskQueueAndStack buffer) {
			super(buffer);
			this.dequeMirror = (CircularDiskDeque) buffer;
		}

		@Override
		protected byte[] readNext() throws IOException {
			return dequeMirror.readLastToRemove();
		}
	}

}
