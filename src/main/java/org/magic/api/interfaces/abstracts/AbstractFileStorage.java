package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.interfaces.MTGGedStorage;
import org.magic.api.interfaces.MTGSerializable;

public abstract class AbstractFileStorage extends AbstractMTGPlugin implements MTGGedStorage {

	protected FileSystem fs;


	public abstract void initFileSystem() throws IOException;

	@Override
	public <T extends MTGSerializable> List<GedEntry<T>> listAll() throws IOException {

		var ret = new ArrayList<GedEntry<T>>();

		try(var stream = Files.walk(getRoot()).filter(Files::isRegularFile))
		{
			stream.forEach(p->{
				try {
					ret.add(read(p));
				} catch (IOException e) {
					logger.error(e);
				}
			});
		return ret;
		}
	}


	@Override
	public Path getRoot() throws IOException {
		if(fs==null)
			initFileSystem();


		return fs.getPath("/");
	}



	@Override
	public FileSystem getFilesSystem() throws IOException {

		if(fs==null)
			initFileSystem();

		return fs;
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.GED;
	}


	@Override
	public <T extends MTGSerializable> GedEntry<T>  read(Path p) throws IOException
	{
		GedEntry<T> ged = SerializationUtils.deserialize(java.nio.file.Files.readAllBytes(p));
		logger.debug("reading {} : {} {}",p,ged.getClasse(),ged.getName());

		notify(ged);
		return ged;
	}

	@Override
	public <T extends MTGSerializable> void store(GedEntry<T> entry) throws IOException
	{

		var p = getPath(entry);
		logger.info("store : {} ",p.toAbsolutePath());

		if(p.getParent()!=null && !Files.exists(p.getParent()))
			Files.createDirectories(p.getParent());

		Files.write(p, SerializationUtils.serialize(entry),StandardOpenOption.CREATE);

	}

	@Override
	public Stream<Path> listDirectory(Path p) throws IOException {
		return Files.list(p);
	}

	private <T extends MTGSerializable> Path getPath(GedEntry<T> entry) throws IOException
	{
		if(entry.getClasse()==null)
			return getFilesSystem().getPath(entry.getName());
		else if(entry.getId()!=null)
			return getFilesSystem().getPath(entry.getClasse().getSimpleName(),entry.getId(),entry.getName());
		else
			return getFilesSystem().getPath(entry.getClasse().getSimpleName(),entry.getName());
	}

	@Override
	public <T extends MTGSerializable>  boolean delete(GedEntry<T> entry) {
		logger.info("removing {}",entry);

		try {
			Files.delete(getPath(entry));
			return true;
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
	}

	@Override
	public <T extends MTGSerializable> Path getPath(Class<T> classe, T instance) throws IOException {


		if(classe==null)
			return getRoot();

		if(instance==null)
			return getFilesSystem().getPath(classe.getSimpleName());

		return getFilesSystem().getPath(classe.getSimpleName(),instance.toString());
	}



}
