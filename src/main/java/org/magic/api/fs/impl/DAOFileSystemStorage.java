package org.magic.api.fs.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.Icon;

import org.magic.api.beans.technical.GedEntry;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractFileStorage;
import org.magic.api.interfaces.extra.MTGSerializable;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;

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
	public <T extends MTGSerializable> List<GedEntry<T>> listAll() throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listAllEntries();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}


	@Override
	public <T extends MTGSerializable> GedEntry<T> read(Path p) throws IOException {


		var cIdClasse = p.getParent().getParent().getFileName().toString();
		var cIdInstance = p.getParent().getFileName().toString();
		var cFName = p.getFileName().toString();

		logger.debug("reading {}/{}/{}",cIdClasse,cIdInstance,cFName);

		try {
			return MTG.getEnabledPlugin(MTGDao.class).readEntry(cIdClasse, cIdInstance, cFName);
		} catch (SQLException e) {
			logger.error(e);
		}


		return null;
	}

	@Override
	public <T extends MTGSerializable>  void store(GedEntry<T> entry) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).storeEntry(entry);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Stream<Path> listDirectory(Path p) throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listEntries(p.getParent().getFileName().toString(),p.getFileName().toString()).stream().map(ge->Path.of(p.getParent().getFileName().toString(),p.getFileName().toString(),ge.getName()));
		} catch (Exception e) {
			logger.error(e);
			throw new IOException(e);
		}
	}

	@Override
	public <T extends MTGSerializable> boolean delete(GedEntry<T> entry) {
		logger.debug("delete {}/{}",entry.getClasse(),entry.getId());
		try {
			return MTG.getEnabledPlugin(MTGDao.class).deleteEntry(entry);
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}

	@Override
	public <T extends MTGSerializable> Path getPath(Class<T> classe, T instance) throws IOException {
		return Path.of(classe.getCanonicalName(), instance.getStoreId());
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_DATABASE;
	}

}
