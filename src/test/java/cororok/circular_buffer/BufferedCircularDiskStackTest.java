package cororok.circular_buffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BufferedCircularDiskStackTest {

	final String fileName = "dqtest.txt";

	final byte[] input = "123456".getBytes();
	final byte[] input1 = "654321".getBytes();
	final byte[] input2 = "abcdef".getBytes();
	final byte[] input3 = "fedcba".getBytes();
	final byte[] input4 = "ABCDEF".getBytes();

	@Test
	public void testReadWrite() throws Exception {
		try (BufferedCircularDiskStack test = openBufferedCircularDiskStack()) {

			assertSizeLengthEquals(0, 0, 0, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(0, 0, 0, test.stack);

			test.addFirst(input);
			assertSizeLengthEquals(1, 6, 10, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(0, 0, 0, test.stack);

			test.addFirst(input1);
			assertSizeLengthEquals(2, 12, 20, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(0, 0, 0, test.stack);

			test.addFirst(input2);
			assertSizeLengthEquals(3, 18, 30, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(0, 0, 0, test.stack);

			test.addFirst(input3);
			assertSizeLengthEquals(4, 24, 40, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(1, 6, 10, test.stack);
			assertArrayEquals(input, test.stack.peekFirst()); // the oldest one

			test.addFirst(input4);
			assertSizeLengthEquals(5, 30, 50, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(2, 12, 20, test.stack);
			assertArrayEquals(input1, test.stack.peekFirst()); // 2nd oldest one

			byte[] result4 = test.removeFirst();
			assertSizeLengthEquals(4, 24, 40, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(2, 12, 20, test.stack); // still be the same
			assertArrayEquals(input4, result4);

			byte[] result3 = test.removeFirst();
			assertSizeLengthEquals(3, 18, 30, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(2, 12, 20, test.stack); // still be the same
			assertArrayEquals(input3, result3);

			byte[] result2 = test.removeFirst();
			assertSizeLengthEquals(2, 12, 20, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(2, 12, 20, test.stack); // still be the same
			assertArrayEquals(input2, result2);

			byte[] result1 = test.removeFirst();
			assertArrayEquals(input1, result1);
			assertSizeLengthEquals(1, 6, 10, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(1, 6, 10, test.stack); // one removed

			byte[] result = test.removeFirst();
			assertArrayEquals(input, result);
			assertSizeLengthEquals(0, 0, 0, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(0, 0, 0, test.stack); // no more
		} catch (Exception e) {
			throw e;
		}
	}

	@Test
	public void testClose() throws Exception {
		BufferedCircularDiskStack test = null;
		try {
			test = openBufferedCircularDiskStack();
			assertSizeLengthEquals(0, 0, 0, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(0, 0, 0, test.stack);

			test.addFirst(input);
			test.addFirst(input1);
			test.addFirst(input2);

			// now memory has 3 elements but disk has 0.
			assertSizeLengthEquals(3, 18, 30, test);
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(0, 0, 0, test.stack);

			// close and re-open
			test.close();
			test = openBufferedCircularDiskStack();

			// should have 3
			assertSizeLengthEquals(3, 18, 30, test);
			// all above should be in disk
			CircularDiskQueueAndStackTest.assertSizeLengthEquals(3, 18, 30, test.stack);
		} finally {
			test.close();
		}
	}

	private BufferedCircularDiskStack openBufferedCircularDiskStack() throws IOException {
		CircularDiskQueueAndStack stack = new CircularDiskQueueAndStack(50, fileName);
		return new BufferedCircularDiskStack(stack, 20);
	}

	public static void assertSizeLengthEquals(long size, long length, long lengthOfStorage,
			BufferedCircularDiskStack sq) {
		assertEquals(size, sq.size());
		assertEquals(length, sq.length());
		assertEquals(lengthOfStorage, sq.lengthOfStorage());
	}

	@Before
	@After
	public void clean() throws Exception {
		CircularDiskQueueAndStackTest.deleteFile(fileName);
	}

}
