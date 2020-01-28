package org.magic.api.interfaces;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public interface MTGGedStorage extends MTGPlugin{

	public FileSystem getFilesSystem() throws IOException;
	public Path getRoot();
}
