package org.magic.services;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.magic.api.beans.GedEntry;
import org.magic.api.interfaces.MTGGedStorage;

public class GedService {

	private static GedService inst;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private FileSystem fileSystem ;
	
	private GedService() {
		
		try {
			fileSystem = MTGControler.getInstance().getEnabled(MTGGedStorage.class).getFilesSystem();
			logger.info("Loading FS :" + fileSystem +" (o="+fileSystem.isOpen()+", ro="+fileSystem.isReadOnly()+"), Provider="+fileSystem.provider());
		} catch (IOException e) {
			logger.error("Error init FS ",e);
		}
		
		
	}
	
	public static GedService inst()
	{
		if(inst ==null)
			inst = new GedService();
		
		return inst;
	}
	
	public FileSystem getFileSystem() {
		return fileSystem;
	}
	
	public <T> void store(GedEntry<T> entry) throws IOException
	{
		Path p = fileSystem.getPath(entry.getFullName());
		
		p = Files.write(p, entry.getContent(),StandardOpenOption.CREATE);
		logger.info("store :"+ p.toAbsolutePath());
		
		fileSystem.getRootDirectories().forEach(r->{
			try {
				Files.list(r).forEach(p2->{
					System.out.println(p2);
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
	}
	
	public List<Path> list(Path dir)
	{
		Path p = fileSystem.getPath(dir.toAbsolutePath().toString());
		try (Stream<Path> s = Files.list(p))
		{
			return s.collect(Collectors.toList());
		} catch (IOException e) {
			return new ArrayList<>();
		}
	}
	
}
