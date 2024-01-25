package org.utils.dav;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.logging.MTGLogger;

public class MTGFileSystemProvider extends FileSystemProvider {

	private MTGDao dao;
	private FileSystem fs;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public MTGFileSystemProvider(MTGFileSystem mtgFileSystem, MTGDao mtgDao) {
		this.dao=mtgDao;
		this.fs = mtgFileSystem;
	}


	@Override
	public String getScheme() {
		return "mtg";
	}

	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
		return fs;
	}

	@Override
	public FileSystem getFileSystem(URI uri) {
		return fs;

	}

	@Override
	public Path getPath(URI uri) {
		return new MTGPath(fs,uri.getPath());
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
		return new MTGByteChannel((MTGPath)path,dao,options,attrs);
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {

		if(((MTGPath)dir).isCard())
			throw new IOException(dir + " is not a folder path");

		return new DirectoryStream<>() {

			private final AtomicBoolean closed = new AtomicBoolean();
			private final AtomicBoolean iteratorReturned = new AtomicBoolean();

			@Override
			public void close() throws IOException {
				closed.set(true);

			}

			@Override
			public Iterator<Path> iterator() {
				if (closed.get()) {
		            throw new IllegalStateException("Already closed");
		        }
				if (!iteratorReturned.compareAndSet(false, true)) {
			        throw new IllegalStateException("Iterator already returned");
			    }
				List<Path> paths = new ArrayList<>();

				List<String> parts = ((MTGPath)dir).getParts();


				if(parts.size()==1)
				{
					try {
						dao.listCollections().forEach(c->paths.add(new MTGPath(fs, c.getName())));
					} catch (SQLException e) {
						logger.error(e);
					}
				}
				else if(parts.size()==2)
				{
					try {
						var c = new MTGCollection(parts.get(1));
						dao.listEditionsIDFromCollection(c).forEach(ed->paths.add(new MTGPath(fs, parts.get(0),c.getName(),ed)));
					} catch (SQLException e)
					{
						logger.error(e);
					}
				}
				else if(parts.size()==3)
				{
					try {
						var c = new MTGCollection(parts.get(1));
						var idEdition = parts.get(2);
						dao.listCardsFromCollection(c,new MTGEdition(idEdition)).forEach(card->paths.add(new MTGPath(fs, parts.get(0),c.getName(),idEdition,card.getName())));
					} catch (SQLException e)
					{
						logger.error(e);
					}
				}



				return paths.iterator();
			}
		};

	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {

		logger.debug("createDirectory {} {}",dir,attrs);

		MTGPath direct = (MTGPath)dir;

		try {
			if(dir.startsWith("Collections") && direct.isCollection())
				dao.saveCollection(direct.getCollection());

		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	@Override
	public void delete(Path path) throws IOException {
		MTGPath from = (MTGPath)path;

		logger.debug("delete {}",from);


	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException {

		MTGPath from = (MTGPath)source;
		MTGPath to = (MTGPath)target;

		logger.debug("copy from {} to {}",from,to);
	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException {
		MTGPath from = (MTGPath)source;
		MTGPath to = (MTGPath)target;


		logger.debug("move from {} to {}",from,to);


	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {
		return path.equals(path2);
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		return false;
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {
		return new FileStore() {

			@Override
			public String type() {
				return dao.getType().toString();
			}

			@Override
			public boolean supportsFileAttributeView(String arg0) {

				return false;
			}

			@Override
			public boolean supportsFileAttributeView(Class<? extends FileAttributeView> arg0) {
				return false;
			}

			@Override
			public String name() {
				return dao.getName();
			}

			@Override
			public boolean isReadOnly() {
				return false;
			}

			@Override
			public long getUsableSpace() throws IOException {
				return 0;
			}

			@Override
			public long getUnallocatedSpace() throws IOException {
				return 0;
			}

			@Override
			public long getTotalSpace() throws IOException {
				return dao.getDBSize().values().stream().mapToLong(Long::longValue).sum();
			}

			@Override
			public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> arg0) {
				return null;
			}

			@Override
			public Object getAttribute(String arg0) throws IOException {
				return null;
			}
		};
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {

		MTGPath p = (MTGPath)path;

		if(p.isCollection())
		{
			try {
				if(dao.getCollection(p.getStringFileName())==null)
					throw new FileNotFoundException(path.toString());
			} catch (SQLException e) {
				throw new IOException(e.getMessage());
			}

		}

		if(p.isEdition())
		{
			try {
				List<String> eds = dao.listEditionsIDFromCollection(p.getCollection());
				if(!eds.contains(p.getStringFileName()))
					throw new FileNotFoundException(path + " not exist");

			} catch (SQLException e) {
				throw new IOException(e.getMessage());
			}

		}

		if(p.isCard())
		{
			try {

				List<MTGCard> cards = dao.listCardsFromCollection(p.getCollection(), new MTGEdition(p.getIDEdition()));

				Optional<MTGCard> mc = cards.stream().filter(c->c.getName().equalsIgnoreCase(p.getStringFileName())).findFirst();

				if(!mc.isPresent())
					throw new FileNotFoundException(path + " not exist");

			} catch (SQLException e) {
				throw new IOException(e.getMessage());
			}

		}




	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
		throw new NotImplementedException("Not implemented");
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options)throws IOException {
		return (A)((MTGPath)path).readAttributes();
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
		logger.debug("reading {}",attributes);
		return new HashMap<>();
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
		logger.debug("reading {} atts:{} {}",path,attribute,value );
	}

}
