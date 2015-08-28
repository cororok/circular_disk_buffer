package cororok.circular_buffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CircularDiskDequeFixedTest {

	final String fileName = "dqtest.txt";

	@Test
	public void testReadWriteOnce() throws Exception {
		final byte[] input0 = "12345".getBytes();
		final byte[] input1 = "abcde".getBytes();
		CircularDiskDequeTest.testReadWriteOnce(new CircularDiskDequeFixed(100, fileName, input0.length), input0,
				input1);
	}

	@Test
	public void testPeekWrite() throws Exception {
		final byte[] input0 = "12345".getBytes();
		final byte[] input1 = "abcde".getBytes();

		CircularDiskDequeTest.testPeekWrite(new CircularDiskDequeFixed(100, fileName, input0.length), input0, input1);
	}

	@Before
	@After
	public void clean() throws Exception {
		CircularDiskQueueAndStackTest.deleteFile(fileName);
	}

}
