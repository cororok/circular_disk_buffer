package cororok.circular_buffer.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DefaultDiskWriter implements DiskWriter {

	RandomAccessFile file;

	public DefaultDiskWriter(File dataFile) throws FileNotFoundException {
		this.file = new RandomAccessFile(dataFile, "rw");
	}

	@Override
	public void seek(long pos) throws IOException {
		file.seek(pos);
	}

	@Override
	public void write(byte[] b) throws IOException {
		file.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		file.write(b, off, len);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return file.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return file.read(b);
	}

	@Override
	public void close() throws IOException {
		file.close();
	}
}
