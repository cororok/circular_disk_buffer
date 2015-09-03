package cororok.circular_buffer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A hybrid stack which uses Memory and Disk together. Because the remove operation of stack returns the newest one not
 * the oldest one those new ones don't even have to go to the disk if the remove is requested very soon.
 * 
 * All the data in memory is moved to the disk when it is closed and if there is not enough space in the disk to save
 * all the current data in the memory it will not add data any more even if the memory has some free space.
 * 
 * @author songduk.park cororok@gmail.com
 *
 */
public class BufferedCircularDiskStack implements Stack {

	LinkedList<byte[]> memory = new LinkedList<>();
	CircularDiskQueueAndStack stack;
	final long maxMemory;
	final int headerSize;

	/**
	 * current total length of all input data without additional headers in memory.
	 */
	protected long length;

	/**
	 * current total length of all input data including additional headers in memory. Memory doesn't have to keep the
	 * headers but when the data is moved to the disk the disk needs additional header size.
	 */
	protected long lengthOfStorage;

	/**
	 * @param stack
	 * @param maxMemory
	 *            maximum total length, not size it can hold data in the memory.
	 */
	public BufferedCircularDiskStack(CircularDiskQueueAndStack stack, long maxMemory) {
		this.stack = stack;
		this.headerSize = stack.getHeaderSize();
		this.maxMemory = maxMemory;
	}

	@Override
	public void addFirst(byte[] bs) throws IOException {
		long requiredLenght = bs.length + headerSize;
		long lengthOfStorageNew = lengthOfStorage + requiredLenght;

		if (stack.canAddWithHeader(lengthOfStorageNew) == false)
			throw new RuntimeException("Not enough space in the disk to add it");

		final long minimumRequiredLength = maxMemory - bs.length;
		moveToDisk(minimumRequiredLength);

		memory.addFirst(bs);
		this.length += bs.length;
		this.lengthOfStorage += requiredLenght;
	}

	private void moveToDisk(final long minimumRequiredLength) throws IOException {
		while (minimumRequiredLength < length) {
			byte[] data = memory.removeLast(); // the oldest one, not first
			stack.addFirst(data);

			length -= data.length;
			lengthOfStorage = lengthOfStorage - data.length - headerSize;
		}
	}

	@Override
	public byte[] removeFirst() throws IOException {
		if (memory.size() > 0) {
			byte[] data = memory.removeFirst();

			this.length -= data.length;
			this.lengthOfStorage = lengthOfStorage - data.length - headerSize;

			return data;
		}

		if (stack.size > 0)
			return stack.removeFirst();

		throw new NoSuchElementException();
	}

	@Override
	public byte[] peekFirst() throws IOException {
		if (memory.size() > 0)
			return memory.peekFirst();

		if (stack.size > 0)
			return stack.peekFirst();

		return null;
	}

	@Override
	public long size() {
		return stack.size + memory.size();
	}

	@Override
	public long length() {
		return stack.length() + length;
	}

	@Override
	public long lengthOfStorage() {
		return stack.lengthOfStorage() + lengthOfStorage;
	}

	/*
	 * Even if memory has some free space it only adds data as much as the disk can
	 */
	@Override
	public long getAvailableTotalSpace() {
		return stack.getAvailableTotalSpace();
	}

	@Override
	public long getAvailableSpace() {
		return stack.getAvailableSpace();
	}

	/*
	 * save all data to the disk first then closes the disk
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		while (memory.size() > 0) {
			byte[] data = memory.removeLast(); // the oldest one, not first
			stack.addFirst(data);
		}
		stack.close();
	}

	public int getHeaderSize() {
		return headerSize;
	}

}
