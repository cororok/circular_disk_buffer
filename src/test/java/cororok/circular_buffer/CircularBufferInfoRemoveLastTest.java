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

import cororok.circular_buffer.CircularBufferInfo;

public class CircularBufferInfoRemoveLastTest {

	@Test
	public void testNormalRemove() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 4, 5 }, cs.removeLast(1));
		assertBufferStorage(3, 4, 1, 0, cs);
	}

	@Test
	public void testNormalRemoveFull() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(new long[] { 3, 5 }, cs.removeLast(2));
		assertBufferStorage(3, 3, 0, 0, cs);
	}

	@Test
	public void testNormalRemoveNotEnough() {
		CircularBufferInfo cs = createNormal35();
		assertArrayEquals(null, cs.removeLast(6));
		assertBufferStorage(3, 5, 2, 1, cs);
	}

	@Test
	public void testReverseRemove() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 2, 3 }, cs.removeLast(1));
		assertBufferStorage(5, 2, 7, 0, cs);
	}

	@Test
	public void testReverseRemoveFull() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 0, 3 }, cs.removeLast(3));
		assertBufferStorage(5, 0, 5, 0, cs);

	}

	@Test
	public void testReverseRemoveOver() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 8, 10, 0, 3 }, cs.removeLast(5));
		assertBufferStorage(5, 8, 3, 0, cs);
	}

	@Test
	public void testReverseRemoveOverFull() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(new long[] { 5, 10, 0, 3 }, cs.removeLast(8));
		assertBufferStorage(5, 5, 0, 0, cs);
	}

	@Test
	public void testReverseRemoveNotEnough() {
		CircularBufferInfo cs = createReverse53();
		assertArrayEquals(null, cs.removeLast(9));
		assertBufferStorage(5, 3, 8, 1, cs);
	}

	//
	// //////////
	@Test
	public void testFullRemove() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(new long[] { 2, 3 }, cs.removeLast(1));
		assertBufferStorage(3, 2, 9, 1, cs);
	}

	@Test
	public void testFullRemoveFull() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(new long[] { 0, 3 }, cs.removeLast(3));
		assertBufferStorage(3, 0, 7, 1, cs);
	}

	@Test
	public void testFullRemoveOver() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(new long[] { 5, 10, 0, 3 }, cs.removeLast(8));
		assertBufferStorage(3, 5, 2, 1, cs);
	}

	@Test
	public void testFullRemoveOverFull() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(new long[] { 3, 10, 0, 3 }, cs.removeLast(10));
		assertBufferStorage(3, 3, 0, 1, cs);
	}

	@Test
	public void testFullRemoveNotEnough() {
		CircularBufferInfo cs = createFull33();
		assertArrayEquals(null, cs.removeLast(11));
		assertBufferStorage(3, 3, 10, 2, cs);
	}

	// //////////
	@Test
	public void testFull0_10Remove() {
		CircularBufferInfo cs = createFull0_10();
		assertArrayEquals(new long[] { 0, 10 }, cs.removeLast(10));
		assertBufferStorage(0, 0, 0, 0, cs);
	}

	@Test
	public void testFul00Remove() {
		CircularBufferInfo cs = createFull00();
		assertArrayEquals(new long[] { 0, 10 }, cs.removeLast(10));
		assertBufferStorage(0, 0, 0, 0, cs);
	}

	@Test
	public void testFul10_10Remove() {
		CircularBufferInfo cs = createFull10_10();
		assertArrayEquals(new long[] { 0, 10 }, cs.removeLast(10));
		assertBufferStorage(10, 0, 0, 0, cs);
	}

}
