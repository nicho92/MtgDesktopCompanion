package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.magic.api.interfaces.MTGGedStorage;
import org.magic.services.MTGConstants;

public abstract class AbstractFileStorage extends AbstractMTGPlugin implements MTGGedStorage {

	protected FileSystem fs;
	
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

	public AbstractFileStorage() {
		super();
		confdir = new File(MTGConstants.CONF_DIR, "ged");
		if (!confdir.exists())
			confdir.mkdir();
		load();

		if (!new File(confdir, getName() + ".conf").exists()) {
			initDefault();
			save();
		}
	}
	

}
