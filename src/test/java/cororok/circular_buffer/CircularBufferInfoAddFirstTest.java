package cororok.circular_buffer;

import static cororok.circular_buffer.CircularBufferInfoTest.assertBufferStorage;
import static cororok.circular_buffer.CircularBufferInfoTest.createNormal35;
import static cororok.circular_buffer.CircularBufferInfoTest.createReverse53;
import static cororok.circular_buffer.CircularBufferInfoTest.createZero00;
import static cororok.circular_buffer.CircularBufferInfoTest.createZero10_0;
import static cororok.circular_buffer.CircularBufferInfoTest.createZero10_10;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import cororok.circular_buffer.CircularBufferInfo;

public class CircularBufferInfoAddFirstTest {

	@Test
	public void testNormalAdd() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 2, 3 }, cs.addFirst(1));
		assertBufferStorage(2, 5, 3, 2, cs);
	}

	@Test
	public void testNormalAddFull() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 0, 3 }, cs.addFirst(3));
		assertBufferStorage(0, 5, 5, 2, cs);
	}

	@Test
	public void testNormalAddOver() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 7, 10, 0, 3 }, cs.addFirst(6));
		assertBufferStorage(7, 5, 8, 2, cs);
	}

	@Test
	public void testNormalAddOverFull() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 5, 10, 0, 3 }, cs.addFirst(8));
		assertBufferStorage(5, 5, 10, 2, cs);
	}

	@Test
	public void testNormalAddNotEnough() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(null, cs.addFirst(9));
		assertBufferStorage(3, 5, 2, 1, cs);
	}

	// /
	@Test
	public void testReverseAdd() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 4, 5 }, cs.addFirst(1));
		assertBufferStorage(4, 3, 9, 2, cs);
	}

	@Test
	public void testReverseAddFull() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 3, 5 }, cs.addFirst(2));
		assertBufferStorage(3, 3, 10, 2, cs);
	}

	@Test
	public void testReverseAddNotEnough() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(null, cs.addFirst(3));
		assertBufferStorage(5, 3, 8, 1, cs);
	}

	// /
	@Test
	public void testZero00AddFull() {
		CircularBufferInfo cs = createZero00();
		assertArrayEquals(new long[] { 0, 10 }, cs.addFirst(10));
		assertBufferStorage(0, 0, 10, 1, cs);
	}

	@Test
	public void testZero10_10AddFull() {
		CircularBufferInfo cs = createZero10_10();
		assertArrayEquals(new long[] { 0, 10 }, cs.addFirst(10));
		assertBufferStorage(0, 10, 10, 1, cs);
	}

	@Test
	public void testZero10_0AddFull() {
		CircularBufferInfo cs = createZero10_0();
		assertArrayEquals(new long[] { 0, 10 }, cs.addFirst(10));
		assertBufferStorage(0, 0, 10, 1, cs);
	}

}
