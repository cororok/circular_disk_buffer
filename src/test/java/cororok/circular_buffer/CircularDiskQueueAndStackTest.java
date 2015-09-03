package cororok.circular_buffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
			test.writeHeader(range, input);
			result = test.readHeader(range);
			assertEquals(input, result);

			input = 1;
			test.writeHeader(range, input);
			result = test.readHeader(range);
			assertEquals(input, result);

			input = Integer.MAX_VALUE;
			test.writeHeader(range, input);
			result = test.readHeader(range);
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

	@Test
	public void testPeekWrite() throws Exception {
		final byte[] input0 = "12345".getBytes();
		final byte[] input1 = "abc".getBytes();

		final long length0 = input0.length;
		final long length1 = input1.length;
		final long lengthBoth = length0 + length1;

		try (CircularDiskQueueAndStack test = openCircularDiskQueueAndStack()) {
			final long space0 = length0 + test.getHeaderSize();
			final long space1 = length1 + test.getHeaderSize();
			final long spaceBoth = space0 + space1;
			assertSizeLengthEquals(0, 0, 0, test);

			// stack
			test.addFirst(input0);
			assertSizeLengthEquals(1, length0, space0, test);

			byte[] result0 = test.peekFirst();
			assertSizeLengthEquals(1, length0, space0, test);
			assertArrayEquals(input0, result0);

			// one more
			result0 = test.peekFirst();
			assertSizeLengthEquals(1, length0, space0, test);
			assertArrayEquals(input0, result0);

			// add another
			test.addFirst(input1);
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);

			byte[] result1 = test.peekFirst();
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);
			assertArrayEquals(input1, result1);

			// one more
			result1 = test.peekFirst();
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);
			assertArrayEquals(input1, result1);

			// remove
			result1 = test.removeFirst();
			assertSizeLengthEquals(1, length0, space0, test);
			assertArrayEquals(input1, result1);
		} catch (Exception e) {
			throw e;
		}
	}

	@Test
	public void testIterator() throws Exception {
		testIterator(openCircularDiskQueueAndStack());
	}

	public static void testIterator(CircularDiskQueueAndStack test) throws Exception {
		try {
			test.addFirst("aa".getBytes());
			test.addFirst("bb".getBytes());
			test.addFirst("cc".getBytes());

			assertSizeLengthEquals(3, 6, 6 + test.getHeaderSize() * 3, test);

			AutoCloseableIter itr = null;
			itr = test.iter();
			assertTrue(itr.hasNext());
			assertTrue(itr.hasNext());

			assertTrue(itr.hasNext());
			assertArrayEquals("cc".getBytes(), itr.next());

			assertTrue(itr.hasNext());
			assertArrayEquals("bb".getBytes(), itr.next());

			assertTrue(itr.hasNext());
			assertArrayEquals("aa".getBytes(), itr.next());

			assertFalse(itr.hasNext());

			boolean shouldFail = false;
			try {
				itr.next();
			} catch (Exception e) {
				shouldFail = true;
			}
			itr.close();
			assertTrue(shouldFail);
			assertSizeLengthEquals(3, 6, 6 + test.getHeaderSize() * 3, test);

			itr = test.iter();
			shouldFail = false;
			test.addLast("xx".getBytes());
			try {
				itr.next();
			} catch (ConcurrentModificationException e) {
				shouldFail = true;
			}
			itr.close();
			assertTrue(shouldFail);
		} catch (Exception e) {
			throw e;
		} finally {
			test.close();
		}
	}

	@Before
	@After
	public void clean() throws Exception {
		deleteFile(fileName);
	}

	public static void deleteFile(String file) {
		deleteFile(new File(file));
		deleteFile(new File(file + ".index"));
		deleteFile(new File(file + ".lock"));
	}

	public static void deleteFile(File file) {
		if (file.exists())
			file.delete();
	}

	public static void assertSizeLengthEquals(long size, long length, long lengthOfStorage,
			CircularDiskQueueAndStack sq) {
		assertEquals(size, sq.size());
		assertEquals(length, sq.length());
		assertEquals(lengthOfStorage, sq.lengthOfStorage());
	}
}
