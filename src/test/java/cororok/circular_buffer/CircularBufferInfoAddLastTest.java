package cororok.circular_buffer;

import static cororok.circular_buffer.CircularBufferInfoTest.assertBufferStorage;
import static cororok.circular_buffer.CircularBufferInfoTest.createNormal35;
import static cororok.circular_buffer.CircularBufferInfoTest.createReverse53;
import static cororok.circular_buffer.CircularBufferInfoTest.createZero00;
import static cororok.circular_buffer.CircularBufferInfoTest.createZero10_0;
import static cororok.circular_buffer.CircularBufferInfoTest.createZero10_10;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CircularBufferInfoAddLastTest {

	@Test
	public void testNormalAdd() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 5, 8 }, cs.addLast(3));
		assertBufferStorage(3, 8, 5, 2, cs);
	}

	@Test
	public void testNormalAddFull() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 5, 10 }, cs.addLast(5));
		assertBufferStorage(3, 10, 7, 2, cs);
	}

	@Test
	public void testNormalAddOver() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 5, 10, 0, 1 }, cs.addLast(6));
		assertBufferStorage(3, 1, 8, 2, cs);
	}

	@Test
	public void testNormalAddOverFull() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 5, 10, 0, 3 }, cs.addLast(8));
		assertBufferStorage(3, 3, 10, 2, cs);
	}

	@Test
	public void testNormalAddNotEnough() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(null, cs.addLast(9));
		assertBufferStorage(3, 5, 2, 1, cs);
	}

	// /
	@Test
	public void testReverseAdd() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 3, 4 }, cs.addLast(1));
		assertBufferStorage(5, 4, 9, 2, cs);
	}

	@Test
	public void testReverseAddFull() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 3, 5 }, cs.addLast(2));
		assertBufferStorage(5, 5, 10, 2, cs);

	}

	@Test
	public void testReverseAddNotEnough() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(null, cs.addLast(3));
		assertBufferStorage(5, 3, 8, 1, cs);
	}

	// /
	@Test
	public void testZero00AddFull() {
		CircularBufferInfo cs = createZero00();
		assertArrayEquals(new long[] { 0, 10 }, cs.addLast(10));
		assertBufferStorage(0, 10, 10, 1, cs);
	}

	@Test
	public void testZero10_10AddFull() {
		CircularBufferInfo cs = createZero10_10();
		assertArrayEquals(new long[] { 0, 10 }, cs.addLast(10));
		assertBufferStorage(10, 10, 10, 1, cs);
	}

	@Test
	public void testZero10_0AddFull() {
		CircularBufferInfo cs = createZero10_0();
		assertArrayEquals(new long[] { 0, 10 }, cs.addLast(10));
		assertBufferStorage(10, 10, 10, 1, cs);
	}

}
