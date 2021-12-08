package org.magic.api.fs.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import org.magic.api.beans.GedEntry;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractFileStorage;
import org.magic.tools.MTG;

public class DAOFileSystemStorage extends AbstractFileStorage {

	@Override
	public String getName() {
		return "DAO";
	}

	@Override
	public void initFileSystem() throws IOException {
		
		if(!MTG.getEnabledPlugin(MTGDao.class).isEnable())
			try {
				MTG.getEnabledPlugin(MTGDao.class).init();
			} catch (SQLException e) {
				throw new IOException(e);
			}
	}

	@Override
	public Path getRoot() throws IOException {
		return Path.of("");
	}

	@Override
	public GedEntry<?> read(Path p) throws IOException {
		logger.debug("reading " + p.toAbsolutePath());
		return null;
	}

	@Override
	public void store(GedEntry<?> entry) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).storeEntry(entry);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Stream<Path> listDirectory(Path p) throws IOException {
		logger.debug("listDirectory" + p.getParent().getFileName() + " " + p.getFileName());
		
		return Stream.of(Path.of("liliana.jpg"), Path.of("test.jpg"));
		
	}

	@Override
	public List<Path> list(String dir) {
		logger.debug("list dir" + dir);
		return null;
	}

	@Override
	public boolean delete(GedEntry<?> entry) {
		logger.debug("delete " + entry.getClasse() + " "+ entry.getId());
		try {
			return MTG.getEnabledPlugin(MTGDao.class).deleteEntry(entry);
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}

	@Override
	public <T> Path getPath(Class<T> classe, T instance) throws IOException {
		logger.debug("getPath" + classe + " " + instance);
		try {
			MTG.getEnabledPlugin(MTGDao.class).listEntries(TRUE, FALSE);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
		
		return Path.of(classe.getName(), instance.toString());
	}

}
