package cororok.circular_buffer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cororok.circular_buffer.CircularBufferInfo;

public class CircularBufferInfoTest {

	static final int CAPACITY = 10;

	static void assertBufferStorage(long start, long end, long length, long size, CircularBufferInfo cs) {
		assertEquals(start, cs.getStart());
		assertEquals(end, cs.getEnd());
		assertEquals(length, cs.length());
		assertEquals(size, cs.size());
	}

	static CircularBufferInfo createNormal35() {
		return new CircularBufferInfo(CAPACITY, 3, 5, 1);
	}

	static CircularBufferInfo createReverse53() {
		return new CircularBufferInfo(CAPACITY, 5, 3, 1);
	}

	static CircularBufferInfo createZero00() {
		return new CircularBufferInfo(CAPACITY, 0, 0, 0);
	}

	static CircularBufferInfo createZero0_10() {
		return new CircularBufferInfo(CAPACITY, 0, 10, 0);
	}

	static CircularBufferInfo createZero10_10() {
		return new CircularBufferInfo(CAPACITY, 10, 10, 0);
	}

	static CircularBufferInfo createZero10_0() {
		return new CircularBufferInfo(CAPACITY, 10, 0, 0);
	}

	static CircularBufferInfo createFull00() {
		return new CircularBufferInfo(CAPACITY, 0, 0, 1);
	}

	static CircularBufferInfo createFull0_10() {
		return new CircularBufferInfo(CAPACITY, 0, 10, 1);
	}

	static CircularBufferInfo createFull10_10() {
		return new CircularBufferInfo(CAPACITY, 10, 10, 1);
	}

	static CircularBufferInfo createFull10_0() {
		return new CircularBufferInfo(CAPACITY, 10, 0, 1);
	}

	static CircularBufferInfo createFull33() {
		return new CircularBufferInfo(CAPACITY, 3, 3, 2);
	}

	@Test
	public void testCreateNormal() {
		CircularBufferInfo cs = createNormal35();
		assertBufferStorage(3, 5, 2, 1, cs);
	}

	@Test
	public void testCreateReverse() {
		CircularBufferInfo cs = createReverse53();
		assertBufferStorage(5, 3, 8, 1, cs);
	}

	@Test
	public void testCreateZero00() {
		CircularBufferInfo cs = createZero00();
		assertBufferStorage(0, 0, 0, 0, cs);
	}

	@Test(expected = RuntimeException.class)
	public void testCreateZero0_10() {
		createZero0_10();
	}

	@Test
	public void testCreateZero10_10() {
		CircularBufferInfo cs = createZero10_10();
		assertBufferStorage(10, 10, 0, 0, cs);
	}

	@Test
	public void testCreateZero10_0() {
		CircularBufferInfo cs = createZero10_0();
		assertBufferStorage(10, 0, 0, 0, cs);
	}

	@Test
	public void testCreateFull00() {
		CircularBufferInfo cs = createFull00();
		assertBufferStorage(0, 0, 10, 1, cs);
	}

	@Test
	public void testCreateFull0_10() {
		CircularBufferInfo cs = createFull0_10();
		assertBufferStorage(0, 10, 10, 1, cs);
	}

	@Test
	public void testCreateFull10_10() {
		CircularBufferInfo cs = createFull10_10();
		assertBufferStorage(10, 10, 10, 1, cs);
	}

	@Test(expected = RuntimeException.class)
	public void testCreateFull10_0() {
		createFull10_0();
	}

	@Test
	public void testCreateFull33() {
		CircularBufferInfo cs = createFull33();
		assertBufferStorage(3, 3, 10, 2, cs);
	}

}
