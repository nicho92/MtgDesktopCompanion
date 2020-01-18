package org.magic.api.fs.impl;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.interfaces.abstracts.AbstractFileStorage;
import org.magic.services.MTGConstants;

public class ZipFileSystemStorage extends AbstractFileStorage {

	
	
	
	@Override
	public void initFileSystem() throws IOException {
		Map<String, Object> env = new HashMap<>();
		  env.put("create", "true");
		  env.put("capacity", "16G");
		  env.put("blockSize", "4k");
		  
		fs = FileSystems.newFileSystem(getFile("ROOT").toPath(),env);
	}

	@Override
	public String getName() {
		return "Zip";
	}
	
	
	@Override
	public void initDefault() {
		setProperty("ROOT", Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "ged.zip").toAbsolutePath().toString());
	}

}
