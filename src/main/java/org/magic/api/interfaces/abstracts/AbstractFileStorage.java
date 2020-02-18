package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.magic.api.interfaces.MTGGedStorage;

public abstract class AbstractFileStorage extends AbstractMTGPlugin implements MTGGedStorage {

	protected FileSystem fs;
	public abstract void initFileSystem() throws IOException;

	
	
	@Override
	public Path getRoot() {
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


}
