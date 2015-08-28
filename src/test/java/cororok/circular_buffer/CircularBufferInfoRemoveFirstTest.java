package cororok.circular_buffer;

import static cororok.circular_buffer.CircularBufferInfoTest.assertBufferStorage;
import static cororok.circular_buffer.CircularBufferInfoTest.createFull00;
import static cororok.circular_buffer.CircularBufferInfoTest.createFull0_10;
import static cororok.circular_buffer.CircularBufferInfoTest.createFull10_10;
import static cororok.circular_buffer.CircularBufferInfoTest.createFull33;
import static cororok.circular_buffer.CircularBufferInfoTest.createNormal35;
import static cororok.circular_buffer.CircularBufferInfoTest.createReverse53;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CircularBufferInfoRemoveFirstTest {

	@Test
	public void testNormalRemove() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 3, 4 }, cs.removeFirst(1));
		assertBufferStorage(4, 5, 1, 0, cs);
	}

	@Test
	public void testNormalRemoveFull() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 3, 5 }, cs.removeFirst(2));
		assertBufferStorage(5, 5, 0, 0, cs);
	}

	@Test
	public void testNormalRemoveNotEnough() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(null, cs.removeFirst(6));
		assertBufferStorage(3, 5, 2, 1, cs);
	}

	@Test
	public void testReverseRemove() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 5, 6 }, cs.removeFirst(1));
		assertBufferStorage(6, 3, 7, 0, cs);
	}

	@Test
	public void testReverseRemoveFull() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 5, 10 }, cs.removeFirst(5));
		assertBufferStorage(10, 3, 3, 0, cs);
	}

	@Test
	public void testReverseRemoveOver() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 5, 10, 0, 2 }, cs.removeFirst(7));
		assertBufferStorage(2, 3, 1, 0, cs);
	}

	@Test
	public void testReverseRemoveOverFull() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 5, 10, 0, 3 }, cs.removeFirst(8));
		assertBufferStorage(3, 3, 0, 0, cs);
	}

	@Test
	public void testReverseRemoveNotEnough() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(null, cs.removeFirst(9));
		assertBufferStorage(5, 3, 8, 1, cs);
	}

	//
	// //////////
	@Test
	public void testFullRemove() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(new long[] { 3, 4 }, cs.removeFirst(1));
		assertBufferStorage(4, 3, 9, 1, cs);
	}

	@Test
	public void testFullRemoveFull() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(new long[] { 3, 10 }, cs.removeFirst(7));
		assertBufferStorage(10, 3, 3, 1, cs);
	}

	@Test
	public void testFullRemoveOver() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(new long[] { 3, 10, 0, 1 }, cs.removeFirst(8));
		assertBufferStorage(1, 3, 2, 1, cs);
	}

	@Test
	public void testFullRemoveOverFull() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(new long[] { 3, 10, 0, 3 }, cs.removeFirst(10));
		assertBufferStorage(3, 3, 0, 1, cs);
	}

	@Test
	public void testFullRemoveNotEnough() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(null, cs.removeFirst(11));
		assertBufferStorage(3, 3, 10, 2, cs);
	}

	// //////////
	@Test
	public void testFull0_10Remove() {
		CircularBufferInfo cs = createFull0_10();
		assertArrayEquals(new long[] { 0, 10 }, cs.removeFirst(10));
		assertBufferStorage(10, 10, 0, 0, cs);
	}

	@Test
	public void testFul00Remove() {
		CircularBufferInfo cs = createFull00();
		assertArrayEquals(new long[] { 0, 10 }, cs.removeFirst(10));
		assertBufferStorage(10, 0, 0, 0, cs);
	}

	@Test
	public void testFul10_10Remove() {
		CircularBufferInfo cs = createFull10_10();
		assertArrayEquals(new long[] { 0, 10 }, cs.removeFirst(10));
		assertBufferStorage(10, 10, 0, 0, cs);
	}
}
