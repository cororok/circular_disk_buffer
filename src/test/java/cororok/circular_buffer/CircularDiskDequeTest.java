package cororok.circular_buffer;

import static cororok.circular_buffer.CircularDiskQueueAndStackTest.assertSizeLengthEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cororok.circular_buffer.CircularDiskDeque;

public class CircularDiskDequeTest {

	final String fileName = "dqtest.txt";

	@Test
	public void testReadWriteOnce() throws Exception {
		final byte[] input0 = "12345".getBytes();
		final byte[] input1 = "abc".getBytes();

		final long length0 = input0.length;
		final long length1 = input1.length;
		final long lengthBoth = length0 + length1;
		try (CircularDiskDeque test = new CircularDiskDeque(100, fileName)) {
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
		}
	}

	@Before
	@After
	public void clean() throws Exception {
		CircularDiskQueueAndStackTest.deleteFile(fileName);
	}

}
