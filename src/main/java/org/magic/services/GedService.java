package org.magic.services;

import java.io.File;
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
	}
	
	public List<Path> list(Path dir)
	{
		try (Stream<Path> s = Files.list(dir))
		{
			return s.collect(Collectors.toList());
		} catch (IOException e) {
			return new ArrayList<>();
		}
		
	}
	
	private void printRoot()
	{
		fileSystem.getRootDirectories().forEach(r->{
			try {
				Files.list(r).forEach(p2->{
					logger.info(p2);
				});
			} catch (IOException e) {
				logger.error(e);
			}
		});
		
	}
	
	
	
	
	public static void main(String[] args) throws IOException {
		
		GedService.inst().store(new GedEntry<>(new File("D:\\Téléchargements\\node-v12.14.1-win-x64.zip")));
		GedService.inst().printRoot();
		
		MTGControler.getInstance().closeApp();
	}
	
	
}
