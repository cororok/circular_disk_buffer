package cororok.circular_buffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import cororok.circular_buffer.storage.DefaultDiskWriterFactory;
import cororok.circular_buffer.storage.IndexWriter;
import cororok.circular_buffer.storage.DiskWriter;
import cororok.circular_buffer.storage.DiskWriterFactory;

/**
 * It supports stack(FIFO, addFirst & removeFirst) and queue(LIFO, addLast & removeFirst) but does not support deque
 * which needs additional lastRemove. It uses a fixed storage so once it reaches the full it can not add any more data
 * but because it is a circular buffer it can recycle the space either left or right direction later when a new space is
 * available.
 * 
 * Data format is [length][binary data] and repeats them. The [length] is 4 bytes header that is the length of next
 * binary data bytes and then [binary data] is any unfixed binary data. For example if it adds "a".getBytes() then the
 * storage needs at the first 4 bytes to save the length 1 and follows 1 byte representing "a" so the total space
 * requires 5 bytes.
 * 
 * Because it saves any unfixed data and only supports removeFirst the length of the given any data is located at the
 * left side of data. If it supports removeLast then it will needs the same header at the right side of data too, see
 * {@link CircularDiskDeque}.
 * 
 * @author songduk.park cororok@gmail.com
 */
public class CircularDiskQueueAndStack implements Queue, Stack {
	protected static final long HEADER_SIZE = 4; // 1 bytes
	CircularBufferInfo info;

	DiskWriter writer;
	IndexWriter index;
	private File lock;

	/**
	 * current total length of all input data without additional headers.
	 */
	protected long length;

	/**
	 * # of elements it has
	 */
	protected long size;

	public CircularDiskQueueAndStack(long capacity, String fileName) throws IOException {
		this(capacity, fileName, new DefaultDiskWriterFactory());
	}

	public CircularDiskQueueAndStack(long capacity, String fileName, DiskWriterFactory facotry) throws IOException {
		if (openFile(fileName, facotry)) { // existing, needs to read size and length
			long[] startSizeEnd = this.index.readAll();
			this.info = new CircularBufferInfo(capacity, startSizeEnd[0], startSizeEnd[2], startSizeEnd[1]);
		} else {
			this.info = new CircularBufferInfo(capacity);
		}
		initSizeAndLength();
	}

	/**
	 * open or create the data file. It should lock file so that other processes can not access the data file at the
	 * same time.
	 * 
	 * @param fileName
	 *            data file name
	 * @param facotry
	 * @return true if it data file already exists
	 * @throws IOException
	 *             if someone is using the file
	 */
	private synchronized boolean openFile(String fileName, DiskWriterFactory facotry) throws IOException {
		lock = new File(fileName + ".lock");
		if (lock.exists())
			throw new IOException("exit because lock file exist at " + lock.getAbsolutePath());

		lock.createNewFile();

		File dataFile = new File(fileName);
		boolean fileExists = dataFile.exists();

		this.writer = facotry.createStorageWriter(dataFile);
		File indexfile = new File(fileName + ".index");
		this.index = new IndexWriter(facotry.createStorageWriter(indexfile));

		return fileExists;
	}

	void initSizeAndLength() {
		this.size = this.info.size() / 2; // info counts header and data
		this.length = this.info.length() - this.size * HEADER_SIZE;
	}

	@Override
	public boolean addFirst(final byte[] bs) throws IOException {
		return write(bs, true);
	}

	@Override
	public boolean addLast(final byte[] bs) throws IOException {
		return write(bs, false);
	}

	@Override
	public byte[] peekFirst() throws IOException {
		return read(true, true);
	}

	@Override
	public byte[] removeFirst() throws IOException {
		byte[] result = read(true, false);
		if (result == null)
			throw new NoSuchElementException();

		return result;
	}

	boolean write(final byte[] bs, boolean toFirst) throws IOException {
		if (info.canAdd(bs.length) == false)
			throw new IOException("no more sapce");

		info.backupStatus();
		try {
			writeStorage(bs, toFirst);
			increaseLengthSize(bs.length);
		} catch (Throwable e) {
			info.rollback();
			throw e;
		}
		return true;
	}

	void writeStorage(final byte[] bs, boolean toFirst) throws IOException {
		if (toFirst) {// careful, add 2nd head, 1st data
			writeToStorage(info.addFirst(bs.length), bs);
			writeLengthToHeader(info.addFirst(HEADER_SIZE), bs.length);
			index.writeStartAndSize(info.getStart(), info.size());
		} else {
			writeLengthToHeader(info.addLast(HEADER_SIZE), bs.length);
			writeToStorage(info.addLast(bs.length), bs);
			index.writeEndAndSize(info.getEnd(), info.size());
		}
	}

	byte[] read(boolean fromFirst, boolean readOnly) throws IOException {
		if (info.size() == 0)
			return null;

		info.backupStatus();
		byte[] result = null;
		boolean needRollback = false;
		try {
			result = readStorage(fromFirst, readOnly);
		} catch (Throwable e) {
			e.printStackTrace();
			needRollback = true;
			throw new IOException(e);
		} finally {
			if (readOnly || needRollback)
				info.rollback();
			else
				decreaseLengthSize(result.length);
		}
		return result;
	}

	byte[] readStorage(boolean fromFirst, boolean readOnly) throws IOException {
		byte[] result;
		if (fromFirst) {
			long length = readLengthFromHeader(info.removeFirst(HEADER_SIZE));
			long[] range = info.removeFirst(length);
			result = readBytesFromStorage(range);
			if (readOnly == false)
				index.writeStartAndSize(info.getStart(), info.size());
		} else {
			throw new IOException("unsupported Read at the last");
		}
		return result;
	}

	byte[] readBytesFromStorage(long[] range) throws IOException {
		if (range == null)
			throw new IOException("null range");

		byte[] result;
		writer.seek(range[0]);

		int size1 = (int) (range[1] - range[0]);
		if (range.length == 2) {
			result = new byte[size1];
			writer.read(result);
		} else {// two pieces
			int size2 = (int) (range[3] - range[2]);
			result = new byte[size1 + size2];
			writer.read(result, 0, size1); // part 1

			writer.seek(range[2]); // part 2
			writer.read(result, size1, size2);
		}
		return result;
	}

	void writeToStorage(final long[] range, byte[] bs) throws IOException {
		if (range == null)
			throw new IOException("null range");

		writer.seek(range[0]);
		int size1 = (int) (range[1] - range[0]);
		if (range.length == 2) {
			writer.write(bs);
		} else {// two pieces
			int size2 = (int) (range[3] - range[2]);
			writer.write(bs, 0, size1); // part 1

			writer.seek(range[2]); // part 2
			writer.write(bs, size1, size2);
		}
	}

	int readLengthFromHeader(long[] range) throws IOException {
		byte[] result = readBytesFromStorage(range);
		return byteToNum(result);
	}

	void writeLengthToHeader(long[] range, int length) throws IOException {
		byte[] data = numToByte(length);
		writeToStorage(range, data);
	}

	void increaseLengthSize(long space) {
		length += space;
		++size;
	}

	void decreaseLengthSize(long space) {
		length -= space;
		--size;
	}

	public String status() {
		return info.toString();
	}

	public void close() throws Exception {
		writer.close();
		index.close();

		lock.delete();
	}

	@Override
	public long size() {
		return this.size;
	}

	public boolean canAdd(long add) {
		return info.canAdd(add);
	}

	@Override
	public long length() {
		return this.length;
	}

	public long lengthOfStorage() {
		return info.length();
	}

	public long getStart() {
		return info.getStart();
	}

	public long getEnd() {
		return info.getEnd();
	}

	public static byte[] numToByte(int num) {
		return ByteBuffer.allocate(4).putInt(num).array();
	}

	public static int byteToNum(byte[] bs) {
		return ByteBuffer.wrap(bs).getInt();
	}

	@Override
	public long getAvailableTotalSpace() {
		return info.getAvailableSpace();
	}

	@Override
	public long getAvailableSpace() {
		return info.getAvailableSpace() - HEADER_SIZE;
	}

	public long getHeaderSize() {
		return HEADER_SIZE;
	}

}
