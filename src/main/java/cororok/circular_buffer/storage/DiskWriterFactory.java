package cororok.circular_buffer.storage;

import java.io.File;
import java.io.IOException;

/**
 * @author songduk.park cororok@gmail.com
 */
public interface DiskWriterFactory {

	DiskWriter createStorageWriter(File dataFile) throws IOException;
}
