package cororok.circular_buffer.storage;

import java.io.IOException;

public interface DiskWriter extends AutoCloseable {

	void seek(long pos) throws IOException;

	void write(byte[] b) throws IOException;

	void write(byte[] b, int off, int len) throws IOException;

	int read(byte[] b, int off, int len) throws IOException;

	int read(byte[] b) throws IOException;
}