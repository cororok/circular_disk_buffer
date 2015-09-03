package cororok.circular_buffer;

import static cororok.circular_buffer.CircularDiskQueueAndStackTest.assertSizeLengthEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ConcurrentModificationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CircularDiskDequeTest {

	final String fileName = "dqtest.txt";

	@Test
	public void testReadWriteOnce() throws Exception {
		final byte[] input0 = "12345".getBytes();
		final byte[] input1 = "abc".getBytes();

		testReadWriteOnce(new CircularDiskDeque(100, fileName), input0, input1);
	}

	@Test
	public void testIterator() throws Exception {
		CircularDiskQueueAndStackTest.testIterator(new CircularDiskDeque(100, fileName));
	}

	@Test
	public void testIteratorBackward() throws Exception {
		testIteratorBackward(new CircularDiskDeque(100, fileName));
	}

	public static void testIteratorBackward(CircularDiskDeque test) throws Exception {
		try {
			test.addFirst("aa".getBytes());
			test.addFirst("bb".getBytes());
			test.addFirst("cc".getBytes());

			assertSizeLengthEquals(3, 6, 6 + test.getHeaderSize() * 3, test);

			AutoCloseableIter itr = null;
			itr = test.iterBackward();
			assertTrue(itr.hasNext());
			assertTrue(itr.hasNext());

			assertTrue(itr.hasNext());
			assertArrayEquals("aa".getBytes(), itr.next());

			assertTrue(itr.hasNext());
			assertArrayEquals("bb".getBytes(), itr.next());

			assertTrue(itr.hasNext());
			assertArrayEquals("cc".getBytes(), itr.next());

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

			itr = test.iterBackward();
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

	public static void testReadWriteOnce(CircularDiskDeque test, final byte[] input0, final byte[] input1)
			throws Exception {
		final long length0 = input0.length;
		final long length1 = input1.length;
		final long lengthBoth = length0 + length1;
		try {
			assertSizeLengthEquals(0, 0, 0, test);

			final long space0 = length0 + test.getHeaderSize();
			final long space1 = length1 + test.getHeaderSize();
			final long spaceBoth = space0 + space1;

			test.addFirst(input0); // input0
			assertSizeLengthEquals(1, length0, space0, test);

			test.addFirst(input1); // input1 input
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);

			byte[] result = test.removeLast(); // removed input0
			assertSizeLengthEquals(1, length1, space1, test); // now input1 left
			assertArrayEquals(input0, result);

			test.addLast(input0); // now input1 input0
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);

			byte[] result1 = test.removeFirst(); // removed input1
			assertSizeLengthEquals(1, length0, space0, test); // now input0 left
			assertArrayEquals(input1, result1);

			byte[] result2 = test.removeLast(); // removed input0
			assertSizeLengthEquals(0, 0, 0, test);
			assertArrayEquals(input0, result2);
		} catch (Exception e) {
			throw e;
		} finally {
			test.close();
		}
	}

	@Test
	public void testPeekWrite() throws Exception {
		final byte[] input0 = "12345".getBytes();
		final byte[] input1 = "abc".getBytes();
		testPeekWrite(new CircularDiskDeque(100, fileName), input0, input1);
	}

	public static void testPeekWrite(CircularDiskDeque test, final byte[] input0, final byte[] input1)
			throws Exception {
		final long length0 = input0.length;
		final long length1 = input1.length;
		final long lengthBoth = length0 + length1;

		try {
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

			result0 = test.peekLast();
			assertSizeLengthEquals(1, length0, space0, test);
			assertArrayEquals(input0, result0);

			// one more
			result0 = test.peekFirst();
			assertSizeLengthEquals(1, length0, space0, test);
			assertArrayEquals(input0, result0);

			result0 = test.peekLast();
			assertSizeLengthEquals(1, length0, space0, test);
			assertArrayEquals(input0, result0);

			// add another
			test.addFirst(input1);
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);

			byte[] result1 = test.peekFirst();
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);
			assertArrayEquals(input1, result1);

			result0 = test.peekLast();
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);
			assertArrayEquals(input0, result0);

			// one more
			result1 = test.peekFirst();
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);
			assertArrayEquals(input1, result1);

			result0 = test.peekLast();
			assertSizeLengthEquals(2, lengthBoth, spaceBoth, test);
			assertArrayEquals(input0, result0);

			// remove
			result1 = test.removeFirst();
			assertSizeLengthEquals(1, length0, space0, test);
			assertArrayEquals(input1, result1);
		} catch (Exception e) {
			throw e;
		} finally {
			test.close();
		}
	}

	@Before
	@After
	public void clean() throws Exception {
		CircularDiskQueueAndStackTest.deleteFile(fileName);
	}

}
