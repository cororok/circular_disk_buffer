package cororok.circular_buffer;

import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import cororok.circular_buffer.storage.DefaultDiskWriterFactory;
import cororok.circular_buffer.storage.DiskWriter;
import cororok.circular_buffer.storage.DiskWriterFactory;
import cororok.circular_buffer.storage.IndexWriter;

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
	protected static final int HEADER_SIZE = 4; // 1 bytes
	CircularBufferInfo info;

	DiskWriter writer;
	IndexWriter index;
	private File lock;
	int changed;

	/**
	 * current total length of all input data without additional headers.
	 */
	protected long length;

	/**
	 * # of elements it has
	 */
	protected long size;

	private DiskWriterFactory facotry;

	public CircularDiskQueueAndStack(long capacity, String fileName) throws IOException {
		this(capacity, fileName, new DefaultDiskWriterFactory());
	}

	public CircularDiskQueueAndStack(long capacity, String fileName, DiskWriterFactory facotry) throws IOException {
		this.facotry = facotry;
		if (openFile(fileName)) { // existing, needs to read size and length
			long[] startSizeEnd = this.index.readAll();
			this.info = new CircularBufferInfo(capacity, startSizeEnd[0], startSizeEnd[2], startSizeEnd[1]);
		} else {
			this.info = new CircularBufferInfo(capacity);
		}
		initSizeAndLength();
	}

	protected CircularDiskQueueAndStack() {
	}

	/**
	 * open or create the data file. It should lock file so that other processes can not access the data file at the
	 * same time.
	 * 
	 * @param fileName
	 *            data file name
	 * @return true if it data file already exists
	 * @throws IOException
	 *             if someone is using the file
	 */
	private synchronized boolean openFile(String fileName) throws IOException {
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

	/**
	 * solves mismatch between this and info
	 */
	void initSizeAndLength() {
		this.size = this.info.size() / 2; // info counts header and data
		this.length = this.info.length() - this.size * HEADER_SIZE;
	}

	@Override
	public void addFirst(final byte[] bs) throws IOException {
		canWrite(bs);
		try {
			writeFirst(bs);
			writeIndexStart();
			increaseLengthSize(bs.length);
		} catch (Throwable e) {
			info.rollback();
			throw e;
		}
	}

	@Override
	public void addLast(final byte[] bs) throws IOException {
		canWrite(bs);
		try {
			writeLast(bs);
			writeIndexEnd();
			increaseLengthSize(bs.length);
		} catch (Throwable e) {
			info.rollback();
			throw e;
		}
	}

	private void canWrite(final byte[] bs) throws IOException {
		if (bs == null || bs.length == 0)
			throw new RuntimeException("empty input data");

		if (info.canAdd(bs.length) == false)
			throw new IOException("no more sapce");

		info.backupStatus();
	}

	protected void writeFirst(final byte[] bs) throws IOException {
		// data first, header later
		writer.writeStorage(info.addFirst(bs.length), bs);
		writeHeader(info.addFirst(HEADER_SIZE), bs.length);
	}

	protected void writeLast(final byte[] bs) throws IOException {
		// header first, data later
		writeHeader(info.addLast(HEADER_SIZE), bs.length);
		writer.writeStorage(info.addLast(bs.length), bs);
	}

	@Override
	public byte[] peekFirst() throws IOException {
		if (info.size() == 0)
			return null;

		info.backupStatus();
		try {
			return readFirst();
		} catch (Throwable e) {
			throw e;
		} finally {
			info.rollback(); // because read only
		}
	}

	@Override
	public byte[] removeFirst() throws IOException {
		if (info.size() == 0)
			throw new NoSuchElementException();

		info.backupStatus();
		try {
			byte[] result = readFirstToRemove();
			writeIndexStart();
			decreaseLengthSize(result.length);
			return result;
		} catch (Throwable e) {
			info.rollback();
			throw e;
		}
	}

	byte[] readFirst() throws IOException {
		long length = readHeader(info.removeFirst(HEADER_SIZE));
		long[] range = info.removeFirst(length);
		return writer.readStorage(range);
	}

	byte[] readFirstToRemove() throws IOException {
		return readFirst();
	}

	void writeIndexStart() throws IOException {
		index.writeStartAndSize(info.getStart(), info.size());
	}

	void writeIndexEnd() throws IOException {
		index.writeEndAndSize(info.getEnd(), info.size());
	}

	int readHeader(long[] range) throws IOException {
		byte[] result = writer.readStorage(range);
		return ByteUtil.byteToNum(result);
	}

	void writeHeader(long[] range, int length) throws IOException {
		byte[] data = ByteUtil.numToByte(length);
		writer.writeStorage(range, data);
	}

	void increaseLengthSize(long space) {
		length += space;
		++size;
		++changed;
	}

	void decreaseLengthSize(long space) {
		length -= space;
		--size;
		++changed;
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

	public boolean canAddWithHeader(long add) {
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

	@Override
	public long getAvailableTotalSpace() {
		return info.getAvailableSpace();
	}

	@Override
	public long getAvailableSpace() {
		return info.getAvailableSpace() - HEADER_SIZE;
	}

	public int getHeaderSize() {
		return HEADER_SIZE;
	}

	/**
	 * {@link #createMirror()} calls this to get an empty instance.
	 * 
	 * @return an empty instance which has the proper readXXX methods
	 */
	protected CircularDiskQueueAndStack createDummy() {
		return new CircularDiskQueueAndStack();
	}

	/**
	 * After it gets an empty instance by {{@link #createDummy()} it adds cloned {@link CircularBufferInfo} and a new
	 * {@link DiskWriter}. It is called by {@link CircularDiskIterator}
	 * 
	 * @return
	 */
	protected final CircularDiskQueueAndStack createMirror() {
		CircularDiskQueueAndStack bufferMirror = createDummy();
		if (this.getClass() != bufferMirror.getClass())
			throw new RuntimeException("createDummy method is not implemented in " + this.getClass());

		bufferMirror.info = (CircularBufferInfo) this.info.clone();
		try {
			bufferMirror.writer = this.facotry.createStorageWriter(this.writer.geteFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bufferMirror;
	}

	/**
	 * It is not an iterator, see {@link AutoCloseableIter}. it reads data from first to last.
	 * 
	 * @return something like iterator.
	 */
	public AutoCloseableIter iter() {
		return new CircularDiskIterator(createMirror());
	}

	public class CircularDiskIterator implements AutoCloseableIter {
		protected CircularDiskQueueAndStack bufferMirror;
		protected CircularBufferInfo infoMirror;
		private final int changedOri;

		CircularDiskIterator(CircularDiskQueueAndStack bufferMirror) {
			this.bufferMirror = bufferMirror;
			this.infoMirror = bufferMirror.info;
			this.changedOri = changed;
		}

		@Override
		public boolean hasNext() {
			return infoMirror.size() > 0;
		}

		@Override
		public void close() throws Exception {
			bufferMirror.writer.close();
		}

		@Override
		public byte[] next() {
			if (changed != changedOri) {
				throw new ConcurrentModificationException("changed from " + changedOri + " to " + changed);
			}
			try {
				return readNext();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * reads data from first but {@link Queue} can have the other way.
		 * 
		 * @return
		 * @throws IOException
		 */
		protected byte[] readNext() throws IOException {
			return bufferMirror.readFirstToRemove();
		}
	}
}
