package org.magic.api.interfaces;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.magic.api.beans.GedEntry;

public interface MTGGedStorage extends MTGPlugin{

	public FileSystem getFilesSystem() throws IOException;
	public Path getRoot() throws IOException;
	public void store(GedEntry<?> entry) throws IOException;
	public Stream<Path> listDirectory(Path p) throws IOException;

	public boolean delete(GedEntry<?> entry);
	public <T> Path getPath(Class<T> classe, T instance) throws IOException;
	public GedEntry<?> read(Path p) throws IOException;
	
	public List<Path> list(String dir);
	
}
