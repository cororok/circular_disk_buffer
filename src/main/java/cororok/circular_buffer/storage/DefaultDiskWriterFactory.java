package cororok.circular_buffer.storage;

import java.io.File;
import java.io.FileNotFoundException;

public class DefaultDiskWriterFactory implements DiskWriterFactory {

	@Override
	public DiskWriter createStorageWriter(File dataFile) throws FileNotFoundException {
		return new DefaultDiskWriter(dataFile);
	}

}
