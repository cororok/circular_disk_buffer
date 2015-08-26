package cororok.circular_buffer;

import static cororok.circular_buffer.CircularDiskQueueAndStackTest.assertSizeLengthEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cororok.circular_buffer.CircularDiskQueueAndStack;

public class CircularDiskQueueAndStackLoopTest {

	final String fileName = "dqtest.txt";

	final long headerSize = CircularDiskQueueAndStack.HEADER_SIZE;

	@Test
	public void testStack() throws Exception {
		testReadWrite(true);
	}

	@Test
	public void testQueue() throws Exception {
		testReadWrite(false);
	}

	public void testReadWrite(boolean isStack) throws Exception {
		final byte[][] inputs = makeInput(125);// byte = -128 ~ 127
		long totalLengthOfStorage = 0;
		long totalLength = 0;
		for (byte[] input : inputs) {
			totalLength += input.length;
		}
		totalLengthOfStorage = totalLength + inputs.length * headerSize;
		final long more = 99; // to be circled.
		try (CircularDiskQueueAndStack test = new CircularDiskQueueAndStack(totalLengthOfStorage + more,
				fileName)) {
			LinkedList<byte[]> dq = new LinkedList<>();
			for (int cnt = 0; cnt < 1000; cnt++) {
				assertSizeLengthEquals(0, 0, 0, test);

				// write
				write(inputs, isStack, test, dq);
				assertSizeLengthEquals(inputs.length, totalLength, totalLengthOfStorage, test);

				assertEquals(more, test.getAvailableTotalSpace());

				// read
				read(isStack, test, dq);
				assertSizeLengthEquals(0, 0, 0, test);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	void read(boolean isStack, CircularDiskQueueAndStack test, LinkedList<byte[]> dq) throws IOException {
		assertEquals(dq.size(), test.size());
		long length = test.length();
		while (dq.size() > 0) {
			final byte[] expected = dq.removeFirst();
			final byte[] result = test.removeFirst();
			assertArrayEquals(expected, result);
			length -= expected.length;
			long lengthOfStorage = length + (dq.size()) * headerSize;
			assertSizeLengthEquals(dq.size(), length, lengthOfStorage, test);
		}
	}

	void write(final byte[][] inputs, boolean isStack, CircularDiskQueueAndStack test, LinkedList<byte[]> dq)
			throws IOException {
		assertEquals(dq.size(), test.size());
		long length = test.length();
		for (byte[] input : inputs) {
			if (isStack) {
				test.addFirst(input);
				dq.addFirst(input);
			} else {
				test.addLast(input);
				dq.addLast(input);
			}
			length += input.length;
			long lengthOfStorage = length + (dq.size()) * headerSize;
			assertSizeLengthEquals(dq.size(), length, lengthOfStorage, test);
		}
	}

	@Test
	public void testMakeInput() {
		assertArrayEquals(new byte[][] {}, makeInput(0));
		assertArrayEquals(new byte[][] { { 0 } }, makeInput(1));
		assertArrayEquals(new byte[][] { { 0 }, { 1, 1 } }, makeInput(2));
		assertArrayEquals(new byte[][] { { 0 }, { 1, 1 }, { 2, 2, 2 } }, makeInput(3));
	}

	public static byte[][] makeInput(int inputSize) {
		byte[][] inputs = new byte[inputSize][];
		for (int i = 0; i < inputSize; i++) {
			byte[] input = new byte[i + 1];
			final byte data = (byte) i;
			for (int j = 0; j <= i; j++) {
				input[j] = data;
			}
			inputs[i] = input;
		}
		return inputs;
	}

	@Before
	@After
	public void clean() throws Exception {
		CircularDiskQueueAndStackTest.deleteFile(fileName);
	}
}
