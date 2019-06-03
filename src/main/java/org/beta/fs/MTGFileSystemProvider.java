package org.beta.fs;

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
import java.nio.file.spi.FileSystemProvider;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDao;

public class MTGFileSystemProvider extends FileSystemProvider {

	private MTGDao dao;
	private FileSystem fs;
	
	
	public MTGFileSystemProvider(MTGFileSystem mtgFileSystem, MTGDao mtgDao) throws SQLException {
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
		throw new UnsupportedOperationException();
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {
		
		
		return new DirectoryStream<Path>() {
		
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
					}
				}
				else if(parts.size()==2)
				{
					try {
						MagicCollection c = new MagicCollection(parts.get(1));
						dao.listEditionsIDFromCollection(c).forEach(ed->paths.add(new MTGPath(fs, parts.get(0),c.getName(),ed)));
					} catch (SQLException e) 
					{
					}
				}
				else if(parts.size()==3)
				{
					try {
						MagicCollection c = new MagicCollection(parts.get(1));
						String idEdition = parts.get(2);
						dao.listCardsFromCollection(c,new MagicEdition(idEdition)).forEach(card->paths.add(new MTGPath(fs, parts.get(0),c.getName(),idEdition,card.getName())));
					} catch (SQLException e) 
					{
					}
				}
				
				
				
				return paths.iterator();
			}
		};
		
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
		try {
			dao.saveCollection(((MTGPath)dir).getStringFileName());
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public void delete(Path path) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
