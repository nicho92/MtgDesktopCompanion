package org.magic.api.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGLogger;

public class MTGFileSystem extends FileSystem {

	private FileSystemProvider provider;
	protected Logger log = MTGLogger.getLogger(this.getClass());
	private JsonExport serializer;

	public MTGFileSystem(MTGDao mtgDao) {
		serializer = new JsonExport();
		this.provider = new MTGFileSystemProvider(this,mtgDao);
	}
	
	public JsonExport getSerializer() {
		return serializer;
	}
	
	@Override
	public FileSystemProvider provider() {
		return provider;
	}

	@Override
	public void close() throws IOException {
		// doNothing
		
	}
	

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public String getSeparator() {
		return "/";
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		
		FileSystem fs = this;
		return new Iterable<>() {
			@Override
			public Iterator<Path> iterator() {
				List<Path> roots = new ArrayList<>();
				List.of("Collections", "Alerts", "Stock", "News", "Shopping","Decks").forEach(s->roots.add(new MTGPath(fs, s)));
				return roots.iterator();
			}
		};
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		return null;
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path getPath(String first, String... more) {
		return new MTGPath(this,first,more);
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WatchService newWatchService() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

		
}
