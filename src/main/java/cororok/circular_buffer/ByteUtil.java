package cororok.circular_buffer;

import java.nio.ByteBuffer;

public class ByteUtil {
	public static byte[] numToByte(int num) {
		return ByteBuffer.allocate(4).putInt(num).array();
	}

	public static int byteToNum(byte[] bs) {
		return ByteBuffer.wrap(bs).getInt();
	}
}
