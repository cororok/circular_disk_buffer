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

	@Override
	void writeStorage(byte[] bs, boolean isFirst) throws IOException {
		if (isFirst) {
			writeLengthToHeader(info.addFirst(HEADER_SIZE), bs.length);
			writeToStorage(info.addFirst(bs.length), bs);
			writeLengthToHeader(info.addFirst(HEADER_SIZE), bs.length); // one more
			index.writeStartAndSize(info.getStart(), info.size());
		} else {
			writeLengthToHeader(info.addLast(HEADER_SIZE), bs.length);
			writeToStorage(info.addLast(bs.length), bs);
			writeLengthToHeader(info.addLast(HEADER_SIZE), bs.length); // one more
			index.writeEndAndSize(info.getEnd(), info.size());
		}
	}

	@Override
	byte[] readStorage(boolean isFirst, boolean readOnly) throws IOException {
		byte[] result;
		if (isFirst) {
			long length = readLengthFromHeader(info.removeFirst(HEADER_SIZE));
			long[] range = info.removeFirst(length);
			result = readBytesFromStorage(range);
			if (readOnly == false) {
				info.removeFirst(HEADER_SIZE); // one more header
				index.writeStartAndSize(info.getStart(), info.size());
			}
		} else {
			long length = readLengthFromHeader(info.removeLast(HEADER_SIZE));
			long[] range = info.removeLast(length);
			result = readBytesFromStorage(range);
			if (readOnly == false) {
				info.removeLast(HEADER_SIZE); // one more header
				index.writeEndAndSize(info.getEnd(), info.size());
			}
		}
		return result;
	}

	@Override
	public byte[] peekLast() throws IOException {
		return read(false, true);
	}

	@Override
	public byte[] removeLast() throws IOException {
		byte[] result = read(false, false);
		if (result == null)
			throw new NoSuchElementException();

		return result;
	}

	@Override
	public long getAvailableSpace() {
		return info.getAvailableSpace() - headerSize;
	}

	@Override
	public long getHeaderSize() {
		return headerSize;
	}
}
