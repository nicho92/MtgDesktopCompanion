package org.magic.api.interfaces;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.magic.api.beans.technical.GedEntry;

public interface MTGGedStorage extends MTGPlugin{

	public FileSystem getFilesSystem() throws IOException;
	public Path getRoot() throws IOException;
	public Stream<Path> listDirectory(Path p) throws IOException;

	public <T extends MTGSerializable> List<GedEntry<T>> listAll() throws IOException;
	public <T extends MTGSerializable> void store(GedEntry<T> entry) throws IOException;
	public <T extends MTGSerializable> boolean delete(GedEntry<T> entry);
	public <T extends MTGSerializable> Path getPath(Class<T> classe, T instance) throws IOException;
	public <T extends MTGSerializable> GedEntry<T> read(Path p) throws IOException;

}
