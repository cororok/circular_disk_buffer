package cororok.circular_buffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cororok.circular_buffer.CircularDiskQueueAndStack;

public class CircularDiskQueueAndStackTest {
	final String fileName = "dqtest.txt";
	final long headerSize = CircularDiskQueueAndStack.HEADER_SIZE;

	@Test
	public void testOpenClose() throws Exception {
		CircularDiskQueueAndStack test = null;
		try {
			test = openCircularDiskQueueAndStack();
			assertSizeLengthEquals(0, 0, 0, test);

			// close reopen
			test.close();
			test = openCircularDiskQueueAndStack();
			assertSizeLengthEquals(0, 0, 0, test);

			byte[] in = "12345".getBytes();
			test.addFirst(in); // at first
			assertSizeLengthEquals(1, in.length, in.length + headerSize, test);

			// close reopen
			test.close();
			test = openCircularDiskQueueAndStack();
			assertSizeLengthEquals(1, in.length, in.length + headerSize, test);

			test.removeFirst();
			assertSizeLengthEquals(0, 0, 0, test);
			test.addLast(in); // at last
			assertSizeLengthEquals(1, in.length, in.length + headerSize, test);

			// close reopen
			test.close();
			test = openCircularDiskQueueAndStack();
			assertSizeLengthEquals(1, in.length, in.length + headerSize, test);
		} catch (Exception e) {
			throw e;
		} finally {
			test.close();
		}
	}

	private CircularDiskQueueAndStack openCircularDiskQueueAndStack() throws IOException {
		return new CircularDiskQueueAndStack(100, fileName);
	}

	@Test
	public void testNumAndByte() {
		for (int i = 0; i < 1000_000; i++) {
			byte[] bytes = CircularDiskQueueAndStack.numToByte(i);
			assertEquals(4, bytes.length);

			int result = CircularDiskQueueAndStack.byteToNum(bytes);
			assertEquals(i, result);
		}
	}

	@Test
	public void testReadWriteHeader() throws Exception {
		testReadWriteHeader(new long[] { 0, 4 });
		testReadWriteHeader(new long[] { 6, 10 });

		testReadWriteHeader(new long[] { 9, 10, 0, 3 });
		testReadWriteHeader(new long[] { 7, 10, 0, 1 });
		testReadWriteHeader(new long[] { 8, 10, 0, 2 });
	}

	private void testReadWriteHeader(long[] range) throws Exception {
		int input = 0;
		int result = 0;
		try (CircularDiskQueueAndStack test = openCircularDiskQueueAndStack()) {
			test.writeLengthToHeader(range, input);
			result = test.readLengthFromHeader(range);
			assertEquals(input, result);

			input = 1;
			test.writeLengthToHeader(range, input);
			result = test.readLengthFromHeader(range);
			assertEquals(input, result);

			input = Integer.MAX_VALUE;
			test.writeLengthToHeader(range, input);
			result = test.readLengthFromHeader(range);
			assertEquals(input, result);
		} catch (Exception e) {
			throw e;
		}
	}

	@Test
	public void testReadWrite() throws Exception {
		final byte[] input = "12345".getBytes();
		final long length = input.length;
		try (CircularDiskQueueAndStack test = openCircularDiskQueueAndStack()) {
			final long space = length + test.getHeaderSize();
			assertSizeLengthEquals(0, 0, 0, test);

			// stack
			test.addFirst(input);
			assertSizeLengthEquals(1, length, space, test);

			byte[] result = test.removeFirst();
			assertSizeLengthEquals(0, 0, 0, test);
			assertArrayEquals(input, result);

			// queue
			test.addLast(input);
			assertSizeLengthEquals(1, length, space, test);

			byte[] result1 = test.removeFirst();
			assertSizeLengthEquals(0, 0, 0, test);
			assertArrayEquals(input, result1);
		} catch (Exception e) {
			throw e;
		}
	}

	@Before
	@After
	public void clean() throws Exception {
		deleteFile(fileName);
	}

	public static void deleteFile(String file) {
		deleteFile(new File(file));
		deleteFile(new File(file+ ".index"));
		deleteFile(new File(file+ ".lock"));
	}

	public static void deleteFile(File file) {
		if (file.exists())
			file.delete();
	}

	public static void assertSizeLengthEquals(long size, long length, long lengthOfStorage,
			CircularDiskQueueAndStack sq) {
		assertEquals(size, sq.size);
		assertEquals(length, sq.length);
		assertEquals(lengthOfStorage, sq.lengthOfStorage());
	}
}
