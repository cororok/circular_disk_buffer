package cororok.circular_buffer.storage;

import java.io.File;
import java.io.IOException;

public interface DiskWriterFactory {

	DiskWriter createStorageWriter(File dataFile) throws IOException;
}
