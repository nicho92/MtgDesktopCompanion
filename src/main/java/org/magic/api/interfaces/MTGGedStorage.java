package org.magic.api.interfaces;

import java.io.IOException;
import java.nio.file.FileSystem;

public interface MTGGedStorage extends MTGPlugin{

	public FileSystem getFilesSystem() throws IOException;
	public void initFileSystem() throws IOException;
}
