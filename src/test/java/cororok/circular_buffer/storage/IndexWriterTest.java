package cororok.circular_buffer.storage;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cororok.circular_buffer.CircularDiskQueueAndStackTest;
import cororok.circular_buffer.storage.DefaultDiskWriter;
import cororok.circular_buffer.storage.IndexWriter;

public class IndexWriterTest {

	final File indexFile = new File("test.index");

	@Test
	public void testReadWriteStartEn() throws Exception {
		try (IndexWriter iw = new IndexWriter(new DefaultDiskWriter(indexFile))) {
			assertArrayEquals(new long[] { 0, 0, 0 }, iw.readAll());
			assertArrayEquals(new long[] { 0, 0, 0 }, iw.readAll());

			long start;
			long size;
			final long end = 0;

			start = 1;
			size = 2;
			iw.writeStartAndSize(start, size);
			assertArrayEquals(new long[] { start, size, end }, iw.readAll());

			start = Long.MAX_VALUE;
			size = Long.MAX_VALUE - 1;
			iw.writeStartAndSize(start, size);
			assertArrayEquals(new long[] { start, size, end }, iw.readAll());
		} catch (Exception e) {
			throw e;
		}
	}

	@Test
	public void testReadWriteEndSize() throws Exception {
		try (IndexWriter iw = new IndexWriter(new DefaultDiskWriter(indexFile))) {
			assertArrayEquals(new long[] { 0, 0, 0 }, iw.readAll());
			assertArrayEquals(new long[] { 0, 0, 0 }, iw.readAll());

			final long start = 0;
			long size;
			long end;

			size = 1;
			end = 2;
			iw.writeEndAndSize(end, size);
			assertArrayEquals(new long[] { start, size, end }, iw.readAll());

			size = Long.MAX_VALUE;
			end = Long.MAX_VALUE - 1;
			iw.writeEndAndSize(end, size);
			assertArrayEquals(new long[] { start, size, end }, iw.readAll());
		} catch (Exception e) {
			throw e;
		}
	}

	@Test
	public void testOpenClose() throws Exception {
		IndexWriter iw = new IndexWriter(new DefaultDiskWriter(indexFile));
		try {
			assertArrayEquals(new long[] { 0, 0, 0 }, iw.readAll());
			assertArrayEquals(new long[] { 0, 0, 0 }, iw.readAll());

			long start;
			long size;
			long end = 0;

			start = 1;
			size = 2;
			iw.writeStartAndSize(start, size);
			assertArrayEquals(new long[] { start, size, end }, iw.readAll());

			end = 3;
			iw.writeEndAndSize(end, size);
			assertArrayEquals(new long[] { start, size, end }, iw.readAll());

			iw.close();
			// open again
			iw = new IndexWriter(new DefaultDiskWriter(indexFile));
			assertArrayEquals(new long[] { start, size, end }, iw.readAll());

			iw.close();
		} catch (Exception e) {
			throw e;
		}
	}

	@Before
	@After
	public void after() {
		CircularDiskQueueAndStackTest.deleteFile(indexFile);
	}
}
