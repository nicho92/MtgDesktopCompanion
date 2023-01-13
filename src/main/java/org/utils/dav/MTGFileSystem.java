package org.utils.dav;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.logging.MTGLogger;

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
		return List.of("Collections", "Alerts", "Stock", "News", "Shopping","Decks","Orders","Transactions","Contacts").stream().map(s->new MTGPath(this, s)).collect(Collectors.toList());
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		return null;
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		return new HashSet<>();
	}

	@Override
	public Path getPath(String first, String... more) {
		return new MTGPath(this,first,more);
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		return null;
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		return null;
	}

	@Override
	public WatchService newWatchService() throws IOException {
		return null;
	}


}
