package cororok.circular_buffer.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cororok.circular_buffer.CircularDiskQueueAndStackTest;

public class DefaultDiskWriterTest {
	final String fileName = "data.txt";
	final int capacity = 9;
	final byte[] full = new byte[] { -1, 1, 2, 3, 4, 5, 6, 7, 8 };
	final byte[] zero = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	DiskWriter writer;

	@Test
	public void testWrite() throws IOException {
		writer.write(full);

		byte[] r0 = new byte[full.length];
		writer.seek(0);
		writer.read(r0);
		assertArrayEquals(full, r0);

		writer.seek(0);
		writer.write(zero);

		byte[] r1 = new byte[full.length];
		writer.seek(0);
		writer.read(r1);
		assertArrayEquals(zero, r1);
	}

	@Test
	public void testReadWrite2Size() throws IOException {
		resetFull();
		writer.writeStorage(new long[] { 2, 5 }, new byte[] { 11, 12, 13 });
		byte[] r = readAll();
		assertArrayEquals(new byte[] { -1, 1, 11, 12, 13, 5, 6, 7, 8 }, r);
		assertArrayEquals(new byte[] { 11, 12, 13 }, writer.readStorage(new long[] { 2, 5 }));

		resetFull();
		writer.writeStorage(new long[] { 0, 1 }, new byte[] { 21 });
		byte[] r1 = readAll();
		assertArrayEquals(new byte[] { 21, 1, 2, 3, 4, 5, 6, 7, 8 }, r1);
		assertArrayEquals(new byte[] { 21 }, writer.readStorage(new long[] { 0, 1 }));


		resetFull();
		writer.writeStorage(new long[] { 0, 3 }, new byte[] { 21, 22, 23 });
		byte[] r2 = readAll();
		assertArrayEquals(new byte[] { 21, 22, 23, 3, 4, 5, 6, 7, 8 }, r2);
		assertArrayEquals(new byte[] {  21, 22, 23  }, writer.readStorage(new long[] { 0, 3 }));


		resetFull();
		writer.writeStorage(new long[] { 8, 9 }, new byte[] { 30 });
		byte[] r3 = readAll();
		assertArrayEquals(new byte[] { -1, 1, 2, 3, 4, 5, 6, 7, 30 }, r3);
		assertArrayEquals(new byte[] {30}, writer.readStorage(new long[] { 8, 9 }));

		resetFull();
		writer.writeStorage(new long[] { 7, 9 }, new byte[] { 31, 32 });
		byte[] r4 = readAll();
		assertArrayEquals(new byte[] { -1, 1, 2, 3, 4, 5, 6, 31, 32 }, r4);
		assertArrayEquals(new byte[] { 31, 32 }, writer.readStorage(new long[] { 7, 9 }));
	}

	@Test
	public void testReadWrite4Size() throws IOException {
		resetFull();
		writer.writeStorage(new long[] { 2, 5, 5, 7 }, new byte[] { 11, 12, 13, 21, 22 });
		byte[] r = readAll();
		assertArrayEquals(new byte[] { -1, 1, 11, 12, 13, 21, 22, 7, 8 }, r);
		assertArrayEquals(new byte[] { 11, 12, 13, 21, 22 }, writer.readStorage(new long[] { 2, 5, 5, 7 }));
		
		resetFull();
		writer.writeStorage(new long[] { 0, 1, 1, 2 }, new byte[] { 21, 22 });
		byte[] r1 = readAll();
		assertArrayEquals(new byte[] { 21, 22, 2, 3, 4, 5, 6, 7, 8 }, r1);
		assertArrayEquals(new byte[] { 21, 22 }, writer.readStorage(new long[] { 0, 1, 1, 2 }));
		
		resetFull();
		writer.writeStorage(new long[] { 0, 3, 3, 5 }, new byte[] { 21, 22, 23, 30, 31 });
		byte[] r2 = readAll();
		assertArrayEquals(new byte[] { 21, 22, 23, 30, 31, 5, 6, 7, 8 }, r2);
		assertArrayEquals(new byte[] { 21, 22, 23, 30, 31 }, writer.readStorage(new long[] { 0, 3, 3, 5 }));
		
		resetFull();
		writer.writeStorage(new long[] { 8, 9, 0, 2 }, new byte[] { 30, 40, 41 });
		byte[] r3 = readAll();
		assertArrayEquals(new byte[] { 40, 41, 2, 3, 4, 5, 6, 7, 30 }, r3);
		assertArrayEquals(new byte[] { 30, 40, 41 }, writer.readStorage(new long[] { 8, 9, 0, 2 }));
		
		resetFull();
		writer.writeStorage(new long[] { 7, 9, 0, 1 }, new byte[] { 31, 32, 41 });
		byte[] r4 = readAll();
		assertArrayEquals(new byte[] { 41, 1, 2, 3, 4, 5, 6, 31, 32 }, r4);
		assertArrayEquals(new byte[] { 31, 32, 41 }, writer.readStorage(new long[] { 7, 9, 0, 1 }));
	}

	private void resetFull() throws IOException {
		writer.seek(0);
		writer.write(full);
	}

	private byte[] readAll() throws IOException {
		byte[] r = new byte[full.length];
		writer.seek(0);
		writer.read(r);
		return r;
	}

	@Before
	public void setup() throws FileNotFoundException {
		clean();
		writer = new DefaultDiskWriter(new File(fileName));
	}

	@After
	public void clean() {
		CircularDiskQueueAndStackTest.deleteFile(fileName);
	}

}
