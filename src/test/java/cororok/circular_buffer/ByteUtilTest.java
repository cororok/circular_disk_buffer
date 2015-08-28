package cororok.circular_buffer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ByteUtilTest {

	@Test
	public void testNumAndByte() {
		for (int i = 0; i < 1000_000; i++) {
			byte[] bytes = ByteUtil.numToByte(i);
			assertEquals(4, bytes.length);

			int result = ByteUtil.byteToNum(bytes);
			assertEquals(i, result);
		}
	}
}
