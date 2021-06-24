package org.magic.services;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.GedEntry;
import org.magic.api.interfaces.MTGGedStorage;
public class GedService {

	private static GedService inst;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private MTGGedStorage storage ;
	
	private GedService() {
		
			storage = getEnabledPlugin(MTGGedStorage.class);
			logger.info("Loading FS :" + getFileSystem() +" (o="+getFileSystem().isOpen()+", ro="+getFileSystem().isReadOnly()+"), Provider="+getFileSystem().provider());
	}
	
	public static GedService inst()
	{
		if(inst ==null)
			inst = new GedService();
		
		return inst;
	}
	
	public FileSystem getFileSystem() {
		try {
			return storage.getFilesSystem();
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}
	
	public GedEntry<?> read(Path p) throws IOException
	{
		GedEntry<?> ged = SerializationUtils.deserialize(java.nio.file.Files.readAllBytes(p));
		logger.debug("reading " + p + " :" + ged.getClasse() + " " + ged.getFullName());
		return ged;
	}
	
	
	public void store(GedEntry<?> entry) throws IOException
	{
		var p = getPath(entry);
		logger.info("store :"+ p.toAbsolutePath());
		
		if(p.getParent()!=null && !Files.exists(p.getParent()))
			Files.createDirectories(p.getParent());
			
		Files.write(p, SerializationUtils.serialize(entry),StandardOpenOption.CREATE);
		
	}
	
	public Stream<Path> listDirectory(Path p) throws IOException {
		return Files.list(p);
	}
	
	
	
	public List<Path> list(String dir)
	{
		try (Stream<Path> s = Files.list(getFileSystem().getPath(dir)))
		{
			return s.collect(Collectors.toList());
		} catch (IOException e) {
			return new ArrayList<>();
		}
		
	}
	
	public Path root()
	{
		return storage.getRoot();
	}
	
	private Path getPath(GedEntry<?> entry)
	{
		if(entry.getClasse()==null)
			return getFileSystem().getPath(entry.getName());
		else if(entry.getObject()!=null)
			return getFileSystem().getPath(entry.getClasse().getSimpleName(),entry.getObject().toString(),entry.getName());
		else
			return getFileSystem().getPath(entry.getClasse().getSimpleName(),entry.getName());
	}
	
	public boolean delete(GedEntry<?> entry) {
		logger.info("removing " + entry);
		
		try {
			Files.delete(getPath(entry));
			return true;
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
	}

	public <T> Path getPath(Class<T> classe, T instance) {
		
		
		if(classe==null)
			return root();
		
		if(instance==null)
			return getFileSystem().getPath(classe.getSimpleName());
		
		return getFileSystem().getPath(classe.getSimpleName(),instance.toString());
	}

	
}
