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

	void initSizeAndLength() {
		this.size = this.info.size(); // cs counts data only
		this.length = this.info.length(); // the same
	}

	@Override
	void writeStorage(final byte[] bs, boolean isFirst) throws IOException {
		if (bs.length != fixedSize)
			throw new IOException("wrong input size " + bs.length + ", expected " + fixedSize);

		if (isFirst) {
			writeToStorage(info.addFirst(bs.length), bs);
			index.writeStartAndSize(info.getStart(), info.size());
		} else {
			writeToStorage(info.addLast(bs.length), bs);
			index.writeEndAndSize(info.getEnd(), info.size());
		}
	}

	@Override
	byte[] readStorage(boolean isFirst, boolean readOnly) throws IOException {
		byte[] result;
		if (isFirst) {
			long[] range = info.removeFirst(fixedSize);
			result = readBytesFromStorage(range);
			if (readOnly == false) {
				index.writeStartAndSize(info.getStart(), info.size());
			}
		} else {
			long[] range = info.removeLast(fixedSize);
			result = readBytesFromStorage(range);
			if (readOnly == false) {
				index.writeEndAndSize(info.getEnd(), info.size());
			}
		}
		return result;
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
