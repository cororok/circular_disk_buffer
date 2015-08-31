package cororok.circular_buffer.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * It is a wrapper of RandomAccessFile.
 * 
 * @author songduk.park cororok@gmail.com
 */
public class DefaultDiskWriter extends DiskWriter {

	RandomAccessFile file;
	File dataFile;

	public DefaultDiskWriter(File dataFile) throws FileNotFoundException {
		this.dataFile = dataFile;
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

	@Override
	public File geteFile() {
		return dataFile;
	}
}
